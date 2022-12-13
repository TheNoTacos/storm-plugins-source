package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.Varbits;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.warp.plugin.warpskiller.Items.Spells;
import net.warp.plugin.warpskiller.PluginStatus;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;
import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;
import java.util.Set;

@Singleton
@Slf4j
public class SetupTask implements Task
{

    private final Set<String> bankObjects = Set.of("Bank booth", "Grand Exchange booth", "Bank chest");
    private final String[] bankText = {"Bank", "Use"};
    private boolean gotPouch;
    private boolean filledPouch;

    public SetupTask(WarpSkillerPlugin plugin)
    {
        this.plugin = plugin;
    }
    private WarpSkillerPlugin plugin;

    @Override
    public boolean validate()
    {
        return plugin.needSetup;
    }

    @Override
    public int execute()
    {
        plugin.state = PluginStatus.SETUP;
        plugin.status = "SetupTask - Start of Setup";
        var bankObject = TileObjects
                .getNearest(x -> x.hasAction(bankText) &&
                        bankObjects.contains(x.getName()));

        log.debug("WarpSkiller setup");
        switch (plugin.config.skillTask())
        {

            case CRAFTING:
            case FLETCHING:
            case HERBLORE:

                if (!Bank.isOpen())
                {
                    log.debug("Opening bank");
                    bankObject.interact(bankText);
                    Time.sleepUntil(Bank::isOpen, -2);
                    return -1;
                }

                if (Bank.isOpen() && !Inventory.isEmpty())
                {
                    Bank.depositInventory();
                    Time.sleepUntil(Inventory::isEmpty, -2);
                    plugin.needSetup = false;
                    return -1;
                }
                break;


            case MAGIC:

                String runePouch = "Rune pouch";
                log.debug("Setting up magic");

                if (!Bank.isOpen() && !gotPouch)
                {
                    log.debug("Opening bank");
                    bankObject.interact(bankText);
                    Time.sleepUntil(Bank::isOpen, -2);
                    return -1;
                }

                plugin.staff = getStaff();

                if (plugin.staff == null)
                {
                    if (!Inventory.contains(plugin.staffID) && !itemEquipped(plugin.staffID))
                    {
                        if (Bank.contains(plugin.staffID))
                        {
                            log.debug("Withdrawing: " + plugin.staff.getName());
                            Bank.depositAllExcept(plugin.item1, plugin.item2, plugin.staff.getName(), "Coins", "Rune pouch");
                            Time.sleep(150);
                            Bank.withdraw(plugin.staff.getId(), 1, Bank.WithdrawMode.ITEM);
                            Time.sleepUntil(() -> Inventory.contains(plugin.staffID), -2);
                        }
                    }
                }
                if (plugin.config.spellType() == Spells.PLANK_MAKE)
                {
                    if (Bank.isOpen() && !gotPouch)
                    {
                        log.debug("Getting Rune pouch");
                        if (!Inventory.contains(runePouch) && Bank.contains(runePouch))
                        {
                            Bank.withdraw(runePouch, 1, Bank.WithdrawMode.ITEM);
                            Time.sleepUntil(() -> Inventory.contains(runePouch), -2);
                            return -1;
                        }

                        if (pouchFilled())
                        {
                            plugin.firstRun = false;
                            log.debug("Pouch filled with Natures and Astral");
                            return -1;
                        }

                        if (Bank.contains(plugin.rune1) && !Inventory.contains(plugin.rune1))
                        {
                            Bank.withdraw(plugin.rune1, 16000, Bank.WithdrawMode.ITEM);
                            Time.sleepUntil(() -> Inventory.contains(plugin.rune1), -2);
                            return -2;
                        }

                        if (Bank.contains(plugin.rune2) && !Inventory.contains(plugin.rune2))
                        {
                            Bank.withdraw(plugin.rune2, 16000, Bank.WithdrawMode.ITEM);
                            Time.sleepUntil(() -> Inventory.contains(plugin.rune2), -2);
                            return -2;
                        }
                        gotPouch = true;
                    }

                    if (Bank.isOpen())
                    {
                        log.debug("Closing bank");
                        Bank.close();
                        Time.sleepUntil(() -> !Bank.isOpen(), -2);
                        return -1;
                    }

                    if (Inventory.contains(runePouch))
                    {
                        Item pouch = Inventory.getFirst(runePouch);

                        if (!filledPouch)
                        {
                            log.debug("Emptying pouch");
                            pouch.interact("Empty");
                            Time.sleepUntil(() -> plugin.client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1) == 0, -2);
                            return -1;
                        }

                        Item rune1 = Inventory.getFirst(plugin.rune1);
                        Item rune2 = Inventory.getFirst(plugin.rune2);

                        if (rune1 != null && plugin.client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1) != 10 && rune1.getId() == 561)
                        {
                            log.debug("Filling pouch with: " + rune1.getName());
                            rune1.useOn(pouch);
                            Time.sleepUntil(() -> !Inventory.contains(rune1.getName()), -2);
                            filledPouch = true;
                            return -1;
                        }

                        if (rune2 != null && plugin.client.getVarbitValue(Varbits.RUNE_POUCH_RUNE2) != 14 && rune2.getId() == 9075)
                        {
                            log.debug("Filling pouch with: " + rune2.getName());
                            rune2.useOn(pouch);
                            Time.sleepUntil(() -> !Inventory.contains(rune2.getName()), -2);
                            filledPouch = true;
                            return -1;
                        }
                        log.debug("Done filling pouch");
                    }
                    plugin.needSetup = false;
                    break;
                }
                }

        log.debug("Plugin setup is done");
        plugin.firstRun = false;
        plugin.needSetup = false;
        return -1;
    }


    private boolean pouchFilled()
    {
        if (plugin.client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1) == 10 || plugin.client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1) == 14)
        {
            return true;
        }

        if (plugin.client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1) == 10 && plugin.client.getVarbitValue(Varbits.RUNE_POUCH_RUNE2) == 14)
        {
            return true;
        }

        if (plugin.client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1) == 14 && plugin.client.getVarbitValue(Varbits.RUNE_POUCH_RUNE2) == 10)
        {
            return true;
        }
        return false;
    }

    private boolean itemEquipped(int... ID)
    {
        return Equipment.contains(ID);
    }

    @Nullable
    private Item getStaff()
    {
        if (Equipment.contains(plugin.staffID))
        {
            return Equipment.getFirst(plugin.staffID);
        }
        if (Bank.contains(plugin.staffID))
        {
            return Bank.getFirst(plugin.staffID);
        }
        if (Inventory.contains(plugin.staffID))
        {
            return Inventory.getFirst(plugin.staffID);
        }
        log.debug("Null");
        return null;
    }
}
