package net.warp.plugin.warpcrabs;

import lombok.Getter;
import net.unethicalite.api.magic.Spell;
import net.unethicalite.api.magic.SpellBook;

@Getter
public enum Alch
{
    LOW_ALCH(SpellBook.Standard.LOW_LEVEL_ALCHEMY, 21, 3),
    HIGH_ALCH(SpellBook.Standard.HIGH_LEVEL_ALCHEMY, 55, 5);

    private final Spell spell;
    private final int level;
    private final int tick;

    Alch(Spell spell, int level, int tick)
    {
        this.spell = spell;
        this.level = level;
        this.tick = tick;
    }

}
