package net.warp.plugin.warpskiller.Items;

import lombok.Getter;

@Getter
public enum Gems
{
    SAPPHIRE("sapphire"),
    EMERALD("emerald"),
    RUBY("ruby"),
    DIAMOND("diamond"),
    DRAGONSTONE("dragonstone");

    private final String gemName;

    Gems(String gemName)
    {
        this.gemName = gemName;
    }
}

