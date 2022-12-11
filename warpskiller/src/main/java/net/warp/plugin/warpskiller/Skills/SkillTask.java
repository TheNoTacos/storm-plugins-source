package net.warp.plugin.warpskiller.Skills;

import lombok.Getter;

@Getter
public enum SkillTask {
    SLEEP("Sleep"),
    CRAFTING("Crafting"),
    MAGIC("Magic"),
    FLETCHING("Fletching"),
    HERBLORE("Herblore");

    private final String taskName;
    SkillTask(String taskName)
    {
        this.taskName = taskName;
    }

}
