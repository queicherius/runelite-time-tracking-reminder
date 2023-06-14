package com.timetrackingreminder;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public class TimeTrackingReminderGroup {
    private final Plugin plugin;
    private final TimeTrackingReminderConfig config;
    private final InfoBoxManager infoBoxManager;
    private final ItemManager itemManager;

    private final String tooltip;
    private final Integer itemId;
    private final Callable<Boolean> shouldShowInfoBoxCallable;

    private TimeTrackingReminderInfoBox infoBox;

    TimeTrackingReminderGroup(
            Plugin plugin,
            TimeTrackingReminderConfig config,
            InfoBoxManager infoBoxManager,
            ItemManager itemManager,
            String tooltip,
            Integer itemId,
            Callable<Boolean> shouldShowInfoBoxCallable
    ) {
        this.plugin = plugin;
        this.config = config;
        this.infoBoxManager = infoBoxManager;
        this.itemManager = itemManager;

        this.tooltip = tooltip;
        this.itemId = itemId;
        this.shouldShowInfoBoxCallable = shouldShowInfoBoxCallable;
    }

    public void onGameTick() {
        boolean shouldShowInfoBox = false;

        try {
            shouldShowInfoBox = shouldShowInfoBoxCallable.call();
        } catch (Exception e) {
            // Silently ignore.
        }

        if (shouldShowInfoBox) {
            showInfoBox();
        } else {
            hideInfoBox();
        }
    }

    public void showInfoBox() {
        if (infoBox != null) {
            return;
        }

        final BufferedImage itemImage = itemManager.getImage(itemId);
        infoBox = new TimeTrackingReminderInfoBox(plugin, config, itemImage, tooltip);
        infoBoxManager.addInfoBox(infoBox);
    }

    public void hideInfoBox() {
        if (infoBox == null) {
            return;
        }

        infoBoxManager.removeInfoBox(infoBox);
        infoBox = null;
    }
}
