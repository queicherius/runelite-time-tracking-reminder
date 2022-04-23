package com.timetrackingreminder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("timetrackingreminder")
public interface TimeTrackingReminderConfig extends Config {
    @ConfigItem(
            keyName = "birdhouses",
            name = "Bird Houses",
            description = "Show an infobox when your Bird Houses are ready."
    )
    default boolean birdhouses() {
        return true;
    }
}