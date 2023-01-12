import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.storm.api.commons.Time;
import net.storm.api.entities.Players;
import net.storm.api.entities.TileItems;
import net.storm.api.entities.TileObjects;
import net.storm.api.game.Combat;
import net.storm.api.interaction.InteractMethod;
import net.storm.api.items.Bank;
import net.storm.api.items.Inventory;
import net.storm.api.movement.Movement;
import net.storm.api.plugins.LoopedPlugin;
import net.storm.api.widgets.Prayers;
import net.storm.api.widgets.Tab;
import net.storm.api.widgets.Tabs;
import net.storm.api.widgets.Widgets;
import org.pf4j.Extension;

import java.util.Comparator;
import java.util.List;

@PluginDescriptor(
        name = "WaRp Vyre Killer",
        description = "Vyre killer",
        enabledByDefault = false
)
@Slf4j
@Extension
@Singleton
public class WarpVyrePlugin extends LoopedPlugin
{
    private final String[] itemLoot = {"Yew logs", "Coal", "Blood shard", "Rune dagger", "Blood rune", "Death rune",
            "Nature rune", "Coins", "Runite ore", "Runite bar", "Emerald bolt tips", "Rune full helm", "Rune kiteshield",
            "Dragonstone", "Rune arrow", "Onyx bolt tips", "Rune dagger", "Ranarr seed", "Adamant platebody", "Adamant platelegs",
            "Grimy ranarr weed", "Tooth half of key", "Rune 2h sword" };

    private final String altarName = "Statue";

    private final WorldPoint altarPoint = new WorldPoint(3605, 3356, 0);
    private final WorldArea altarArea = new WorldArea(new WorldPoint(3603, 3355, 0), new WorldPoint(3607, 3358, 0));
    private final WorldPoint bankArea = new WorldPoint(3605, 3365, 0);
    private final WorldArea fightArea = new WorldArea(3595,  3361,  6,  6, 0);
    private final WorldArea bankingArea = new WorldArea(new WorldPoint(3603, 3365, 0), new WorldPoint(3607, 3368, 0));

    private boolean needPrayer = false;

    @Provides
    WarpVyreConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpVyreConfig.class);
    }
    @Inject
    WarpVyreConfig config;

    @Override
    protected int loop()
    {
        Player local = Players.getLocal();

        if (Movement.isWalking())
        {
            log.debug("Moving");
            return -2;
        }

        if (Prayers.getPoints() <= config.prayerAmount())
        {
            needPrayer = true;
            log.debug("Recharging prayer Points.");
            TileObject altar = TileObjects.getNearest(altarName);

            TileObject doorOpen = TileObjects.within(new WorldArea(3605, 3358, 1, 1, 0), x -> x.getName().contains("Door") && x.hasAction("Open"))
                    .stream()
                    .min(Comparator.comparingInt(x -> x.distanceTo(local.getWorldLocation())))
                    .orElse(null);

            if (!altarArea.contains(local.getWorldLocation()))
            {
                if (doorOpen != null)
                {
                    doorOpen.interact("Open");
                    return -1;
                }
                Movement.walk(altarPoint);
                return -1;
            }

            if (altarArea.contains(local.getWorldLocation()))
            {
                if (altar != null)
                {
                    log.debug("Praying");
                    altar.interact("Pray-at");
                    Time.sleepUntil(() -> Prayers.getPoints() > config.prayerAmount(), 2000);
                    needPrayer = false;
                    return -1;
                }
            }
        }

        if (playerHeadIcon() == null)
        {
            log.debug("Toggling Prayer");
            togglePrayer(Prayer.PROTECT_FROM_MELEE);
            return -1;
        }

        String[] tempItems = config.lootItems().split(",");

        List<Item> itemList = Inventory.getAll(config.lootAll() ? itemLoot : tempItems);

        if (Bank.isOpen() &&  !itemList.isEmpty())
        {
            for (Item item : itemList)
            {
                log.debug("Banking: " + item.getName());
                Bank.depositAll(item.getName());
                Time.sleep(100);
            }
            return -1;
        }

        if (!Combat.isRetaliating())
        {
            log.debug("Setting up Retaliate");
            Tabs.open(Tab.COMBAT);
            Time.sleep(300);
            Widget retaliateWidget = Widgets.get(593, 30);
            retaliateWidget.interact(0);
            return -1;
        }

        if (Inventory.isFull() && !Bank.isOpen())
        {
            TileObject bank = TileObjects.getNearest("Bank booth");

            TileObject doorOpen = TileObjects.within(new WorldArea(3605, 3365, 1, 1, 0), x -> x.getName().contains("Door") && x.hasAction("Open"))
                    .stream()
                    .min(Comparator.comparingInt(x -> x.distanceTo(local.getWorldLocation())))
                    .orElse(null);

            TileObject doorClose = TileObjects.within(new WorldArea(3605, 3364, 1, 1, 0), x -> x.getName().contains("Door") && x.hasAction("Close"))
                    .stream()
                    .min(Comparator.comparingInt(x -> x.distanceTo(local.getWorldLocation())))
                    .orElse(null);

            if (Inventory.contains(x -> x.getName().contains("Vyre")))
            {
                Inventory.getFirst(x -> x.getName().contains("Vyre")).interact("Wear");
                return -1;
            }

            if (!bankingArea.contains(local.getWorldLocation()))
            {
                if (doorOpen != null)
                {
                    log.debug("Opening door");
                    doorOpen.interact("Open");
                    return -1;
                }
                log.debug("Moving to Bank Area");
                Movement.walk(bankArea);
                return -1;
            }

            if (bankingArea.contains(local.getWorldLocation()))
            {
                if (doorClose != null)
                {
                    log.debug("Closing door");
                    doorClose.interact("Close");
                    return -1;
                }

                if (bank != null && !Bank.isOpen())
                {
                    log.debug("Opening bank");
                    bank.interact("Bank");
                    return -1;
                }
            }
        }

        if (wearEquipment())
        {
            log.debug("Switching gear");
            Inventory.getFirst(config.playerFeet(), config.playerLeg(), config.playerTop()).interact("Wear");
            return -1;
        }

        if (!fightArea.contains(local.getWorldLocation()) && !needPrayer)
        {
            log.debug("Moving to Fight Area");
            Movement.walkTo(fightArea.getRandom());
            return -1;
        }

        List<TileItem> items = TileItems.getAll(config.lootAll() ? itemLoot : tempItems);
        if (!items.isEmpty())
        {
            for (TileItem item : items)
            {
                if (local.getWorldLocation().distanceTo(item) < 2)
                {
                    log.debug("Looting: " + item.getName());
                    item.pickup();
                    Time.sleep(250);
                }
            }
            return -1;
        }

        if (config.offensivePrayer() && !Prayers.isEnabled(config.prayerType().getPrayer()))
        {
            log.debug("Toggling: "  + config.prayerType());
            togglePrayer(config.prayerType().getPrayer());
            return -1;
        }
        return -1;
    }

    private HeadIcon playerHeadIcon()
    {
        return Players.getLocal().getOverheadIcon();
    }
    private void togglePrayer(Prayer prayer)
    {
        Widget widget = Widgets.get(prayer.getWidgetInfo());
        if (widget != null)
        {
            widget.interact(InteractMethod.PACKETS, 0);
        }
    }
    private boolean wearEquipment()
    {
        return Inventory.contains(config.playerFeet()) || Inventory.contains(config.playerTop()) || Inventory.contains(config.playerLeg());
    }
}
