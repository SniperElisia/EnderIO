package crazypants.enderio.machine;

import buildcraft.api.power.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.*;
import crazypants.enderio.power.*;

public abstract class AbstractMachineEntity extends TileEntity implements IInventory, IInternalPowerReceptor, IMachine {

  public short facing;

  // Client sync monitoring
  protected int ticksSinceSync = -1;
  protected boolean forceClientUpdate = true;
  protected boolean lastActive;
  protected float lastSyncPowerStored = -1;

  // Power 
  protected Capacitors capacitorType;

  // Used on the client as the power provided isn't sinked
  private float storedEnergy;

  protected ItemStack[] inventory;
  protected final int inventorySize;

  protected EnderPowerProvider powerHandler;
  
  protected RedstoneControlMode redstoneControlMode;

  protected boolean redstoneCheckPassed;

  public AbstractMachineEntity(int inventorySize) {
    this.inventorySize = inventorySize + 1; //plus one for capacitor ;
    facing = 3;
    capacitorType = Capacitors.BASIC_CAPACITOR;
    powerHandler = PowerHandlerUtil.createHandler(capacitorType.capacitor);
    
    inventory = new ItemStack[this.inventorySize];
    
    redstoneControlMode = RedstoneControlMode.IGNORE;
  }
  
  @Override
  public final boolean isStackValidForSlot(int i, ItemStack itemstack) {
    if(i == inventorySize - 1) {
      return itemstack.itemID == ModObject.itemBasicCapacitor.actualId && itemstack.getItemDamage() > 0;
    }
    return isMachineItemValidForSlot(i, itemstack);
  }
  
  protected abstract boolean isMachineItemValidForSlot(int i, ItemStack itemstack);
  public RedstoneControlMode getRedstoneControlMode() {
    return redstoneControlMode;
  }

  public void setRedstoneControlMode(RedstoneControlMode redstoneControlMode) {
    this.redstoneControlMode = redstoneControlMode;    
  }

  @Override
  public EnderPowerProvider getPowerHandler() {
    return powerHandler;
  }

  @Override
  public void applyPerdition() {
    // TODO Apply values derived capcitor
    
  }

  public short getFacing() {
    return facing;
  }

  public void setFacing(short facing) {
    this.facing = facing;
  }

  public abstract boolean isActive();

  public abstract float getProgress();

  public int getProgressScaled(int scale) {
    int result = (int) (getProgress() * scale);
    return result;
  }

  // --- Power
  // --------------------------------------------------------------------------------------

  public boolean hasPower() {
    boolean hasPower = powerHandler.getEnergyStored() > 0;
    return hasPower;
  }

  public ICapacitor getCapacitor() {
    return capacitorType.capacitor;
  }

  public int getEnergyStoredScaled(int scale) {
    // NB: called on the client so can't use the power provider
    return (int) (scale * (storedEnergy / capacitorType.capacitor.getMaxEnergyStored()));
  }
  
  public float getEnergyStored() {
    return storedEnergy;
  }  

 public void setCapacitor(Capacitors capacitorType) {
    this.capacitorType = capacitorType;
    PowerHandlerUtil.configure(powerHandler, capacitorType.capacitor);
    forceClientUpdate = true;
  }

  @Override
  public void setPowerProvider(IPowerProvider provider) {
    
  }

  @Override
  public IPowerProvider getPowerProvider() {
    return powerHandler;
  }

  @Override
  public void doWork() {   
  }

  @Override
  public int powerRequest(ForgeDirection from) {
    return (int)(powerHandler.getMaxEnergyStored() - powerHandler.getEnergyStored());
  }
  protected float getPowerUsePerTick() {
    return capacitorType.capacitor.getMaxEnergyExtracted();
  }
  
  // --- Process Loop
  // --------------------------------------------------------------------------

  @Override
  public void updateEntity() {

    if (worldObj == null) { // sanity check
      return;
    }

    if (worldObj.isRemote) {
      // check if the block on the client needs to update its texture
      if (isActive() != lastActive) {
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
      }
      lastActive = isActive();
      return;

    } // else is server, do all logic only on the server
    

    boolean requiresClientSync = false;
    if (forceClientUpdate) {
      // First update, send state to client
      forceClientUpdate = false;
      requiresClientSync = true;
    }

    redstoneCheckPassed = true;
    if(redstoneControlMode == RedstoneControlMode.ON) {
      int  powerLevel = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
      if(powerLevel < 1) {
        redstoneCheckPassed = false;
      }      
    } else if(redstoneControlMode == RedstoneControlMode.OFF) {      
      int  powerLevel = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
      if(powerLevel > 0) {
        redstoneCheckPassed = false;
      }      
    }
    
    requiresClientSync |= processTasks(redstoneCheckPassed);
    
    // Update if our power has changed by more than 1%
    requiresClientSync |= Math.abs(lastSyncPowerStored - powerHandler.getEnergyStored()) > powerHandler.getMaxEnergyStored() / 100;    

    if (requiresClientSync) {
      lastSyncPowerStored = powerHandler.getEnergyStored();
      // this will cause 'getPacketDescription()' to be called and its result
      // will be sent to the PacketHandler on the other end of
      // client/server connection
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      // And this will make sure our current tile entity state is saved
      worldObj.updateTileEntityChunkAndDoNothing(xCoord, yCoord, zCoord, this);
    }

  }

