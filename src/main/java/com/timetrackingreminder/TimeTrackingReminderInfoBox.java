package com.timetrackingreminder;

import java.awt.*;
import java.awt.image.BufferedImage;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class TimeTrackingReminderInfoBox extends InfoBox {
    private final String type;

    TimeTrackingReminderInfoBox(Plugin plugin, BufferedImage image, String type) {
        super(image, plugin);
        this.type = type;
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
