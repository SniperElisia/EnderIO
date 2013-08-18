package crazypants.enderio.conduit;

import java.util.*;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.*;
import crazypants.enderio.conduit.geom.*;
import crazypants.enderio.power.IInternalPowerReceptor;

public interface IConduitBundle extends IInternalPowerReceptor, ITankContainer {

  TileEntity getEntity();

  //conduits
  
  boolean hasType(Class<? extends IConduit> type);
  
  <T extends IConduit> T getConduit(Class<T> type);
  
  void addConduit(IConduit conduit);
  
  void removeConduit(IConduit conduit);
  
  Collection<IConduit> getConduits();
  
  Offset getOffset(Class<? extends IConduit> type, ForgeDirection dir); 

  // connections

  Set<ForgeDirection> getConnections(Class<? extends IConduit> type);

  boolean containsConnection(Class<? extends IConduit> type, ForgeDirection west);

  Set<ForgeDirection> getAllConnections();

  boolean containsConnection(ForgeDirection dir);

  // events
  
  void onNeighborBlockChange(int blockId);

  void onBlockRemoved();

  // Facade
  
  boolean hasFacade();
  
  void setFacadeId(int blockID);
  
  int getFacadeId();
  
  void setFacadeMetadata(int meta);
  
  int getFacadeMetadata();

  List<CollidableComponent> getCollidableComponents();

  List<CollidableComponent> getConnectors();

  void dirty();  

}