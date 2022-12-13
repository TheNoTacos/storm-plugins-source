package net.warp.plugin.warpskiller.Skills;

import lombok.Getter;

@Getter
public enum Herblore
{
    CLEAN("Clean Herbs"),
    POTION("Potion");
    private final String taskName;

    Herblore (String taskName)
    {
        this.taskName = taskName;
    }
}
