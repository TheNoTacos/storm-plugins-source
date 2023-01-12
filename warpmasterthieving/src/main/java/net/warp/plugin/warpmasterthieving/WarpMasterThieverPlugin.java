package net.warp.plugin.warpmasterthieving;

import com.google.inject.Inject;
import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;

import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.storm.api.commons.Rand;
import net.storm.api.commons.Time;
import net.storm.api.entities.NPCs;
import net.storm.api.entities.Players;
import net.storm.api.entities.TileObjects;
import net.storm.api.game.Combat;
import net.storm.api.items.Bank;
import net.storm.api.items.Inventory;
import net.storm.api.movement.Movement;
import net.storm.api.plugins.LoopedPlugin;
import org.pf4j.Extension;

import java.util.Comparator;

@PluginDescriptor(
        name = "WaRp Master thieving",
        description = "Steals from the poor",
        enabledByDefault = false
)

@Slf4j
@Extension
public class WarpMasterThieverPlugin extends LoopedPlugin
{
    @Provides
    WarpMasterThieverConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpMasterThieverConfig.class);
    }
    @Inject
    WarpMasterThieverConfig config;
    private final int bankBooth = 10355;
    @Override
    protected int loop()
    {

        var bank = TileObjects
                .getSurrounding(Players.getLocal().getWorldLocation(), 23, bankBooth)
                .stream()
                .min(Comparator.comparing(x -> x.distanceTo(Players.getLocal().getWorldLocation())))
                .orElse(null);

        NPC npc = NPCs.getNearest(config.npc().getNPCID());

        WorldArea farmerArea = config.npc().getThievingArea();
        WorldArea bankArea = config.npc().getBankLocation();


        String[] seeds = config.seedToDrop().split(",");

        Player local = Players.getLocal();

        if (Combat.getHealthPercent() <= config.healthPercent() && Inventory.contains(config.foodName()))
        {
            log.debug("Eating: " + config.foodName());
            Inventory.getFirst(config.foodName()).interact("Eat");
            return -1;
        }

        if (Inventory.getCount(true, 22531) >= Rand.nextInt(18, 27))
        {
            log.debug("Coin pouch");
            Inventory.getFirst(22531).interact("Open-all");
            return -1;
        }

        if (Inventory.contains(seeds))
        {
            log.debug("Dropping seeds");
            Inventory.getFirst(seeds).interact("Drop");
            Time.sleepUntil(() -> !Inventory.contains(seeds), -2);
            return -1;
        }

        if (Movement.isWalking())
        {
            return -1;
        }


        if (local.getGraphic() == 245)
        {
            Time.sleepUntil(() -> local.getGraphic() != 245, -4);
            return -1;
        }

        if (Bank.isOpen())
        {
            if (Inventory.contains(x -> x.getName().contains("seed")))
            {
                log.debug("Deposit seeds");
                Bank.depositInventory();
                Time.sleepUntil(Inventory::isEmpty, -2);
                return -1;
            }

            if (Inventory.getCount(config.foodName()) < config.foodAmount())
            {
                log.debug("Withdraw food");
                Bank.withdraw(config.foodName(), config.foodAmount() - Inventory.getCount(config.foodName()), Bank.WithdrawMode.ITEM);
                Time.sleepUntil(() -> Inventory.getCount(config.foodName()) >= config.foodAmount(), -2);
                return -1;
            }

            if (Inventory.getCount(config.foodName()) >= config.foodAmount())
            {
                log.debug("Closing bank");
                Bank.close();
                Time.sleepUntil(() -> !Bank.isOpen(), -2);
                return -1;
            }
        }

        if (!Inventory.contains(config.foodName()) || Inventory.isFull())
        {

            if (!Bank.isOpen() && bank != null)
            {
                log.debug("Click bank");
                bank.interact("Bank");
                Time.sleepUntil(Bank::isOpen, -2);
                return -1;
            }

            if (!bankArea.contains(local.getWorldLocation()))
            {
                log.debug("Walking to bank");
                Movement.walkTo(bankArea.getRandom());
                Time.sleepUntil(local::isMoving, -6);
                return -3;
            }
        }


        if (npc != null)
        {
            npc.interact("Pickpocket");
            return Rand.nextInt(344, 544);
        }

        if (!farmerArea.contains(local.getWorldLocation()))
        {
            log.debug("Walking to Location");
            Movement.walkTo(farmerArea.getRandom());
            return -1;
        }
        return -1;
    }
}
