package crazypants.enderio;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import crazypants.vecmath.VecmathUtil;

public final class Config {

  static int BID = 700;
  static int IID = 8524;

  public static final double DEFAULT_CONDUIT_SCALE = 0.75;

  public static boolean useAlternateBinderRecipe;

  public static boolean useAlternateTesseractModel;

  public static boolean photovoltaicCellEnabled = true;

  public static double conduitScale = DEFAULT_CONDUIT_SCALE;

  public static int numConduitsPerRecipe = 8;

  public static double transceiverEnergyLoss = 0.1;

  public static double transceiverUpkeepCost = 0.25;

  public static double transceiverBucketTransmissionCost = 1;

  public static int transceiverMaxIO = 256;

  public static File configDirectory;

  public static boolean useHardRecipes = false;

  public static boolean detailedPowerTrackingEnabled = false;

  public static double maxPhotovoltaicOutput = 1.0;

  public static boolean useSneakMouseWheelYetaWrench = true;

  public static boolean useSneakRightClickYetaWrench = false;

  public static boolean useRfAsDefault = true;

  public static boolean itemConduitUsePhyscialDistance = false;

  public static int advancedFluidConduitExtractRate = 100;

  public static int advancedFluidConduitMaxIoRate = 400;

  public static int fluidConduitExtractRate = 50;

  public static int fluidConduitMaxIoRate = 200;

  public static boolean renderCapBankGauge = true;

  public static boolean renderCapBankGaugeBackground = true;

  public static boolean renderCapBankGaugeLevel = true;

  public static boolean updateLightingWhenHidingFacades = false;

  public static boolean travelAnchorEnabled = true;
  public static int travelAnchorMaxDistance = 48;

  public static int travelStaffMaxDistance = 96;
  public static float travelStaffPowerPerBlockRF = 250;

  public static int travelStaffMaxBlinkDistance = 16;
  public static int travelStaffBlinkPauseTicks = 10;

  public static boolean travelStaffEnabled = true;
  public static boolean travelStaffBlinkEnabled = true;
  public static boolean travelStaffBlinkThroughSolidBlocksEnabled = true;
  public static boolean travelStaffBlinkThroughClearBlocksEnabled = true;

  public static int enderIoRange = 8;
  public static boolean enderIoMeAccessEnabled = true;

  public static int darkSteelPowerStorageBase = 100000;
  public static int darkSteelPowerStorageLevelOne = 150000;
  public static int darkSteelPowerStorageLevelTwo = 250000;
  public static int darkSteelPowerStorageLevelThree = 500000;

  public static float darkSteelSpeedOneWalkModifier = 0.1f;
  public static float darkSteelSpeedTwoWalkMultiplier = 0.2f;
  public static float darkSteelSpeedThreeWalkMultiplier = 0.3f;

  public static float darkSteelSpeedOneSprintModifier = 0.1f;
  public static float darkSteelSpeedTwoSprintMultiplier= 0.3f;
  public static float darkSteelSpeedThreeSprintMultiplier = 0.5f;

  public static double darkSteelBootsJumpModifier = 1.5;

  public static int darkSteelWalkPowerCost = darkSteelPowerStorageLevelTwo / 3000;
  public static int darkSteelSprintPowerCost = darkSteelWalkPowerCost * 4;
  public static boolean darkSteelDrainPowerFromInventory = false;
  public static int darkSteelBootsJumpPowerCost = 250;

  public static float darkSteelSwordWitherSkullChance = 0.05f;
  public static float darkSteelSwordWitherSkullLootingModifier = 0.167f / 3f; //at looting 3, have a 1 in 6 chance of getting a skull
  public static float darkSteelSwordSkullChance = 0.2f;
  public static float darkSteelSwordSkullLootingModifier = 0.15f;
  public static float vanillaSwordSkullLootingModifier = 0.1f;
  public static int darkSteelSwordPowerUsePerHit = 750;
  public static double darkSteelSwordEnderPearlDropChance = 1;
  public static double darkSteelSwordEnderPearlDropChancePerLooting = 0.5;

  public static int darkSteelPickPowerUseObsidian = 10000;
  public static int darkSteelPickEffeciencyObsidian = 50;
  public static int darkSteelPickPowerUsePerDamagePoint = 750;
  public static float darkSteelPickEffeciencyBoostWhenPowered = 2;

