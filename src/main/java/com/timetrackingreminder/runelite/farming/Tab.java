package com.timetrackingreminder.runelite.farming;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@RequiredArgsConstructor
@Getter
public enum Tab
{
    OVERVIEW("Overview", ItemID.OLD_NOTES),
    CLOCK("Timers & Stopwatches", ItemID.WATCH),
    BIRD_HOUSE("Bird Houses", ItemID.OAK_BIRD_HOUSE),
    ALLOTMENT("Allotment Patches", ItemID.CABBAGE),
    FLOWER("Flower Patches", ItemID.RED_FLOWERS),
    HERB("Herb Patches", ItemID.GRIMY_RANARR_WEED),
    HERB1("Herb Patches", ItemID.GRIMY_RANARR_WEED),
    TREE("Tree Patches", ItemID.YEW_LOGS),
    TREE1("Tree Patches", ItemID.YEW_LOGS),
    FRUIT_TREE("Fruit Tree Patches", ItemID.PINEAPPLE),
    FRUIT_TREE1("Fruit Tree Patches", ItemID.PINEAPPLE),
    HOPS("Hops Patches", ItemID.BARLEY),
    BUSH("Bush Patches", ItemID.POISON_IVY_BERRIES),
    BUSH1("Bush Patches", ItemID.POISON_IVY_BERRIES),
    GRAPE("Grape Patches", ItemID.GRAPES),
    SPECIAL("Special Patches", ItemID.MUSHROOM),
    COMPOST("Giant compost", ItemID.ULTRACOMPOST),
    SEAWEED("Sea weed", ItemID.GIANT_SEAWEED),
    CALQUAT("CALQUAT", ItemID.CALQUAT_FRUIT),
    HARDWOOD("HARDWOOD", ItemID.TEAK_LOGS),
    CACTUS("CACTUS", ItemID.POTATO_CACTUS),
    TIME_OFFSET("", ItemID.WATERING_CAN);

    public static final Tab[] FARMING_TABS = {HERB, TREE, FRUIT_TREE, SPECIAL, FLOWER, ALLOTMENT, BUSH, GRAPE, HOPS};

    private final String name;
    private final int itemID;
}