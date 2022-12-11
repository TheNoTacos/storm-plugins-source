package net.warp.plugin.warpskiller.Skills;

import lombok.Getter;

@Getter
public enum Crafting {
    GEMCUTTING("Gem cutting"),
    GLASSBLOW("Blow glass"),
    AMETHYST("Amethyst");

    private final String craftName;

    Crafting (String craftName)
    {
        this.craftName = craftName;
    }
}
