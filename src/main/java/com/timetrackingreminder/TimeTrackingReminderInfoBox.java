package com.timetrackingreminder;

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class TimeTrackingReminderInfoBox extends InfoBox {
    private final Plugin plugin;
    private final String tooltip;

    TimeTrackingReminderInfoBox(Plugin plugin, BufferedImage image, String tooltip) {
        super(image, plugin);
        this.plugin = plugin;
        this.tooltip = tooltip;
    }

    @Override
    public String getName() {
        String id = tooltip.toLowerCase().replaceAll("\\W+", "_");
        return plugin.getClass().getSimpleName() + "_" + getClass().getSimpleName() + "_" + id;
    }

    @Override
    public String getText() {
        return "Ready";
    }

    @Override
    public Color getTextColor() {
        return Color.GREEN;
    }

    @Override
    public String getTooltip() {
        return tooltip;
    }
}
