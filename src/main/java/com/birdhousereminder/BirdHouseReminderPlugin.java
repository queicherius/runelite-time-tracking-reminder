package com.birdhousereminder;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseTracker;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
        name = "Bird House Reminder"
)
public class BirdHouseReminderPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private BirdHouseTracker birdHouseTracker;

    private BirdHouseReminderInfoBox infoBox;

    @Override
    protected void startUp() throws Exception {
        log.info("Bird House Reminder started!");
        this.showInfoBox();
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Bird House Reminder stopped!");
        this.hideInfoBox();
    }

    @Subscribe
    public void onGameTick(GameTick t) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        SummaryState summary = birdHouseTracker.getSummary();

        if (summary == SummaryState.IN_PROGRESS) {
            hideInfoBox();
        } else {
            showInfoBox();
        }
    }

    private void showInfoBox() {
        if (infoBox != null) {
            return;
        }

        final BufferedImage image = itemManager.getImage(22192);
        infoBox = new BirdHouseReminderInfoBox(this, image);
        infoBoxManager.addInfoBox(infoBox);
    }

    private void hideInfoBox() {
        if (infoBox == null) {
            return;
        }

        infoBoxManager.removeInfoBox(infoBox);
        infoBox = null;
    }

    @Provides
    BirdHouseReminderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BirdHouseReminderConfig.class);
    }

    // HACK No idea if this is how you're supposed to do it, but this is needed
    // so we can @Inject the BirdHouseTracker singleton.
    @Provides
    TimeTrackingConfig provideTimeTrackingConfig(ConfigManager configManager) {
        return configManager.getConfig(TimeTrackingConfig.class);
    }
}
