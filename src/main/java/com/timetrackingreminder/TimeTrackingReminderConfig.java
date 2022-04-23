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
    default boolean birdHouses() {
        return true;
    }

    @ConfigItem(
            keyName = "herbpatches",
            name = "Herb Patches",
            description = "Show an infobox when your Herb Patches are ready."
    )
    default boolean herbPatches() {
        return true;
    }

    @ConfigItem(
            keyName = "treepatches",
            name = "Tree Patches",
            description = "Show an infobox when your Tree Patches are ready."
    )
    default boolean treePatches() {
        return true;
    }

    @ConfigItem(
            keyName = "fruittreepatches",
            name = "Fruit Tree Patches",
            description = "Show an infobox when your Fruit Tree Patches are ready."
    )
    default boolean fruitTreePatches() {
        return true;
    }
}