package com.birdhousereminder;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseTracker;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;

import java.lang.reflect.Field;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
        name = "Bird House Reminder",
        description = "Show an infobox when bird houses are ready."
)
public class BirdHouseReminderPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    private BirdHouseTracker birdHouseTracker;

    private BirdHouseReminderInfoBox infoBox;

    @Override
    protected void startUp() throws Exception {
        log.info("Bird House Reminder started!");
        injectBirdHouseTrackerInstance();
    }

    private void injectBirdHouseTrackerInstance() {
        TimeTrackingPlugin timeTrackingPlugin = null;

        for (Plugin plugin : pluginManager.getPlugins()) {
            if (plugin.getName().equals("Time Tracking")) {
                timeTrackingPlugin = (TimeTrackingPlugin) plugin;
            }
        }

        if (timeTrackingPlugin == null) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "'Bird House Reminder' plugin could not find instance of 'Time Tracking' plugin. Maybe it's not enabled?", null);
            return;
        }

        try {
            Field field = timeTrackingPlugin.getClass().getDeclaredField("birdHouseTracker");
            field.setAccessible(true);
            birdHouseTracker = (BirdHouseTracker) field.get(timeTrackingPlugin);
            log.info("Injected 'birdHouseTracker' via reflection");
        } catch (NoSuchFieldException e) {
            log.error("Could not find field 'birdHouseTracker' via reflection");
        } catch (IllegalAccessException e) {
            log.error("Could not access field 'birdHouseTracker' via reflection");
        }
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Bird House Reminder stopped!");
        hideInfoBox();
    }

    @Subscribe
    public void onGameTick(GameTick t) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (birdHouseTracker == null) {
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
}
