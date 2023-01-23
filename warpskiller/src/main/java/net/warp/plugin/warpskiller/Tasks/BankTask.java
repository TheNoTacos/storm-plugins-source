package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.TileItem;
import net.storm.api.commons.Time;
import net.storm.api.entities.Players;
import net.storm.api.entities.TileItems;
import net.storm.api.entities.TileObjects;
import net.storm.api.game.Game;
import net.storm.api.items.Bank;
import net.storm.api.items.Inventory;
import net.storm.api.plugins.Plugins;
import net.storm.api.plugins.Task;
import net.storm.api.utils.MessageUtils;
import net.warp.plugin.warpskiller.Skills.Herblore;
import net.warp.plugin.warpskiller.Items.Spells;
import net.warp.plugin.warpskiller.PluginStatus;
import net.warp.plugin.warpskiller.Skills.SkillTask;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;

import javax.swing.*;
import java.util.List;
import java.util.Set;

@Slf4j
public class BankTask implements Task
{
    public BankTask(WarpSkillerPlugin plugin)
    {
        this.plugin = plugin;
    }

    private final WarpSkillerPlugin plugin;
    private final Set<String> bankObjects = Set.of("Bank booth", "Grand Exchange booth", "Bank chest");
    private final String[] bankText = {"Bank", "Use"};

    private boolean pickGlass;

    @Override
    public boolean validate()
    {
        return plugin.banking;
    }

