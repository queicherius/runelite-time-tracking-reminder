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
import net.runelite.client.plugins.timetracking.Tab;
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
                        "Your Bird Houses are ready.",
                        21515, // Oak bird house
                        () -> config.birdHouses() && birdHouseTracker.getSummary() != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your Herb Patches are ready.",
                        207, // Grimy ranarr weed
                        () -> config.herbPatches() && farmingTracker.getSummary(Tab.HERB) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your Tree Patches are ready.",
                        1515, // Yew logs
                        () -> config.treePatches() && farmingTracker.getSummary(Tab.TREE) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your Fruit Tree Patches are ready.",
                        2114, // Pineapple
                        () -> config.fruitTreePatches() && farmingTracker.getSummary(Tab.FRUIT_TREE) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your Seaweed Patches are ready.",
                        21504, // Giant seaweed
                        () -> config.seaweedPatches() && showSeaweedInfoBox()
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your Bush Patches are ready.",
                        239, // Whiteberry
                        () -> config.bushPatches() && farmingTracker.getSummary(Tab.BUSH) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your Farming Contract is ready.",
                        22993, // Seed pack
                        () -> config.farmingContract() && farmingContractManager.getSummary() != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Your Hespori Patch is ready.",
                        20661, // Tangleroot
                        () -> config.hespori() && showHesporiInfoBox()
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

    private boolean showSeaweedInfoBox() {
        FarmingRegion region = new FarmingRegion("Seaweed", 15008, true,
                new FarmingPatch("North", Varbits.FARMING_4771, PatchImplementation.SEAWEED),
                new FarmingPatch("South", Varbits.FARMING_4772, PatchImplementation.SEAWEED)
        );
        FarmingPatch patch0 = region.getPatches()[0];
        PatchPrediction prediction0 = farmingTracker.predictPatch(patch0);
        FarmingPatch patch1 = region.getPatches()[1];
        PatchPrediction prediction1 = farmingTracker.predictPatch(patch1);

        if (prediction0 == null) {
            return false;
        }
        if (prediction1 == null) {
            return false;
        }

        if ((prediction0.getProduce() != Produce.SEAWEED) && (prediction1.getProduce() != Produce.SEAWEED)) {
            return true;
        }

        if ((prediction0.getCropState() != CropState.GROWING) && (prediction1.getCropState() != CropState.GROWING)) {
            return true;
        }

        // If the state is "GROWING" check if it should be done by now
        long unixNow = Instant.now().getEpochSecond();
        if (prediction0.getDoneEstimate() <= prediction1.getDoneEstimate()){
            return prediction1.getDoneEstimate() <= unixNow;
        }else{
            return prediction0.getDoneEstimate() <= unixNow;
        }
    }

    private boolean showHesporiInfoBox() {
        FarmingRegion region = new FarmingRegion("Farming Guild", 5021, true,
                new FarmingPatch("Hespori", Varbits.FARMING_7908, PatchImplementation.HESPORI)
        );
        FarmingPatch patch = region.getPatches()[0];
        PatchPrediction prediction = farmingTracker.predictPatch(patch);

        if (prediction == null) {
            return false;
        }

        if (prediction.getProduce() != Produce.HESPORI) {
            return true;
        }

        if (prediction.getCropState() != CropState.GROWING) {
            return true;
        }

        // If the state is "GROWING" check if it should be done by now
        long unixNow = Instant.now().getEpochSecond();
        return prediction.getDoneEstimate() <= unixNow;
    }
}