  public static int hootchPowerPerCycle = 6;
  public static int hootchPowerTotalBurnTime = 4000;
  public static int rocketFuelPowerPerCycle = 16;
  public static int rocketFuelPowerTotalBurnTime = 7000;
  public static int fireWaterPowerPerCycle = 8;
  public static int fireWaterPowerTotalBurnTime = 15000;
  public static float vatPowerUserPerTick = 2;

  public static boolean addFuelTooltipsToAllFluidContainers = true;
  public static boolean addFurnaceFuelTootip = true;

  public static int darkSteelUpgradeVibrantCost = 20;
  public static int darkSteelUpgradePowerOneCost = 10;
  public static int darkSteelUpgradePowerTwoCost = 20;
  public static int darkSteelUpgradePowerThreeCost = 30;

  public static void load(FMLPreInitializationEvent event) {
    configDirectory = new File(event.getModConfigurationDirectory(), "enderio");
    if(!configDirectory.exists()) {
      configDirectory.mkdir();
    }

    File configFile = new File(configDirectory, "EnderIO.cfg");
    Configuration cfg = new Configuration(configFile);
    try {
      cfg.load();
      Config.processConfig(cfg);
    } catch (Exception e) {
      Log.error("EnderIO has a problem loading it's configuration");
    } finally {
      if(cfg.hasChanged()) {
        cfg.save();
      }
    }
  }

  public static void processConfig(Configuration config) {
    useRfAsDefault = config.get("Settings", "displayPowerAsRedstoneFlux", useRfAsDefault, "If true, all power is displayed in RF, otherwise MJ is used.")
        .getBoolean(useRfAsDefault);

    useHardRecipes = config.get("Settings", "useHardRecipes", useHardRecipes, "When enabled machines cost significantly more.")
        .getBoolean(useHardRecipes);

    numConduitsPerRecipe = config.get("Settings", "numConduitsPerRecipe", numConduitsPerRecipe,
        "The number of conduits crafted per recipe.").getInt(numConduitsPerRecipe);

    photovoltaicCellEnabled = config.get("Settings", "photovoltaicCellEnabled", photovoltaicCellEnabled,
        "If set to false Photovoltaic Cells will not be craftable.")
        .getBoolean(photovoltaicCellEnabled);

    maxPhotovoltaicOutput = config.get("Settings", "maxPhotovoltaicOutput", maxPhotovoltaicOutput,
        "Maximum output in MJ/t of the Photovoltaic Panels.").getDouble(maxPhotovoltaicOutput);

    useAlternateBinderRecipe = config.get("Settings", "useAlternateBinderRecipe", false, "Create conduit binder in crafting table instead of furnace")
        .getBoolean(useAlternateBinderRecipe);

    conduitScale = config.get("Settings", "conduitScale", DEFAULT_CONDUIT_SCALE,
        "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n" +
        "In SMP, all clients must be using the same value as the server.").getDouble(DEFAULT_CONDUIT_SCALE);
    conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);

    fluidConduitExtractRate = config.get("Settings", "fluidConduitExtractRate", fluidConduitExtractRate,
        "Number of millibuckects per tick extract by a fluid conduits auto extract..").getInt(fluidConduitExtractRate);

