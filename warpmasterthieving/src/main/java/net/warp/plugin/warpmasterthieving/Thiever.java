package net.warp.plugin.warpmasterthieving;

import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Thiever
{
    MASTER_FARMER_DRAYNOR(new WorldArea(new WorldPoint(3078, 3252, 0), new WorldPoint(3083, 3248, 0)),
                            new WorldArea(new WorldPoint(3092, 3241, 0), new WorldPoint(3094, 3245, 0)),
                        5730),
    MASTER_FARMER_ARDOUGNE(new WorldArea(new WorldPoint(2629, 3356, 0), new WorldPoint(2645, 3367, 0)),
                            new WorldArea(new WorldPoint(2613, 3332, 0), new WorldPoint(2620, 3334, 0)),
                        5730),
    ARDOUGNE_KNIGHT(new WorldArea(new WorldPoint(2649, 3280, 0), new WorldPoint(2655, 3287, 0)),
            new WorldArea(new WorldPoint(2649, 3280, 0), new WorldPoint(2655, 3287, 0)),
            3297);

    private final WorldArea thievingArea;
    private final WorldArea bankLocation;
    private final int NPCID;

    Thiever(WorldArea thievingArea, WorldArea bankLocation, int NPCID)
    {
        this.thievingArea = thievingArea;
        this.bankLocation = bankLocation;
        this.NPCID = NPCID;
    }

}

