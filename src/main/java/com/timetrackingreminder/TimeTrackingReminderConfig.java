package com.timetrackingreminder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("timetrackingreminder")
public interface TimeTrackingReminderConfig extends Config {
    @ConfigSection(
            name = "Miscellaneous",
            description = "Settings for miscellaneous infoboxes",
            position = 100
    )
    String miscellaneousSection = "Miscellaneous";

    @ConfigSection(
            name = "Farming patches",
            description = "Settings for farming patch infoboxes",
            position = 200
    )
    String farmingPatchesSection = "Farming patches";

    // --- Generic plugin options ---

    @ConfigItem(
            keyName = "showininstances",
            name = "Show in instances",
            description = "Show/hide infoboxes in instances (such as raids).",
            position = 1
    )
    default boolean showInInstances() {
        return true;
    }

	@ConfigItem(
		keyName = "customOverlayMessage",
		name = "Custom Overlay Message",
		description = "Use a custom overlay message instead of the default 'Ready'",
		position = 2
	)
	default String customMessage() { return "Ready"; }

    // -- Miscellaneous infoboxes ---

    @ConfigItem(
            keyName = "birdhouses",
            name = "Bird houses",
            description = "Show an infobox when your bird houses are ready.",
            section = miscellaneousSection,
            position = 101
    )
    default boolean birdHouses() {
        return true;
    }

    @ConfigItem(
            keyName = "farmingcontract",
            name = "Farming contract",
            description = "Show an infobox when your farming contract is ready.",
            section = miscellaneousSection,
            position = 102
    )
    default boolean farmingContract() {
        return true;
    }

    @ConfigItem(
            keyName = "hespori",
            name = "Hespori",
            description = "Show an infobox when your Hespori patch is ready.",
            section = miscellaneousSection,
            position = 103
    )
    default boolean hespori() {
        return true;
    }

    @ConfigItem(
            keyName = "giantcompostbin",
            name = "Giant compost bin",
            description = "Show an infobox when your giant compost bin is ready.",
            section = miscellaneousSection,
            position = 104
    )
    default boolean giantCompostBin() {
        return true;
    }

    // -- Farming patch infoboxes ---

    @ConfigItem(
            keyName = "herbpatches",
            name = "Herb patches",
            description = "Show an infobox when your herb patches are ready.",
            section = farmingPatchesSection,
            position = 201
    )
    default boolean herbPatches() {
        return true;
    }

    @ConfigItem(
            keyName = "treepatches",
            name = "Tree patches",
            description = "Show an infobox when your tree patches are ready.",
            section = farmingPatchesSection,
            position = 202
    )
    default boolean treePatches() {
        return true;
    }

    @ConfigItem(
            keyName = "fruittreepatches",
            name = "Fruit tree patches",
            description = "Show an infobox when your fruit tree patches are ready.",
            section = farmingPatchesSection,
            position = 203
    )
    default boolean fruitTreePatches() {
        return true;
    }

    @ConfigItem(
            keyName = "hardwoodpatches",
            name = "Hardwood patches",
            description = "Show an infobox when your hardwood patches are ready.",
            section = farmingPatchesSection,
            position = 204
    )
    default boolean hardwoodPatches() {
        return true;
    }

    @ConfigItem(
            keyName = "calquatpatch",
            name = "Calquat patch",
            description = "Show an infobox when your calquat patch is ready.",
            section = farmingPatchesSection,
            position = 205
    )
    default boolean calquatPatch() {
        return true;
    }

    @ConfigItem(
            keyName = "redwoodpatch",
            name = "Redwood patch",
            description = "Show an infobox when your redwood patch is ready.",
            section = farmingPatchesSection,
            position = 206
    )
    default boolean redwoodPatch() {
        return true;
    }

    @ConfigItem(
            keyName = "seaweedpatches",
            name = "Seaweed patches",
            description = "Show an infobox when your seaweed patches are ready.",
            section = farmingPatchesSection,
            position = 207
    )
    default boolean seaweedPatches() {
        return true;
    }

    @ConfigItem(
            keyName = "hopspatches",
            name = "Hops patches",
            description = "Show an infobox when your hops patches are ready.",
            section = farmingPatchesSection,
            position = 208
    )
    default boolean hopsPatches() {
        return true;
    }

    @ConfigItem(
            keyName = "bushpatches",
            name = "Bush patches",
            description = "Show an infobox when your bush patches are ready.",
            section = farmingPatchesSection,
            position = 209
    )
    default boolean bushPatches() {
        return true;
    }

    @ConfigItem(
            keyName = "cactuspatches",
            name = "Cactus patches",
            description = "Show an infobox when your cactus patches are ready.",
            section = farmingPatchesSection,
            position = 210
    )
    default boolean cactusPatches() {
        return true;
    }

    @ConfigItem(
            keyName = "mushroompatch",
            name = "Mushroom patch",
            description = "Show an infobox when your mushroom patch is ready.",
            section = farmingPatchesSection,
            position = 211
    )
    default boolean mushroomPatch() {
        return true;
    }

    @ConfigItem(
            keyName = "belladonnapatch",
            name = "Belladonna patch",
            description = "Show an infobox when your belladonna patch is ready.",
            section = farmingPatchesSection,
            position = 212
    )
    default boolean belladonnaPatch() {
        return true;
    }

    @ConfigItem(
            keyName = "crystalpatch",
            name = "Crystal patch",
            description = "Show an infobox when your crystal patch is ready.",
            section = farmingPatchesSection,
            position = 213
    )
    default boolean crystalPatch() {
        return true;
    }
}