package com.timetrackingreminder;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.timetrackingreminder.runelite.farming.CompostTracker;
import com.timetrackingreminder.runelite.farming.FarmingContractManager;
import com.timetrackingreminder.runelite.farming.FarmingWorld;
import com.timetrackingreminder.runelite.hunter.BirdHouseTracker;
import com.timetrackingreminder.runelite.farming.FarmingTracker;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
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
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

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
                        "Bird Houses",
                        21515, // Oak bird house
                        () -> config.birdHouses() && birdHouseTracker.getSummary() != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Herb Patches",
                        207, // Grimy ranarr weed
                        () -> config.herbPatches() && farmingTracker.getSummary(Tab.HERB) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Tree Patches",
                        1515, // Yew logs
                        () -> config.treePatches() && farmingTracker.getSummary(Tab.TREE) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Fruit Tree Patches",
                        2114, // Pineapple
                        () -> config.fruitTreePatches() && farmingTracker.getSummary(Tab.FRUIT_TREE) != SummaryState.IN_PROGRESS
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
}
