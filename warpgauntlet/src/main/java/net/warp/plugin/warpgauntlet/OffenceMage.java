package net.warp.plugin.warpgauntlet;

import lombok.Getter;
import net.runelite.api.Prayer;

@Getter
public enum OffenceMage
{
    MYSTIC_MIGHT("Mystic Might", Prayer.MYSTIC_MIGHT),
    AUGURY("Augury", Prayer.AUGURY);

    private final String prayerName;
    private final Prayer prayerType;

    OffenceMage(String prayerName, Prayer prayerType)
    {
        this.prayerName = prayerName;
        this.prayerType = prayerType;
    }
}
