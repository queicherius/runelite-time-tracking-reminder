package com.timetrackingreminder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("timetrackingreminder")
public interface TimeTrackingReminderConfig extends Config {
    @ConfigItem(
            keyName = "birdhouses",
            name = "Bird houses",
            description = "Show an infobox when your bird houses are ready."
    )
    default boolean birdHouses() {
        return true;
    }

    @ConfigItem(
            keyName = "herbpatches",
            name = "Herb patches",
            description = "Show an infobox when your herb patches are ready."
    )
    default boolean herbPatches() {
        return true;
    }

    @ConfigItem(
            keyName = "treepatches",
            name = "Tree patches",
            description = "Show an infobox when your tree patches are ready."
    )
    default boolean treePatches() {
        return true;
    }

    @ConfigItem(
            keyName = "fruittreepatches",
            name = "Fruit tree patches",
            description = "Show an infobox when your fruit tree patches are ready."
    )
    default boolean fruitTreePatches() {
        return true;
    }

    @ConfigItem(
            keyName = "seaweedpatches",
            name = "Seaweed patches",
            description = "Show an infobox when your seaweed patches are ready."
    )
    default boolean seaweedPatches() {
        return true;
    }

    @ConfigItem(
            keyName = "bushpatches",
            name = "Bush patches",
            description = "Show an infobox when your bush patches are ready."
    )
    default boolean bushPatches() {
        return true;
    }

    @ConfigItem(
            keyName = "farmingcontract",
            name = "Farming contract",
            description = "Show an infobox when your farming contract is ready."
    )
    default boolean farmingContract() {
        return true;
    }

    @ConfigItem(
            keyName = "hespori",
            name = "hespori",
            description = "Show an infobox when your Hespori patch is ready."
    )
    default boolean hespori() {
        return true;
    }

    @ConfigItem(
            keyName = "showininstances",
            name = "Show in instances",
            description = "Show/hide infoboxes in instances (such as raids)."
    )
    default boolean showInInstances() {
        return true;
    }
}