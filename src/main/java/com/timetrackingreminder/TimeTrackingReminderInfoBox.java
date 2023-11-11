package com.timetrackingreminder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

import lombok.Getter;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class TimeTrackingReminderInfoBox extends InfoBox {
    private final TimeTrackingReminderPlugin plugin;
    private final TimeTrackingReminderConfig config;
    private final String tooltip;
    @Getter
    private final BooleanSupplier shouldShowInfoBox;
    @Getter
    private final String shortName;

    TimeTrackingReminderInfoBox(
            TimeTrackingReminderPlugin plugin,
            TimeTrackingReminderConfig config,
            String shortName,
            String tooltip,
            BufferedImage image,
            BooleanSupplier shouldShowInfoBox
    ) {
        super(image, plugin);
        this.plugin = plugin;
        this.config = config;
        this.shortName = shortName;
        this.tooltip = tooltip;
        this.shouldShowInfoBox = shouldShowInfoBox;
    }

    @Override
    public String getName() {
        String id = tooltip.toLowerCase().replaceAll("\\W+", "_");
        return plugin.getClass().getSimpleName() + "_" + getClass().getSimpleName() + "_" + id;
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
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public boolean render() {
        if (!plugin.showInfoboxInInstance() || config.groupBoxesTogether()) {
            return false;
        }
        return shouldShowInfoBox.getAsBoolean();
    }
}
