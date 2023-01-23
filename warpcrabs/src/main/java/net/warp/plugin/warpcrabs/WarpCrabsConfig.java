package net.warp.plugin.warpcrabs;

import net.runelite.client.config.*;

@ConfigGroup("warpcrabs")
public interface WarpCrabsConfig extends Config
{
    @ConfigSection(
            name = "Location",
            description = "Settings",
            position = 0
    )
    String location = "Location";
    @ConfigSection(
            name = "Food",
            description = "Food",
            position = 1
    )
    String food = "Food";
    @ConfigSection(
            name = "High Alch",
            description = "Magic settings",
            position = 2
    )
    String highAlch = "High Alch";
    @ConfigSection(
            name = "Looting",
            description = "Looting settings",
            position = 4
    )
    String looting = "Looting";
    @ConfigItem(
            keyName = "location",
            name = "Location",
            description = "What location",
            position = 0,
            section = location
    )
    default Locations location()
    {
        return Locations.SANDCRAB1;
    }

    @ConfigItem(
            keyName = "crabRadius",
            name = "Crab radius: ",
            description = "Radius to find Crabs",
            position = 1,
            section = location
    )
    default int crabRadius()
    {
        return 6;
    }

    @ConfigItem(
            keyName = "rockRadius",
            name = "Rock radius: ",
            description = "Radius to find Rocks",
            position = 2,
            section = location
    )
    default int rockRadius()
    {
        return 8;
    }

    @ConfigItem(
            keyName = "eatFood",
            name = "Eat food: ",
            description = "Eat food?",
            position = 0,
            section = food
    )
    default boolean eatFood()
    {
        return false;
    }

    @Range(max = 100)
    @ConfigItem(
            keyName = "eatHealthPercent",
            name = "Health %",
            description = "Health % to eat at",
            position = 1,
            section = food
    )
    default int healthPercent()
    {
        return 65;
    }

    @ConfigItem(
            keyName = "foodName",
            name = "Food name: ",
            description = "Name of the food to eat",
            position = 2,
            section = food
    )
    default String foodName()
    {
        return "Salmon";
    }

    @ConfigItem(
            keyName = "alchItems",
            name = "Alch: ",
            description = "High Alch items",
            position = 0,
            section = highAlch
    )
    default boolean highAlch()
    {
        return false;
    }

    @ConfigItem(
            keyName = "alchItem",
            name = "Item to Alch: ",
            description = "High Alch item",
            position = 1,
            section = highAlch
    )
    default String alchItem()
    {
        return "Rune full helm";
    }

    @ConfigItem(
            keyName = "lootItems",
            name = "Loot Items: ",
            description = "Wanna Loot?",
            position = 0,
            section = looting
    )
    default boolean lootItems()
    {
        return false;
    }
    @ConfigItem(
            keyName = "lootItem",
            name = "Items to loot: ",
            description = "Separate by ,",
            position = 1,
            section = looting
    )
    default String lootItem()
    {
        return "Coins,Seaweed";
    }
}
