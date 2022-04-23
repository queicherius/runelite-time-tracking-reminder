package com.timetrackingreminder;

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class TimeTrackingReminderInfoBox extends InfoBox {
    private final Plugin plugin;
    private final String name;

    TimeTrackingReminderInfoBox(Plugin plugin, BufferedImage image, String name) {
        super(image, plugin);
        this.plugin = plugin;
        this.name = name;
    }

    @Override
    public String getName() {
        String nameSlug = name.toLowerCase().replaceAll("\\s+", "_");
        return plugin.getClass().getSimpleName() + "_" + getClass().getSimpleName() + "_" + nameSlug;
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
        return "Your " + name + " are ready.";
    }
}
