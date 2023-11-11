package com.timetrackingreminder;

import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.StringJoiner;

public class TimeTrackingReminderGroupInfoBox extends InfoBox {
    private final TimeTrackingReminderPlugin plugin;
    private final TimeTrackingReminderConfig config;

    TimeTrackingReminderGroupInfoBox(
            TimeTrackingReminderPlugin plugin,
            TimeTrackingReminderConfig config,
            BufferedImage image) {
        super(image, plugin);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public String getTooltip() {
        // The tooltip doesn't show newline characters so using comma instead
        StringJoiner tooltip = new StringJoiner(", ", "Your ", " are ready.");
        for (TimeTrackingReminderInfoBox box : plugin.getReminderInfoBoxes()) {
            if (box.getShouldShowInfoBox().getAsBoolean()) {
                tooltip.add(box.getShortName());
            }
        }
        return tooltip.toString();
    }

    @Override
    public String getText() {
        return config.customMessage();
    }

    @Override
    public Color getTextColor() {
        return Color.GREEN;
    }

    @Override
    public boolean render() {
        if (!plugin.showInfoboxInInstance() || !config.groupBoxesTogether()) {
            return false;
        }
        for (TimeTrackingReminderInfoBox box : plugin.getReminderInfoBoxes()) {
            if (box.getShouldShowInfoBox().getAsBoolean()) {
                return true;
            }
        }
        return false;
    }
}
