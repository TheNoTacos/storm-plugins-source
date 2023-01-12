package net.warp.plugin.warpspinner;

import lombok.Getter;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Location
{
    LUMBRIDGE(new WorldPoint(3208, 3220, 2), new WorldPoint(3209, 3213, 1), 27291, 18491),
    SEERS(new WorldPoint(2724, 3493, 0), new WorldPoint(2711, 3471, 1), 25808, 27264, 25936, 27291, 18491, ObjectID.BANK_BOOTH_10083),
    CRAFTING_GUILD(new WorldPoint(2935, 3280, 0), new WorldPoint(2936, 3286, 1), 14886);

    private final WorldPoint bankLocation;
    private final WorldPoint spinLocation;
    private final int[] bankID;

    Location(WorldPoint bankLocation, WorldPoint spinLocation, int... bankID)
    {
        this.bankLocation = bankLocation;
        this.spinLocation = spinLocation;
        this.bankID = bankID;
    }
}