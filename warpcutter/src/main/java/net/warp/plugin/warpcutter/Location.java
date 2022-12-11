package net.warp.plugin.warpcutter;

import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Location {
    POWERCHOP("Drop", null, null, Tree.TREE, Tree.OAK, Tree.WILLOW, Tree.MAPLE, Tree.YEW, Tree.MAGIC),
    DRAYNOR("Draynor", new WorldArea(3092, 3242, 2, 3, 0),
            new WorldArea(new WorldPoint(3084, 3227, 0), new WorldPoint(3089, 3237, 0)),
            Tree.WILLOW),
    PORT_SARIM("Port Sarim", new WorldArea(3093, 3243, 3, 3, 0),
            new WorldArea(new WorldPoint(3056, 3250, 0), new WorldPoint(3063, 3256, 0)),
            Tree.WILLOW),
    SEERS("Seers", new WorldArea(2724, 3493, 1, 1, 0),
            new WorldArea(new WorldPoint(2720, 3499, 0), new WorldPoint(2733, 3503, 0)),
            Tree.MAPLE),
    WOODCUTTING_GUILD("Guild", new WorldArea(1591, 3477, 2, 2, 0),
            new WorldArea(new WorldPoint(1577, 3481, 0), new WorldPoint(1598, 3493, 0)),
            Tree.MAGIC, Tree.YEW);

    private final String locationName;
    private final WorldArea bankArea;
    private final WorldArea treeArea;
    private final Tree[] treeChop;

    Location (String locationName, WorldArea bankArea, WorldArea treeArea, Tree... treeChop)
    {
        this.locationName = locationName;
        this.bankArea = bankArea;
        this.treeArea = treeArea;
        this.treeChop = treeChop;
    }



}
