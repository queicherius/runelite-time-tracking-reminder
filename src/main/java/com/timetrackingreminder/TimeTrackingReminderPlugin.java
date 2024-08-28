package com.timetrackingreminder;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.timetrackingreminder.runelite.farming.*;
import com.timetrackingreminder.runelite.hunter.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.awt.*;

@Slf4j
@PluginDescriptor(
        name = "Time Tracking Reminder",
        description = "Extend the \"Time Tracking\" plugin to show an infobox when bird houses or farming patches are ready."
)
public class TimeTrackingReminderPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private TimeTrackingReminderConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private Notifier notifier;

    @Inject
    private ItemManager itemManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    private BirdHouseTracker birdHouseTracker;
    private FarmingTracker farmingTracker;
    private FarmingContractManager farmingContractManager;

    @Getter
    private TimeTrackingReminderInfoBox[] reminderInfoBoxes;
    private TimeTrackingReminderGroupInfoBox groupInfoBox;

    @Provides
    TimeTrackingReminderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TimeTrackingReminderConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Time Tracking Reminder started!");
        initializeTrackers();
        initializeReminderGroups();
    }

    private void initializeTrackers() {
        TimeTrackingConfig timeTrackingConfig = configManager.getConfig(TimeTrackingConfig.class);
        FarmingWorld farmingWorld = new FarmingWorld();

        CompostTracker compostTracker = new CompostTracker(
                client,
                farmingWorld,
                configManager
        );

        PaymentTracker paymentTracker = new PaymentTracker(
                client,
                configManager,
                farmingWorld
        );

        birdHouseTracker = new BirdHouseTracker(
                client,
                itemManager,
                configManager,
                timeTrackingConfig,
                notifier
        );

        farmingTracker = new FarmingTracker(
                client,
                itemManager,
                configManager,
                timeTrackingConfig,
                farmingWorld,
                notifier,
                compostTracker,
                paymentTracker
        );
        farmingTracker.setIgnoreFarmingGuild(config.ignoreFarmingGuild());

        farmingContractManager = new FarmingContractManager(
                client,
                itemManager,
                configManager,
                timeTrackingConfig,
                farmingWorld,
                farmingTracker
        );
    }

    private void initializeReminderGroups() {
        reminderInfoBoxes = new TimeTrackingReminderInfoBox[] {
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "bird houses",
                        "Your bird houses are ready.",
                        itemManager.getImage(ItemID.OAK_BIRD_HOUSE),
                        () -> config.birdHouses() && birdHouseTracker.getSummary() != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "herb patches",
                        "Your herb patches are ready.",
                        itemManager.getImage(ItemID.GRIMY_RANARR_WEED),
                        () -> config.herbPatches() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.HERB) : farmingTracker.getSummary(Tab.HERB) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "allotment patches",
                        "Your allotment patches are ready.",
                        itemManager.getImage(ItemID.CABBAGE),
                        () -> config.allotmentPatches() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.ALLOTMENT) : farmingTracker.getSummary(Tab.ALLOTMENT) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "flower patches",
                        "Your flower patches are ready.",
                        itemManager.getImage(ItemID.RED_FLOWERS),
                        () -> config.flowerPatches() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.FLOWER) : farmingTracker.getSummary(Tab.FLOWER) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "tree patches",
                        "Your tree patches are ready.",
                        itemManager.getImage(ItemID.YEW_LOGS),
                        () -> config.treePatches() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.TREE) : farmingTracker.getSummary(Tab.TREE) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "fruit tree patches",
                        "Your fruit tree patches are ready.",
                        itemManager.getImage(ItemID.PINEAPPLE),
                        () -> config.fruitTreePatches() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.FRUIT_TREE) : farmingTracker.getSummary(Tab.FRUIT_TREE) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "seaweed patches",
                        "Your seaweed patches are ready.",
                        itemManager.getImage(ItemID.GIANT_SEAWEED),
                        () -> config.seaweedPatches() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.SEAWEED) : farmingTracker.getSummary(Tab.SEAWEED) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "bush patches",
                        "Your bush patches are ready.",
                        itemManager.getImage(ItemID.WHITE_BERRIES),
                        () -> config.bushPatches() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.BUSH) : farmingTracker.getSummary(Tab.BUSH) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "farming contract",
                        "Your farming contract is ready.",
                        itemManager.getImage(ItemID.SEED_PACK),
                        () -> {
                            boolean isInProgress = farmingContractManager.getSummary() == SummaryState.IN_PROGRESS;
                            boolean isOccupiedWrongSeed = farmingContractManager.getSummary() == SummaryState.OCCUPIED && farmingContractManager.getContractCropState() == null;
                            boolean farmingContractReady = !isInProgress && !isOccupiedWrongSeed;

                            return config.farmingContract() && farmingContractReady;
                        }
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "Hespori patch",
                        "Your Hespori patch is ready.",
                        itemManager.getImage(ItemID.TANGLEROOT),
                        () -> config.hespori() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.HESPORI) : farmingTracker.getSummary(Tab.HESPORI) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "giant compost bin",
                        "Your giant compost bin is ready.",
                        itemManager.getImage(ItemID.ULTRACOMPOST),
                        () -> config.giantCompostBin() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.BIG_COMPOST) : farmingTracker.getSummary(Tab.BIG_COMPOST) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "calquat patch",
                        "Your calquat patch is ready.",
                        itemManager.getImage(ItemID.CALQUAT_FRUIT),
                        () -> config.calquatPatch() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.CALQUAT) : farmingTracker.getSummary(Tab.CALQUAT) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "celastrus patch",
                        "Your celastrus patch is ready.",
                        itemManager.getImage(ItemID.CELASTRUS_BARK),
                        () -> config.celastrusPatch() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.CELASTRUS) : farmingTracker.getSummary(Tab.CELASTRUS) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "hardwood patches",
                        "Your hardwood patches are ready.",
                        itemManager.getImage(ItemID.TEAK_LOGS),
                        () -> config.hardwoodPatches() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.HARDWOOD) : farmingTracker.getSummary(Tab.HARDWOOD) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "hops patches",
                        "Your hops patches are ready.",
                        itemManager.getImage(ItemID.BARLEY),
                        () -> config.hopsPatches() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.HOPS) : farmingTracker.getSummary(Tab.HOPS) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "cactus patches",
                        "Your cactus patches are ready.",
                        itemManager.getImage(ItemID.POTATO_CACTUS),
                        () -> config.cactusPatches() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.CACTUS) : farmingTracker.getSummary(Tab.CACTUS) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "redwood patch",
                        "Your redwood patch is ready.",
                        itemManager.getImage(ItemID.REDWOOD_LOGS),
                        () -> config.redwoodPatch() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.REDWOOD) : farmingTracker.getSummary(Tab.REDWOOD) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "mushroom patch",
                        "Your mushroom patch is ready.",
                        itemManager.getImage(ItemID.MUSHROOM),
                        () -> config.mushroomPatch() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.MUSHROOM) : farmingTracker.getSummary(Tab.MUSHROOM) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "belladonna patch",
                        "Your belladonna patch is ready.",
                        itemManager.getImage(ItemID.NIGHTSHADE),
                        () -> config.belladonnaPatch() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.BELLADONNA) : farmingTracker.getSummary(Tab.BELLADONNA) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "Anima patch",
                        "Your anima patch is ready for replacement.",
                        itemManager.getImage(ItemID.ANIMAINFUSED_BARK),
                        () -> config.animaPatch() && farmingTracker.getSummary(Tab.ANIMA) != SummaryState.IN_PROGRESS,
                        "Dead",
                        Color.RED
                ),
                new TimeTrackingReminderInfoBox(
                        this,
                        config,
                        "Crystal patch",
                        "Your Crystal patch is ready.",
                        itemManager.getImage(ItemID.CRYSTAL_SHARD),
                        () -> config.crystalPatch() && (config.onlyHarvestable() ?
                                farmingTracker.getHarvestable(Tab.CRYSTAL) :
                                (farmingTracker.getSummary(Tab.CRYSTAL) != SummaryState.IN_PROGRESS &&
                                        farmingTracker.getSummary(Tab.CRYSTAL) != SummaryState.EMPTY)
                        )
                )
        };
        for (TimeTrackingReminderInfoBox infoBox : reminderInfoBoxes) {
            infoBoxManager.addInfoBox(infoBox);
        }

        groupInfoBox = new TimeTrackingReminderGroupInfoBox(
                    this,
                    config,
                    itemManager.getImage(ItemID.WATCH));
        infoBoxManager.addInfoBox(groupInfoBox);
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Time Tracking Reminder stopped!");

        for (TimeTrackingReminderInfoBox infoBox : reminderInfoBoxes) {
            infoBoxManager.removeInfoBox(infoBox);
        }
        infoBoxManager.removeInfoBox(groupInfoBox);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        birdHouseTracker.loadFromConfig();
        farmingTracker.loadCompletionTimes();
        farmingContractManager.loadContractFromConfig();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        String group = event.getGroup();
        if (!group.equals(TimeTrackingConfig.CONFIG_GROUP) && !group.equals(TimeTrackingReminderConfig.CONFIG_GROUP)) {
            return;
        }

        birdHouseTracker.loadFromConfig();
        farmingTracker.setIgnoreFarmingGuild(config.ignoreFarmingGuild());
        farmingTracker.loadCompletionTimes();
        farmingContractManager.loadContractFromConfig();
    }

    @Subscribe
    public void onGameTick(GameTick t) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        birdHouseTracker.updateCompletionTime();
        farmingTracker.updateCompletionTime();
        farmingContractManager.handleContractState();
    }

    public boolean showInfoboxInInstance() {
        if (!config.showInInstances() && client.isInInstancedRegion()) {
            return false;
        }

        return true;
    }
}