    fluidConduitMaxIoRate = config.get("Settings", "fluidConduitMaxIoRate", fluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to a fluid conduit.").getInt(fluidConduitMaxIoRate);

    advancedFluidConduitExtractRate = config.get("Settings", "advancedFluidConduitExtractRate", advancedFluidConduitExtractRate,
        "Number of millibuckects per tick extract by advanced fluid conduits auto extract..").getInt(advancedFluidConduitExtractRate);

    advancedFluidConduitMaxIoRate = config.get("Settings", "advancedFluidConduitMaxIoRate", advancedFluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to an advanced fluid conduit.").getInt(advancedFluidConduitMaxIoRate);

    useAlternateTesseractModel = config.get("Settings", "useAlternateTransceiverModel", useAlternateTesseractModel,
        "Use TheKazador's alternatice model for the Dimensional Transceiver")
        .getBoolean(false);
    transceiverEnergyLoss = config.get("Settings", "transceiverEnergyLoss", transceiverEnergyLoss,
        "Amount of energy lost when transfered by Dimensional Transceiver 0 is no loss, 1 is 100% loss").getDouble(transceiverEnergyLoss);
    transceiverUpkeepCost = config.get("Settings", "transceiverUpkeepCost", transceiverUpkeepCost,
        "Number of MJ/t required to keep a Dimensional Transceiver connection open").getDouble(transceiverUpkeepCost);
    transceiverMaxIO = config.get("Settings", "transceiverMaxIO", transceiverMaxIO,
        "Maximum MJ/t sent and recieved by a Dimensional Transceiver per tick. Input and output limits are not cumulative").getInt(transceiverMaxIO);
    transceiverBucketTransmissionCost = config.get("Settings", "transceiverBucketTransmissionCost", transceiverBucketTransmissionCost,
        "The cost in MJ of trasporting a bucket of fluid via a Dimensional Transceiver.").getDouble(transceiverBucketTransmissionCost);


    vatPowerUserPerTick = (float)config.get("Settings", "vatPowerUserPerTick", vatPowerUserPerTick,
        "Power use (MJ/t) used by the vat.").getDouble(vatPowerUserPerTick);

    detailedPowerTrackingEnabled = config
        .get(
            "Settings",
            "perInterfacePowerTrackingEnabled",
            detailedPowerTrackingEnabled,
            "Enable per tick sampling on individual power inputs and outputs. This allows slightly more detailed messages from the MJ Reader but has a negative impact on server performance.")
            .getBoolean(detailedPowerTrackingEnabled);

    useSneakMouseWheelYetaWrench = config.get("Settings", "useSneakMouseWheelYetaWrench", useSneakMouseWheelYetaWrench,
        "If true, shift-mouse wheel will change the conduit display mode when the YetaWrench is eqipped.")
        .getBoolean(useSneakMouseWheelYetaWrench);

    useSneakRightClickYetaWrench = config.get("Settings", "useSneakRightClickYetaWrench", useSneakRightClickYetaWrench,
        "If true, shift-clicking the YetaWrench on a null or non wrenchable object will change the conduit display mode.")
        .getBoolean(useSneakRightClickYetaWrench);

    itemConduitUsePhyscialDistance = config.get("Settings", "itemConduitUsePhyscialDistance", itemConduitUsePhyscialDistance, "If true, " +
        "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
        .getBoolean(itemConduitUsePhyscialDistance);

    itemConduitUsePhyscialDistance = config.get("Settings", "itemConduitUsePhyscialDistance", itemConduitUsePhyscialDistance, "If true, " +
        "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
        .getBoolean(itemConduitUsePhyscialDistance);

    if(!useSneakMouseWheelYetaWrench && !useSneakRightClickYetaWrench) {
      Log.warn("Both useSneakMouseWheelYetaWrench and useSneakRightClickYetaWrench are set to false. Enabling mouse wheel.");
      useSneakMouseWheelYetaWrench = true;
    }

    travelAnchorEnabled = config.get("Settings", "travelAnchorEnabled", travelAnchorEnabled,
        "When set to false the travel anchor will not be craftable.").getBoolean(travelAnchorEnabled);

    travelAnchorMaxDistance = config.get("Settings", "travelAnchorMaxDistance", travelAnchorMaxDistance,
        "Maximum number of blocks that can be traveled from one travel anchor to another.").getInt(travelAnchorMaxDistance);

    travelStaffMaxDistance = config.get("Settings", "travelStaffMaxDistance", travelStaffMaxDistance,
        "Maximum number of blocks that can be traveled using the Staff of the Traveling.").getInt(travelStaffMaxDistance);
    travelStaffPowerPerBlockRF = (float) config.get("Settings", "travelStaffPowerPerBlockRF", travelStaffPowerPerBlockRF,
        "Number of MJ required per block travelled using the Staff of the Traveling.").getDouble(travelStaffPowerPerBlockRF);

    travelStaffMaxBlinkDistance = config.get("Settings", "travelStaffMaxBlinkDistance", travelStaffMaxBlinkDistance,
        "Max number of blocks teleported when shift clicking the staff.").getInt(travelStaffMaxBlinkDistance);

    travelStaffBlinkPauseTicks = config.get("Settings", "travelStaffBlinkPauseTicks", travelStaffBlinkPauseTicks,
        "Minimum number of ticks between 'blinks'. Values of 10 or less allow a limited sort of flight.").getInt(travelStaffBlinkPauseTicks);

    travelStaffEnabled = config.get("Settings", "travelStaffEnabled", travelAnchorEnabled,
        "If set to false the travel staff will not be craftable.").getBoolean(travelStaffEnabled);
    travelStaffBlinkEnabled = config.get("Settings", "travelStaffBlinkEnabled", travelStaffBlinkEnabled,
        "If set to false the travel staff can not be used to shift-right click teleport, or blink.").getBoolean(travelStaffBlinkEnabled);
    travelStaffBlinkThroughSolidBlocksEnabled = config.get("Settings", "travelStaffBlinkThroughSolidBlocksEnabled", travelStaffBlinkThroughSolidBlocksEnabled,
        "If set to false the travel staff can be used to blink through any block.").getBoolean(travelStaffBlinkThroughSolidBlocksEnabled);
    travelStaffBlinkThroughClearBlocksEnabled = config
        .get("Settings", "travelStaffBlinkThroughClearBlocksEnabled", travelStaffBlinkThroughClearBlocksEnabled,
            "If travelStaffBlinkThroughSolidBlocksEnabled is set to false and this is true, the travel " +
                "staff can only be used to blink through transparent or partial blocks (e.g. torches). " +
            "If both are false, only air blocks may be teleported through.")
            .getBoolean(travelStaffBlinkThroughClearBlocksEnabled);

    enderIoRange = config.get("Settings", "enderIoRange", enderIoRange,
        "Range accessable (in blocks) when using the Ender IO.").getInt(enderIoRange);

    enderIoMeAccessEnabled = config.get("Settings", "enderIoMeAccessEnabled", enderIoMeAccessEnabled,
        "If fasle, you will not be able to access a ME acess or crafting terminal using the Ender IO.").getBoolean(enderIoMeAccessEnabled);

    updateLightingWhenHidingFacades = config.get("Settings", "updateLightingWhenHidingFacades", updateLightingWhenHidingFacades,
        "When true, correct lighting is recalculated (client side) for conduit bundles when transitioning to"
            + " from being hidden behind a facade. This produces "
            + "better quality rendering but can result in frame stutters when switching to/from a wrench.")
            .getBoolean(updateLightingWhenHidingFacades);


    darkSteelPowerStorageBase = config.get("Settings", "darkSteelPowerStorageBase", darkSteelPowerStorageBase,
        "Base amount of power stored by dark steel items.").getInt(darkSteelPowerStorageBase);
      darkSteelPowerStorageLevelOne = config.get("Settings", "darkSteelPowerStorageLevelOne", darkSteelPowerStorageLevelOne,
          "Amount of power stored by dark steel items with a level 1 upgrade.").getInt(darkSteelPowerStorageLevelOne);
      darkSteelPowerStorageLevelTwo = config.get("Settings", "darkSteelPowerStorageLevelTwo", darkSteelPowerStorageLevelTwo,
          "Amount of power stored by dark steel items with a level 2 upgrade.").getInt(darkSteelPowerStorageLevelTwo);
      darkSteelPowerStorageLevelThree = config.get("Settings", "darkSteelPowerStorageLevelThree", darkSteelPowerStorageLevelThree,
          "Amount of power stored by dark steel items with a level 3 upgrade.").getInt(darkSteelPowerStorageLevelThree);

      darkSteelUpgradeVibrantCost = config.get("Settings", "darkSteelUpgradeVibrantCost", darkSteelUpgradeVibrantCost,
          "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradeVibrantCost);
      darkSteelUpgradePowerOneCost = config.get("Settings", "darkSteelUpgradePowerOneCost", darkSteelUpgradePowerOneCost,
          "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradePowerOneCost);
      darkSteelUpgradePowerTwoCost = config.get("Settings", "darkSteelUpgradePowerTwoCost", darkSteelUpgradePowerTwoCost,
          "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradePowerTwoCost);
      darkSteelUpgradePowerThreeCost = config.get("Settings", "darkSteelUpgradePowerThreeCost", darkSteelUpgradePowerThreeCost,
          "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradePowerThreeCost);


//    darkSteelLeggingWalkModifier = config.get("Settings", "darkSteelLeggingWalkModifier", darkSteelLeggingWalkModifier,
//        "Speed modifier applied when walking in the Dark Steel Boots.").getDouble(darkSteelLeggingWalkModifier);
//    darkSteelLeggingSprintModifier = config.get("Settings", "darkSteelLeggingSprintModifier", darkSteelLeggingSprintModifier,
//        "Speed modifier applied when sprinting in the Dark Steel Boots.").getDouble(darkSteelLeggingSprintModifier);

    darkSteelSpeedOneWalkModifier = (float)config.get("Settings", "darkSteelSpeedOneWalkModifier", darkSteelSpeedOneWalkModifier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedOneWalkModifier);
    darkSteelSpeedTwoWalkMultiplier = (float)config.get("Settings", "darkSteelSpeedTwoWalkMultiplier", darkSteelSpeedTwoWalkMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedTwoWalkMultiplier);
    darkSteelSpeedThreeWalkMultiplier = (float)config.get("Settings", "darkSteelSpeedThreeWalkMultiplier", darkSteelSpeedThreeWalkMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedThreeWalkMultiplier);

    darkSteelSpeedOneSprintModifier = (float)config.get("Settings", "darkSteelSpeedOneSprintModifier", darkSteelSpeedOneSprintModifier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedOneSprintModifier);
    darkSteelSpeedTwoSprintMultiplier = (float)config.get("Settings", "darkSteelSpeedTwoSprintMultiplier", darkSteelSpeedTwoSprintMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedTwoSprintMultiplier);
    darkSteelSpeedThreeSprintMultiplier = (float)config.get("Settings", "darkSteelSpeedThreeSprintMultiplier", darkSteelSpeedThreeSprintMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedThreeSprintMultiplier);


    darkSteelBootsJumpModifier = config.get("Settings", "darkSteelBootsJumpModifier", darkSteelBootsJumpModifier,
        "Jump height modifier applied when jumping with Dark Steel Boots equipped").getDouble(darkSteelBootsJumpModifier);

    darkSteelPowerStorageBase = config.get("Settings", "darkSteelPowerStorage", darkSteelPowerStorageBase,
        "Amount of power stored (RF) per crystal in the armor items recipe.").getInt(darkSteelPowerStorageBase);
    darkSteelWalkPowerCost = config.get("Settings", "darkSteelWalkPowerCost", darkSteelWalkPowerCost,
        "Amount of power stored (RF) per block walked when wearing the dark steel boots.").getInt(darkSteelWalkPowerCost);
    darkSteelSprintPowerCost = config.get("Settings", "darkSteelSprintPowerCost", darkSteelWalkPowerCost,
        "Amount of power stored (RF) per block walked when wearing the dark stell boots.").getInt(darkSteelSprintPowerCost);
    darkSteelDrainPowerFromInventory = config.get("Settings", "darkSteelDrainPowerFromInventory", darkSteelDrainPowerFromInventory,
        "If true, drak steel armor will drain power stored (RF) in power containers in the players invenotry.").getBoolean(darkSteelDrainPowerFromInventory);

    darkSteelBootsJumpPowerCost = config.get("Settings", "darkSteelBootsJumpPowerCost", darkSteelBootsJumpPowerCost,
        "Base amount of power used per jump (RF) dark steel boots. The second jump in a 'double jump' uses 2x this etc").getInt(darkSteelBootsJumpPowerCost);

    darkSteelSwordSkullChance = (float) config.get("Settings", "darkSteelSwordSkullChance", darkSteelSwordSkullChance,
        "The base chance a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(darkSteelSwordSkullChance);
    darkSteelSwordSkullLootingModifier = (float) config.get("Settings", "darkSteelSwordSkullLootingModifier", darkSteelSwordSkullLootingModifier,
        "The chance per looting level that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
            darkSteelSwordSkullLootingModifier);
    darkSteelSwordWitherSkullChance = (float) config.get("Settings", "darkSteelSwordWitherSkullChance", darkSteelSwordWitherSkullChance,
        "The base chance a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
            darkSteelSwordWitherSkullChance);
    darkSteelSwordWitherSkullLootingModifier = (float) config.get("Settings", "darkSteelSwordWitherSkullLootingModifie",
        darkSteelSwordWitherSkullLootingModifier,
        "The chance per looting level that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
            darkSteelSwordWitherSkullLootingModifier);
    vanillaSwordSkullLootingModifier = (float) config.get("Settings", "vanillaSwordSkullLootingModifier", vanillaSwordSkullLootingModifier,
        "The chance per looting level that a skull will be dropped when using a non-dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
            vanillaSwordSkullLootingModifier);
    darkSteelSwordPowerUsePerHit = config.get("Settings", "darkSteelSwordPowerUsePerHit", darkSteelSwordPowerUsePerHit,
        "The amount of power (RF) used per hit.").getInt(darkSteelSwordPowerUsePerHit);
    darkSteelSwordEnderPearlDropChance = config.get("Settings", "darkSteelSwordEnderPearlDropChance", darkSteelSwordEnderPearlDropChance,
        "The chance that an ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
            darkSteelSwordEnderPearlDropChance);
    darkSteelSwordEnderPearlDropChancePerLooting = config.get("Settings", "darkSteelSwordEnderPearlDropChancePerLooting",
        darkSteelSwordEnderPearlDropChancePerLooting,
        "The chance for each looting level that an additional ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(
            darkSteelSwordEnderPearlDropChancePerLooting);

    darkSteelPickPowerUseObsidian = config.get("Settings", "darkSteelPickPowerUseObsidian", darkSteelPickPowerUseObsidian,
        "The amount of power (RF) used to break an obsidian block.").getInt(darkSteelPickPowerUseObsidian);
    darkSteelPickEffeciencyObsidian = config.get("Settings", "darkSteelPickEffeciencyObsidian", darkSteelPickEffeciencyObsidian,
        "The efeciency when breaking obsidian with a powered  Dark Pickaxe.").getInt(darkSteelPickEffeciencyObsidian);
    darkSteelPickPowerUsePerDamagePoint = config.get("Settings", "darkSteelPickPowerUsePerDamagePoint", darkSteelPickPowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelPickPowerUsePerDamagePoint);
    darkSteelPickEffeciencyBoostWhenPowered = (float) config.get("Settings", "darkSteelPickEffeciencyBoostWhenPowered",
        darkSteelPickEffeciencyBoostWhenPowered, "The increase in effciency when powered.").getDouble(darkSteelPickEffeciencyBoostWhenPowered);

    hootchPowerPerCycle = config.get("Settings", "hootchPowerPerCycle", hootchPowerPerCycle,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(hootchPowerPerCycle);
    hootchPowerTotalBurnTime = config.get("Settings", "hootchPowerTotalBurnTime", hootchPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(hootchPowerTotalBurnTime);

    rocketFuelPowerPerCycle = config.get("Settings", "rocketFuelPowerPerCycle", rocketFuelPowerPerCycle,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(rocketFuelPowerPerCycle);
    rocketFuelPowerTotalBurnTime = config.get("Settings", "rocketFuelPowerTotalBurnTime", rocketFuelPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(rocketFuelPowerTotalBurnTime);

    fireWaterPowerPerCycle = config.get("Settings", "fireWaterPowerPerCycle", fireWaterPowerPerCycle,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(fireWaterPowerPerCycle);
    fireWaterPowerTotalBurnTime = config.get("Settings", "fireWaterPowerTotalBurnTime", fireWaterPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(fireWaterPowerTotalBurnTime);

    addFuelTooltipsToAllFluidContainers = config.get("Settings", "addFuelTooltipsToAllFluidContainers", addFuelTooltipsToAllFluidContainers,
        "If true, the MJ/t and burn time of the fuel will be displayed in all tooltips for fluid containers with fuel.").getBoolean(addFuelTooltipsToAllFluidContainers);

    //TODO: Debug
    renderCapBankGauge = config.get("Debug", "renderCapBankGauge", renderCapBankGauge, "If not true capacitor banks will not render the level gauge at all.")
        .getBoolean(renderCapBankGauge);
    renderCapBankGaugeBackground = config.get("Debug", "renderCapBankGaugeBackground", renderCapBankGaugeBackground,
        "If not true capacitor banks will not render the level gauge background.")
        .getBoolean(renderCapBankGaugeBackground);
    renderCapBankGaugeLevel = config.get("Debug", "renderCapBankGaugeLevel", renderCapBankGaugeLevel,
        "If not true capacitor banks will not render the level on the gauge.")
        .getBoolean(renderCapBankGaugeLevel);

  }

  private Config() {
  }

}