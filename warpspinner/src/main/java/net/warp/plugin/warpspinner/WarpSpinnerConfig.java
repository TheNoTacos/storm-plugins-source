package net.warp.plugin.warpspinner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
@ConfigGroup("warpspinner")
public interface WarpSpinnerConfig extends Config
{

    @ConfigSection(
            name = "Warp Spinner",
            description = "Settings",
            position = 0
    )
    String settings = "Settings";

    @ConfigItem(
            keyName = "location",
            name = "Location",
            description = "Where to spin",
            position = 1,
            section = settings
    )
    default Location location()
    {
        return Location.LUMBRIDGE;
    }

}
