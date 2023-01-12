package net.warp.plugin.warpspinner;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.storm.api.commons.Time;
import net.storm.api.entities.Players;
import net.storm.api.entities.TileObjects;
import net.storm.api.items.Bank;
import net.storm.api.items.Inventory;
import net.storm.api.movement.Movement;
import net.storm.api.plugins.LoopedPlugin;
import net.storm.api.widgets.Dialog;
import net.storm.api.widgets.Production;
import net.storm.api.widgets.Widgets;
import org.pf4j.Extension;

import javax.inject.Inject;

@PluginDescriptor(
        name = "WaRp Spinner",
        description = "Spins flags",
        enabledByDefault = false
)
@Slf4j
@Extension
public class WarpSpinnerPlugin extends LoopedPlugin
{

    @Provides
    WarpSpinnerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpSpinnerConfig.class);
    }

    @Inject
    private WarpSpinnerConfig config;


    @Override
    protected int loop()
    {
        var local = Players.getLocal();

        int flaxID = ItemID.FLAX;
        int stringID = ItemID.BOW_STRING;

        Widget spinWidget = Widgets.get(270, 16);

        TileObject bankObject = TileObjects.getFirstSurrounding(local.getWorldLocation(), 8, config.location().getBankID());
        TileObject wheelObject = TileObjects.getFirstSurrounding(local.getWorldLocation(), 1, "Spinning wheel");

        if (Movement.isWalking())
        {
            return -1;
        }

        if (Production.isOpen() && spinWidget != null)
        {
            spinWidget.interact(0);
            Time.sleepUntil(() -> !Inventory.contains(flaxID) || Dialog.canContinue(), 90000);
            return -1;
        }

        if (Bank.isOpen())
        {
            if (Inventory.contains(stringID))
            {
                Bank.depositInventory();
                Time.sleepUntil(Inventory::isEmpty, -3);
                return -1;
            }

            if (Inventory.getCount(flaxID) <= 27)
            {
                Bank.withdraw(flaxID, 28, Bank.WithdrawMode.ITEM);
                Time.sleepUntil(() -> Inventory.contains(flaxID), -3);
                return -1;
            }

            if (Inventory.contains(flaxID))
            {
                Bank.close();
                Time.sleepUntil(() -> !Bank.isOpen(), -3);
                return -1;
            }
        }

        if (bankObject != null && Inventory.getCount(stringID) >= 1)
        {
            if (!Bank.isOpen())
            {
                bankObject.interact("Use", "Bank");
                Time.sleepUntil(Bank::isOpen, -3);
                return -1;
            }
        }



        if (wheelObject != null && Inventory.getCount(flaxID) >= 1)
        {
            wheelObject.interact("Spin");
            Time.sleepUntil(Production::isOpen, 80000);
            return -1;
        }

        if (Inventory.getCount(flaxID) >= 1 && !config.location().getSpinLocation().equals(local.getWorldLocation()))
        {
            Movement.walkTo(config.location().getSpinLocation());
            return -1;
        }

        if (Inventory.getCount(flaxID) <= 0 && config.location().getBankLocation().distanceTo(local.getWorldLocation()) > 3)
        {
            Movement.walkTo(config.location().getBankLocation());
            return -1;
        }

            return -1;
    }
}
