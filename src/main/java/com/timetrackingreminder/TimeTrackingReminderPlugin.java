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
                compostTracker
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
                        infoBoxManager,
                        itemManager,
                        "Your bird houses are ready.",
                        21515, // Oak bird house
                        () -> config.birdHouses() && showInfoboxInInstance() && birdHouseTracker.getSummary() != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your herb patches are ready.",
                        207, // Grimy ranarr weed
                        () -> config.herbPatches() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.HERB) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your tree patches are ready.",
                        1515, // Yew logs
                        () -> config.treePatches() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.TREE) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your fruit tree patches are ready.",
                        2114, // Pineapple
                        () -> config.fruitTreePatches() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.FRUIT_TREE) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your seaweed patches are ready.",
                        21504, // Giant seaweed
                        () -> config.seaweedPatches() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.SEAWEED) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your bush patches are ready.",
                        239, // White berries
                        () -> config.bushPatches() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.BUSH) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your farming contract is ready.",
                        22993, // Seed pack
                        () -> config.farmingContract() && showInfoboxInInstance() && farmingContractManager.getSummary() != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your Hespori patch is ready.",
                        20661, // Tangleroot
                        () -> config.hespori() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.HESPORI) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your giant compost bin is ready.",
                        21483, // Ultracompost
                        () -> config.giantCompostBin() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.GIANT_COMPOST) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your calquat patch is ready.",
                        5980, // Calquat fruit
                        () -> config.calquatPatch() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.CALQUAT) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your hardwood patches are ready.",
                        6333, // Teak logs
                        () -> config.hardwoodPatches() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.HARDWOOD) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your hops patches are ready.",
                        6006, // Barley
                        () -> config.hopsPatches() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.HOPS) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your cactus patch is ready.",
                        3138, // Potato cactus
                        () -> config.cactusPatch() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.CACTUS) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your redwood patch is ready.",
                        19669, // Redwood log
                        () -> config.redwoodPatch() && showInfoboxInInstance() && farmingTracker.getSummary(Tab.REDWOOD) != SummaryState.IN_PROGRESS
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
        if (!config.showInInstances() && client.isInInstancedRegion()){
            return false;
        }

        return true;
    }
}
