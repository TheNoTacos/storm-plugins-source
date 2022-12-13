package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.unethicalite.api.game.Prices;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.plugins.Plugins;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.utils.MessageUtils;
import net.warp.plugin.warpskiller.Skills.SkillTask;
import net.warp.plugin.warpskiller.Items.Spells;
import net.warp.plugin.warpskiller.PluginStatus;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;

import javax.swing.*;

@Slf4j
public class MagicTask implements Task
{
    public MagicTask(WarpSkillerPlugin plugin)
    {
        this.plugin = plugin;
    }
    private final WarpSkillerPlugin plugin;
    @Override
    public boolean validate()
    {
            return plugin.config.skillTask() == SkillTask.MAGIC;
    }
    private boolean needPrice = true;
    private final int plankID = 8782;
    private final int logID = 6332;
    private int rune1Price = 0;
    private int rune2Price = 0;
    private int item1Price = 0;
    private int item2Price = 0;

    @Override
    public int execute()
    {
        if (needItems())
        {
            log.debug("We need items");
            plugin.banking = true;
            return -1;
        }

        if (!staffEquipped() && Inventory.contains(plugin.staffID))
        {
            Inventory.getFirst(plugin.staffID).interact("Wield");
            return -1;
        }

        if (!plugin.config.spellType().getSpellType().canCast())
        {
            MessageUtils.addMessage("Can't cast spell shutting plugin");
            SwingUtilities.invokeLater(() -> Plugins.stopPlugin(plugin));
            return -1;
        }

        switch (plugin.config.spellType())
        {
            case SUPERHEAT_GLASS:
                if (Inventory.getCount(plugin.item1) == plugin.item1Amount && Inventory.getCount(plugin.item2) == plugin.item2Amount)
                {
                    plugin.status = "MagicTask - Casting spell";
                    plugin.state = PluginStatus.GLASS_MAKE;
                    Magic.cast(plugin.config.spellType().getSpellType());
                    calculateProfit();
                    return -4;
                }
                else
                {
                    plugin.status = "MagicTask - Inventory is fucked";
                    plugin.banking = true;
                }


            case PLANK_MAKE:
                if (Inventory.contains(995) && Inventory.contains(logID))
                {
                    plugin.status = "MagicTask - Casting spell";
                    plugin.state = PluginStatus.PLANK_MAKE;
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst(logID));
                    calculateProfit();
                    return -3;
                }
                break;
            case LOW_ALCH:
            case HIGH_ALCH:
                plugin.state = PluginStatus.ALCH;
                if (Inventory.contains(plugin.config.alchItem()) && Inventory.contains(plugin.rune1))
                {
                    plugin.status = "MagicTask - Casting spell";
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst(plugin.config.alchItem()));
                    calculateProfit();
                    return -4;
                }
                break;

            case SUPERHEAT:
                plugin.state = PluginStatus.SUPERHEAT;
                if (Inventory.contains(plugin.config.barType().getOres()) && Inventory.contains(plugin.rune1))
                {
                    plugin.status = "MagicTask - Casting spell";
                    Magic.cast(plugin.config.spellType().getSpellType(), Inventory.getFirst(plugin.config.barType().getOres()));
                    return -3;
                }
                break;
        }
        return -1;
    }
    private boolean staffEquipped()
    {
        return Equipment.contains(plugin.staffID);
    }
    private boolean needItems ()
    {
        switch (plugin.config.spellType())
        {
            case SUPERHEAT_GLASS:
                return !Inventory.contains("Bucket of sand") || !Inventory.contains("Giant seaweed");
            case PLANK_MAKE:
                return !Inventory.contains(995) || !Inventory.contains(logID);
            case HIGH_ALCH:
            case LOW_ALCH:
                return !Inventory.contains(plugin.rune1) || !Inventory.contains(plugin.config.alchItem());
            case SUPERHEAT:
                return !Inventory.contains(plugin.rune1) || !Inventory.contains(plugin.config.barType().getOres());
            default:
                return true;
        }
    }

    private int getPrice(int itemID)
    {
        if (itemID == 0) return 0;
        return Prices.getItemPrice(itemID);
    }

    private void calculateProfit()
    {
        if (needPrice)
        {
            item1Price = 0;
            item2Price = 0;
            rune1Price = 0;
            rune2Price = 0;
            log.debug("Getting item prices");
            switch (plugin.config.spellType())
            {
                case SUPERHEAT_GLASS:
                    int temp = getPrice(1783);
                    item1Price = getPrice(1775);
                    item2Price = getPrice(21504);
                    rune1Price = getPrice(9075);
                    item2Price = item2Price * 3;
                    item2Price += temp * 18;

                    needPrice = false;
                    return;
                case PLANK_MAKE:
                    item1Price = getPrice(plankID);
                    item2Price = getPrice(logID) + 1050;
                    rune1Price = getPrice(plugin.rune1);
                    rune2Price = getPrice(plugin.rune2);
                    rune2Price += rune2Price;
                    needPrice = false;
                    return;

                case HIGH_ALCH:
                    Item alchItem = Inventory.getFirst(plugin.config.alchItem());
                    ItemComposition alchPrice = alchItem.getComposition();
                    item1Price = alchPrice.getHaPrice();
                    item2Price = getPrice(alchItem.getId());
                    rune1Price = getPrice(plugin.rune1);
                    rune2Price = getPrice(plugin.rune2);
                    needPrice = false;
                    return;
            }
        }
        int cost = item2Price + rune1Price + rune2Price;
        if (plugin.config.spellType() == Spells.SUPERHEAT_GLASS)
        {
            if (Inventory.contains("Molten glass"))
            {
                item1Price = item1Price * Inventory.getCount("Molten glass");
            }
        }
        plugin.profit +=  item1Price - cost;
    }
}
