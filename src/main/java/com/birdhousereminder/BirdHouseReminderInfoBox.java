package com.birdhousereminder;

import java.awt.*;
import java.awt.image.BufferedImage;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class BirdHouseReminderInfoBox extends InfoBox {
    BirdHouseReminderInfoBox(Plugin plugin, BufferedImage image) {
        super(image, plugin);
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    @Override
    public String getTooltip() {
        return "Your bird houses are ready.";
    }
}
