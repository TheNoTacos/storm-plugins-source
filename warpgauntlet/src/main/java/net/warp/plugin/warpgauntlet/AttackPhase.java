package net.warp.plugin.warpgauntlet;

import lombok.Getter;
import net.runelite.api.Prayer;


@Getter
public enum AttackPhase
{
    MAGIC(Prayer.PROTECT_FROM_MAGIC),
    RANGE(Prayer.PROTECT_FROM_MISSILES);

    private final Prayer prayerType;

    AttackPhase(Prayer prayerType)
    {
        this.prayerType = prayerType;
    }
}