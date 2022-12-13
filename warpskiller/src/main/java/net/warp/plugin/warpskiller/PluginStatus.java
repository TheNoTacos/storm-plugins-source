package net.warp.plugin.warpskiller;

import lombok.Getter;

@Getter
public enum PluginStatus
{
    IDLE("Idle"),
    ALCH("Alching"),
    SUPERHEAT("Casting Superheat ore"),
    PLANK_MAKE("Casting Plank make"),
    GLASS_MAKE("Casting Superheat Glass"),
    BANK("Banking"),
    GEMCUTTING("Cutting Gems"),
    GLASSBLOW("Blowing glass"),
    AMETHYST("Cutting Amethyst"),
    CLEANHERBS("Cleaning Herbs"),
    POTIONMAKING("Making potions"),
    FLETCHBOWS("Fletching Bows (u)"),
    SETUP("Setting up");
    private final String status;
    PluginStatus(String status)
    {
        this.status = status;
    }
}