    @Override
    public int execute()
    {
        plugin.state = PluginStatus.BANK;
        plugin.itemSetup();
        var bankObject = TileObjects
                .getNearest(x -> x.hasAction(bankText) &&
                        bankObjects.contains(x.getName()));

        plugin.state = PluginStatus.BANK;

        TileItem glass = TileItems.getNearest(x -> x.getName().equals("Molten glass") && Players.getLocal().distanceTo(x.getWorldLocation()) == 0);
        if (plugin.config.pickupGlass() && glass != null)
        {
            plugin.status = "BankTask - Glass pickup";
            if (!pickGlass)
            {
                log.debug("Banking to pickup Glass");

                if (!Bank.isOpen() && bankObject != null && Inventory.contains("Molten glass"))
                {
                    log.debug("Opening bank");
                    bankObject.interact(bankText);
                    Time.sleepUntil(Bank::isOpen, -4);
                    return -1;
                }
                if (Bank.isOpen())
                {
                    Bank.depositAll("Molten glass");
                    Time.sleepUntil(() -> !Inventory.contains("Molten glass"), -2);
                    Bank.close();
                    Time.sleepUntil(() -> !Bank.isOpen(), -2);
                    return -1;
                }
                pickGlass = true;
            }
            if (Inventory.getFreeSlots() < 5)
            {
                pickGlass = false;
                return -1;
            }
            plugin.status = "BankTask - Glass pickup";
            log.debug("Picking up glass");
            glass.pickup();
            return -1;
        }

        if (!Bank.isOpen() && bankObject != null)
        {
            log.debug("Opening bank");
            plugin.status = "BankTask - Opening Bank";
            bankObject.interact(bankText);
            Time.sleepUntil(Bank::isOpen, -4);
            return -1;
        }

        if (Bank.isOpen())
        {
            switch (plugin.config.skillTask())
            {
                case MAGIC:
                    if (plugin.config.spellType() == Spells.PLANK_MAKE)
                    {
                        if (!Inventory.contains(995))
                        {
                            plugin.status = "BankTask - Get coins";
                            Bank.withdrawAll(995, Bank.WithdrawMode.ITEM);
                            Time.sleepUntil(() -> Inventory.contains(995), -2);
                            return -1;
                        }
                    }

                    if (plugin.config.spellType() == Spells.HIGH_ALCH || plugin.config.spellType() == Spells.LOW_ALCH || plugin.config.spellType() == Spells.SUPERHEAT)
                    {
                        plugin.item1Amount = Bank.getCount(plugin.item1) + Inventory.getCount(plugin.item1);
                        plugin.item2Amount = Bank.getCount(plugin.item2) + Inventory.getCount(plugin.item2);



                        Bank.depositAllExcept(plugin.item1, plugin.item2, "Nature rune");

                        if (!Inventory.contains(plugin.item1))
                        {

                            Item alchItem = Bank.getFirst(plugin.item1);
                            if (alchItem != null)
                            {
                                Bank.withdraw(alchItem.getId(), alchItem.getQuantity(), Bank.WithdrawMode.NOTED);
                                plugin.item1Amount = Bank.getCount(plugin.item1) + Inventory.getCount(plugin.item1);
                                Time.sleepUntil(() -> Inventory.contains(alchItem.getId()) , -3);
                                return -1;
                            }
                        }

                        if (!Inventory.contains(plugin.item2))
                        {
                            Item alchItem = Bank.getFirst(plugin.item2);
                            if (alchItem != null)
                            {
                                Bank.withdraw(alchItem.getId(), alchItem.getQuantity(), Bank.WithdrawMode.ITEM);
                                Time.sleepUntil(() -> Inventory.contains(alchItem.getId()) , -3);
                                return -1;
                            }
                        }

                        Bank.close();
                        Time.sleepUntil(() -> !Bank.isOpen(), -2);
                        plugin.banking = false;
                        return -1;
                    }

                case HERBLORE:
                case CRAFTING:
                case FLETCHING:
                    log.debug("Banking for: " + plugin.config.skillTask());

                    if (plugin.staff == null)
                    {
                        plugin.staff = Inventory.getFirst(plugin.item1);
                    }
                    for (Item item : Inventory.getAll())
                    {
                        if (item.getName().equals(plugin.item1)
                                || item.getName().equals(plugin.item2)
                                || item.getName().equals("Rune pouch")
                                || item.getName().equals("Coins")
                                || item.getName().equals(plugin.staff.getName())
                                || item.getId() == plugin.rune1
                                || item.getId() == plugin.rune2)
                        {
                            continue;
                        }
                        log.debug("Removing item: " + item.getName());
                        plugin.status = "BankTask - Deposit " + item.getName();
                        Bank.depositAll(item.getName());
                        Time.sleepUntil(() -> !Inventory.contains(item.getName()), -2);
                        return -1;
                    }

                    if (!plugin.item1.contains("Null"))
                    {
                        if (Inventory.getCount(false, plugin.item1) != plugin.item1Amount)
                        {
                            plugin.status = "BankTask - Getting item ";
                            log.info("Checking if bank has " + plugin.item1);
                            var bankItem = Bank.getFirst(x -> x.getName().equals(plugin.item1));

                            if (bankItem != null)
                            {
                                plugin.status = "BankTask - Getting item " + bankItem.getName();
                                log.info("Getting " + plugin.item1Amount + " " + bankItem.getName() + " from bank");
                                Bank.withdraw(bankItem.getName(), plugin.item1Amount, Bank.WithdrawMode.ITEM);
                                Time.sleepUntil(() -> Inventory.getCount(plugin.item1) == plugin.item1Amount, -3);
                                return -2;
                            }
                            plugin.status = "BankTask - Item not in bank";
                            log.info("Couldn't find " + plugin.item1 + " in bank");
                            stopPlugin();
                        }
                    }

                    if (!plugin.item2.contains("Null"))
                    {
                        plugin.status = "BankTask - Getting item ";
                        if (Inventory.getCount(false, plugin.item2) != plugin.item2Amount)
                        {
                            log.info("Checking if bank has " + plugin.item2);
                            var bankItem = Bank.getFirst(x -> x.getName().equals(plugin.item2));

                            if (bankItem != null)
                            {
                                plugin.status = "BankTask - Getting item " + bankItem.getName();
                                log.info("Getting " + plugin.item2Amount + " " + bankItem.getName() + " from bank");
                                Bank.withdraw(bankItem.getName(), plugin.item2Amount, Bank.WithdrawMode.ITEM);
                                Time.sleepUntil(() -> Inventory.getCount(plugin.item2) == plugin.item2Amount, -3);
                                return -2;
                            }
                            plugin.status = "BankTask - Item not in bank";
                            log.info("Couldn't find " + plugin.item2 + " in bank");
                            stopPlugin();
                        }
                    }
                    break;
            }
            plugin.status = "BankTask - Closing Bank";
            log.debug("Closing bank");
            Bank.close();
            Time.sleepUntil(() -> !Bank.isOpen(), -2);
            plugin.banking = false;
        }
        log.debug("Banking is: " + plugin.banking);
        return -1;
    }

    private void stopPlugin()
    {
        MessageUtils.addMessage("Can't find items in Bank.");
        if (plugin.config.logOut()) Game.logout();
        SwingUtilities.invokeLater(() -> Plugins.stopPlugin(plugin));
    }
}
