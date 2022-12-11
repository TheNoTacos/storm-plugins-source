package net.warp.plugin.warpmasterthieving;

import net.runelite.client.config.*;

@ConfigGroup("warpmasterthieving")
public interface WarpMasterThieverConfig extends Config {
    @ConfigSection(
            name = "Warp Thieving",
            description = "Thieving settings",
            position = 0
    )
    String general = "Warp Thieving";
    @ConfigItem(
            keyName = "eat",
            name = "Eat food",
            description = "Eat food to heal",
            position = 0,
            section = general
    )
    default boolean eat()
    {
        return true;
    }

    @Range(max = 100)
    @ConfigItem(
            keyName = "eatHealthPercent",
            name = "Health %",
            description = "Health % to eat at",
            position = 1,
            section = general
    )
    default int healthPercent()
    {
        return 65;
    }

    @ConfigItem(
            keyName = "foodName",
            name = "Food name:",
            description = "Name of the food..",
            position = 2,
            section = general
    )
    default String foodName() { return "Cake"; }

    @ConfigItem(
            keyName = "foodAmount",
            name = "Amount:",
            description = "Amount of food to take",
            position = 3,
            section = general
    )
    default int foodAmount() { return 5; }

    @ConfigItem(
            keyName = "seedToDrop",
            name = "Seeds to drop: ",
            description = "Itemnames to drop",
            position = 4,
            section = general
    )
    default String seedToDrop() { return "Hammerstone seed,"; }
}
