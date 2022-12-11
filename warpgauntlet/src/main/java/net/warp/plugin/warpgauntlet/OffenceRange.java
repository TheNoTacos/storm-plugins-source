package net.warp.plugin.warpgauntlet;

import lombok.Getter;
import net.runelite.api.Prayer;


@Getter
public enum OffenceRange
{
    EAGLE_EYE("Eagle eye", Prayer.EAGLE_EYE),
    RIGOUR("Rigour", Prayer.RIGOUR);

    private final String prayerName;
    private final Prayer prayerType;
    OffenceRange(String prayerName, Prayer prayerType)
    {
        this.prayerName = prayerName;
        this.prayerType = prayerType;
    }
}
