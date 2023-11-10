package com.timetrackingreminder;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.timetrackingreminder.runelite.farming.*;
import com.timetrackingreminder.runelite.hunter.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
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

import java.time.Instant;

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

    private TimeTrackingReminderGroup[] reminderGroups;

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
        reminderGroups = new TimeTrackingReminderGroup[]{
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your bird houses are ready.",
                        21515, // Oak bird house
                        () -> config.birdHouses() && showInfoboxInInstance() && birdHouseTracker.getSummary() != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your herb patches are ready.",
                        207, // Grimy ranarr weed
                        () -> config.herbPatches() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.HERB) : farmingTracker.getSummary(Tab.HERB) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your allotment patches are ready.",
                        1965, // Cabbage
                        () -> config.allotmentPatches() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.ALLOTMENT) : farmingTracker.getSummary(Tab.ALLOTMENT) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your tree patches are ready.",
                        1515, // Yew logs
                        () -> config.treePatches() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.TREE) : farmingTracker.getSummary(Tab.TREE) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your fruit tree patches are ready.",
                        2114, // Pineapple
                        () -> config.fruitTreePatches() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.FRUIT_TREE) : farmingTracker.getSummary(Tab.FRUIT_TREE) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your seaweed patches are ready.",
                        21504, // Giant seaweed
                        () -> config.seaweedPatches() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.SEAWEED) : farmingTracker.getSummary(Tab.SEAWEED) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your bush patches are ready.",
                        239, // White berries
                        () -> config.bushPatches() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.BUSH) : farmingTracker.getSummary(Tab.BUSH) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your farming contract is ready.",
                        22993, // Seed pack
                        () -> {
                            boolean isInProgress = farmingContractManager.getSummary() == SummaryState.IN_PROGRESS;
                            boolean isOccupiedWrongSeed = farmingContractManager.getSummary() == SummaryState.OCCUPIED && farmingContractManager.getContractCropState() == null;
                            boolean farmingContractReady = !isInProgress && !isOccupiedWrongSeed;

                            return config.farmingContract() && showInfoboxInInstance() && farmingContractReady;
                        }
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your Hespori patch is ready.",
                        20661, // Tangleroot
                        () -> config.hespori() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.HESPORI) : farmingTracker.getSummary(Tab.HESPORI) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your giant compost bin is ready.",
                        21483, // Ultracompost
                        () -> config.giantCompostBin() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.BIG_COMPOST) : farmingTracker.getSummary(Tab.BIG_COMPOST) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your calquat patch is ready.",
                        5980, // Calquat fruit
                        () -> config.calquatPatch() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.CALQUAT) : farmingTracker.getSummary(Tab.CALQUAT) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your hardwood patches are ready.",
                        6333, // Teak logs
                        () -> config.hardwoodPatches() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.HARDWOOD) : farmingTracker.getSummary(Tab.HARDWOOD) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your hops patches are ready.",
                        6006, // Barley
                        () -> config.hopsPatches() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.HOPS) : farmingTracker.getSummary(Tab.HOPS) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your cactus patches are ready.",
                        3138, // Potato cactus
                        () -> config.cactusPatches() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.CACTUS) : farmingTracker.getSummary(Tab.CACTUS) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your redwood patch is ready.",
                        19669, // Redwood log
                        () -> config.redwoodPatch() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.REDWOOD) : farmingTracker.getSummary(Tab.REDWOOD) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your mushroom patch is ready.",
                        6004, // Mushroom
                        () -> config.mushroomPatch() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.MUSHROOM) : farmingTracker.getSummary(Tab.MUSHROOM) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your belladonna patch is ready.",
                        27790, // Nightshade
                        () -> config.belladonnaPatch() && showInfoboxInInstance() && (config.onlyHarvestable() ? farmingTracker.getHarvestable(Tab.BELLADONNA) : farmingTracker.getSummary(Tab.BELLADONNA) != SummaryState.IN_PROGRESS)
                ),
                new TimeTrackingReminderGroup(
                        this,
                        config,
                        infoBoxManager,
                        itemManager,
                        "Your Crystal patch is ready.",
                        23962, // Crystal shard
                        () -> config.crystalPatch() && showInfoboxInInstance() && (config.onlyHarvestable() ? 
                                farmingTracker.getHarvestable(Tab.CRYSTAL) :
                                (farmingTracker.getSummary(Tab.CRYSTAL) != SummaryState.IN_PROGRESS && 
                                        farmingTracker.getSummary(Tab.CRYSTAL) != SummaryState.EMPTY)
                        )
                )
        };
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Time Tracking Reminder stopped!");

        for (TimeTrackingReminderGroup reminderGroup : reminderGroups) {
            reminderGroup.hideInfoBox();
        }
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
        if (!event.getGroup().equals(TimeTrackingConfig.CONFIG_GROUP)) {
            return;
        }

        birdHouseTracker.loadFromConfig();
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

        for (TimeTrackingReminderGroup reminderGroup : reminderGroups) {
            reminderGroup.onGameTick();
        }
    }

    private boolean showInfoboxInInstance() {
        if (!config.showInInstances() && client.isInInstancedRegion()) {
            return false;
        }

        return true;
    }
}
