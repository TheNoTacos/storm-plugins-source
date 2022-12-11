import net.runelite.client.config.*;

@ConfigGroup("warpvyrekiller")
public interface WarpVyreConfig extends Config
{
    @ConfigSection(
            name = "Prayer settings",
            description = "Prayer settings",
            position = 0
    )
    String general = "General";

    @ConfigSection(
            name = "Equipment Settings",
            description = "Equipment Settings",
            position = 1
    )
    String equipment = "equipment";

    @ConfigSection(
            name = "Loot settings",
            description = "Loot settings",
            position = 2
    )
    String loot = "Loot";

    @ConfigItem(
            keyName = "prayer",
            name = "Offensive prayer",
            description = "Use offensive prayer",
            position = 0,
            section = general
    )
    default boolean offensivePrayer()
    {
        return false;
    }

    @ConfigItem(
            keyName = "whatPrayer",
            name = "Which prayer",
            description = "What offensive prayer to use",
            position = 1,
            section = general
    )
    default OffensivePrayer prayerType()
    {
        return OffensivePrayer.PIETY;
    }

    @ConfigItem(
            keyName = "prayerAmount",
            name = "Prayer recharge",
            description = "Prayer amount to use altar at",
            position = 2,
            section = general
    )
    default int prayerAmount()
    {
        return 15;
    }

    @ConfigItem(
            keyName = "playerTop",
            name = "Player top",
            description = "What top you wear",
            position = 0,
            section = equipment
    )
    default String playerTop()
    {
        return "Proselyte hauberk";
    }

    @ConfigItem(
            keyName = "playerLeg",
            name = "Player legs",
            description = "What legs you wear",
            position = 1,
            section = equipment
    )
    default String playerLeg()
    {
        return "Proselyte cuisse";
    }

    @ConfigItem(
            keyName = "playerFeet",
            name = "Player Foot",
            description = "Send feet pics",
            position = 2,
            section = equipment
    )
    default String playerFeet()
    {
        return "Primordial boots";
    }

    @ConfigItem(
            keyName = "loot",
            name = "Loot all",
            description = "Loot all standard items",
            position = 0,
            section = loot
    )
    default boolean lootAll()
    {
        return true;
    }

    @ConfigItem(
            keyName = "lootItems",
            name = "Items to loot:",
            description = "What items to loot",
            position = 1,
            section = loot
    )
    default String lootItems()
    {
        return "Blood shard,Nature rune";
    }

}
