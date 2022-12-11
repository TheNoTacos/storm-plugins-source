package net.warp.plugin.warpcutter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.util.Arrays;

@ConfigGroup("warpcutter")
public interface WarpCutterConfig extends Config
{
    @ConfigSection(
            name = "Warp woodcutter",
            description = "Settings",
            position = 0
    )
    String general = "Warp woodcutter";
    @ConfigItem(
            keyName = "birdNest",
            name = "Get Birdnests",
            description = "Pickup Birdnests?",
            position = 0,
            section = general
    )
    default boolean getNest() { return true; }
    @ConfigItem(
            keyName = "locationName",
            name = "Location",
            description = "Where to bank",
            position = 1,
            section = general
    )
    default Location locationName() { return Location.DRAYNOR; }
    @ConfigItem(
            keyName = "treeType",
            name = "Tree type",
            description = "The type of tree to chop",
            position = 2,
            section = general
    )
    default Tree treeType()
    {
        return locationName().getTreeChop()[0];
    }
    @ConfigItem(
            keyName = "useSpecial",
            name = "Use Special",
            description = "Use special attack?",
            position = 3,
            section = general
    )
    default boolean useSpecial() { return true; }
}
