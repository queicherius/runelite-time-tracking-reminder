package com.timetrackingreminder;

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class TimeTrackingReminderInfoBox extends InfoBox {
    private final Plugin plugin;
    private final String type;

    TimeTrackingReminderInfoBox(Plugin plugin, BufferedImage image, String type) {
        super(image, plugin);
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public String getName() {
        String typeString = type.toLowerCase().replaceAll("\\s+", "");
        return plugin.getClass().getSimpleName() + "_" + getClass().getSimpleName() + "_" + typeString;
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
        return "Your " + type + " are ready.";
    }
}