  protected abstract boolean processTasks(boolean redstoneCheckPassed);

  // ---- Tile Entity
  // ------------------------------------------------------------------------------

  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    facing = nbtRoot.getShort("facing");

    setCapacitor(Capacitors.values()[nbtRoot.getShort("capacitorType")]);    

    float storedEnergy = nbtRoot.getFloat("storedEnergy");
    powerHandler.setEnergy(storedEnergy);
    // For the client as provider is not saved to NBT
    this.storedEnergy = storedEnergy;
    
    // read in the inventories contents
    inventory = new ItemStack[inventorySize];
    NBTTagList itemList = nbtRoot.getTagList("Items");

    for (int i = 0; i < itemList.tagCount(); i++) {
      NBTTagCompound itemStack = (NBTTagCompound) itemList.tagAt(i);
      byte slot = itemStack.getByte("Slot");
      if (slot >= 0 && slot < inventory.length) {
        inventory[slot] = ItemStack.loadItemStackFromNBT(itemStack);
      }
    }
    
    int rsContr = nbtRoot.getInteger("redstoneControlMode");
    if(rsContr < 0 || rsContr >= RedstoneControlMode.values().length) {
      rsContr = 0;
    }
    redstoneControlMode = RedstoneControlMode.values()[rsContr];
    
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setShort("facing", facing);
    nbtRoot.setFloat("storedEnergy", powerHandler.getEnergyStored());
    nbtRoot.setShort("capacitorType", (short)capacitorType.ordinal());
    
    // write inventory list
    NBTTagList itemList = new NBTTagList();
    for (int i = 0; i < inventory.length; i++) {
      if (inventory[i] != null) {
        NBTTagCompound itemStackNBT = new NBTTagCompound();
        itemStackNBT.setByte("Slot", (byte) i);
        inventory[i].writeToNBT(itemStackNBT);
        itemList.appendTag(itemStackNBT);
      }
    }
    nbtRoot.setTag("Items", itemList);    
    
    nbtRoot.setInteger("redstoneControlMode", redstoneControlMode.ordinal());
  }

  // ---- Inventory
  // ------------------------------------------------------------------------------
  
  @Override
  public boolean isInvNameLocalized() {
    return false;
  }
  
  @Override
  public boolean isUseableByPlayer(EntityPlayer player) {
    if (worldObj == null) {
      return true;
    }
    if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this) {
      return false;
    }
    return player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
  }

  @Override
  public int getSizeInventory() {
    return inventorySize;
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return inventory[slot];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack fromStack = inventory[fromSlot];
    if (fromStack == null) {
      return null;
    }
    if (fromStack.stackSize <= amount) {
      inventory[fromSlot] = null;
      updateCapacitorFromSlot();
      return fromStack;
    }
    ItemStack result = new ItemStack(fromStack.itemID, amount, fromStack.getItemDamage());
    if (fromStack.stackTagCompound != null) {
      result.stackTagCompound = (NBTTagCompound) fromStack.stackTagCompound.copy();
    }
    fromStack.stackSize -= amount;
    return result;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    if (contents == null) {
      inventory[slot] = contents;
    } else {
      inventory[slot] = contents.copy();
    }

    if (contents != null && contents.stackSize > getInventoryStackLimit()) {
      contents.stackSize = getInventoryStackLimit();
    }
    
    if(slot == inventory.length - 1) {
      updateCapacitorFromSlot();      
    }
  }

  private void updateCapacitorFromSlot() {
    ItemStack contents = inventory[inventory.length - 1]; 
    if(contents == null) {
      setCapacitor(Capacitors.BASIC_CAPACITOR);
    } else {
      setCapacitor(Capacitors.values()[contents.getItemDamage()]);
    }    
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int i) {
    return null;
  }

  @Override
  public void openChest() {
  }

  @Override
  public void closeChest() {
  }

  public void onNeighborBlockChange(int blockId) {        
  }

}