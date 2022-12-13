package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.TileItem;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Plugins;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.utils.MessageUtils;
import net.warp.plugin.warpskiller.Skills.Herblore;
import net.warp.plugin.warpskiller.Items.Spells;
import net.warp.plugin.warpskiller.PluginStatus;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;

import javax.swing.*;
import java.util.List;
import java.util.Set;

@Slf4j
public class BankTask implements Task
{
    public BankTask(WarpSkillerPlugin plugin) {
        this.plugin = plugin;
    }

    private final WarpSkillerPlugin plugin;
    private final Set<String> bankObjects = Set.of("Bank booth", "Grand Exchange booth", "Bank chest");
    private final String[] bankText = {"Bank", "Use"};

    private boolean pickGlass;

    @Override
    public boolean validate() {
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
            if (!pickGlass) {
                log.debug("Banking to pickup Glass");

                if (!Bank.isOpen() && bankObject != null && Inventory.contains("Molten glass")) {
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

                case HERBLORE:
                    if (plugin.config.herbloreType() == Herblore.CLEAN)
                    {
                        if (Inventory.contains(x -> x.getName().contains(plugin.item1)))
                        {
                            if (Bank.isOpen())
                            {
                                plugin.status = "BankTask - Herblore closing bank";
                                Bank.close();
                                Time.sleepUntil(() -> !Bank.isOpen(), -2);
                                plugin.banking = false;
                                return -1;
                            }
                            plugin.banking = false;
                            return -1;
                        }

                        if (!Bank.contains(x -> x.getName().equals(plugin.item1)))
                        {
                            MessageUtils.addMessage("Couldn't find Grimy in bank!");
                            stopPlugin();
                        }

                        log.debug("Getting Grimy herbs from bank");
                        plugin.status = "BankTask - Withdrawing grimy herbs";
                        Bank.depositInventory();
                        Time.sleepUntil(Inventory::isEmpty, -2);

                        List<Item> grimyHerb = Bank.getAll(x -> x.getName().contains(plugin.item1));
                        for (Item grimy : grimyHerb)
                        {
                            if (grimy.getQuantity() < 1)
                            {
                                continue;
                            }

                            if (!Inventory.isFull())
                            {
                                plugin.status = "BankTask - Getting " + grimy.getName();
                                log.debug("Getting: " + grimy.getName());
                                Bank.withdrawAll(grimy.getName(), Bank.WithdrawMode.ITEM);
                                Time.sleepUntil(() -> Inventory.contains(grimy.getName()), -2);
                            }
                        }
                        return -1;
                    }
                case CRAFTING:
                case FLETCHING:
                    log.debug("Banking for: " + plugin.config.skillTask());

                    if (plugin.staff == null)
                    {
                        plugin.staff = Inventory.getFirst(plugin.item1);
                    }
                    for (Item item : Inventory.getAll()) {
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

                            if (bankItem != null) {
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

                            if (bankItem != null) {
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
