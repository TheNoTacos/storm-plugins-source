package net.warp.plugin.warpcrabs;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.storm.api.MouseHandler;
import net.storm.api.commons.Rand;
import net.storm.api.commons.Time;
import net.storm.api.entities.NPCs;
import net.storm.api.entities.Players;
import net.storm.api.entities.TileItems;
import net.storm.api.game.Combat;
import net.storm.api.game.Prices;
import net.storm.api.game.Skills;
import net.storm.api.items.Inventory;
import net.storm.api.magic.Magic;
import net.storm.api.movement.Movement;
import net.storm.api.packets.MousePackets;
import net.storm.api.plugins.LoopedPlugin;
import net.storm.api.widgets.Tab;
import net.storm.api.widgets.Tabs;
import net.storm.api.widgets.Widgets;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.List;

//Thanks to Keegan for the support

@PluginDescriptor(
        name = "WaRp Crabs",
        description = "Get crabs",
        enabledByDefault = false
)
@Slf4j
@Extension
public class WarpCrabsPlugin extends LoopedPlugin
{

    @Provides
    WarpCrabsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpCrabsConfig.class);
    }
    @Inject
    private WarpCrabsConfig config;
    @Inject
    public TimerUtil timerUtil;
    @Inject
    public Client client;
    @Inject
    private ConfigManager configManager;
    @Inject
    private BuilderOverlay builderOverlay;
    @Inject
    private OverlayManager overlayManager;
    private boolean timerRunning = false;
    private boolean getLoot = false;
    private int gameTicks = 5;
    public int alchProfit;
    public int rangeExp;
    public int attackExp;
    public int strengthExp;
    public int defenceExp;
    public int mageExp;
    public long startTime;
    public String status = "Idle";


    //private Alch alch;
    @Override
    protected void startUp()
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        overlayManager.add(builderOverlay);
        rangeExp = Skills.getExperience(Skill.RANGED);
        attackExp = Skills.getExperience(Skill.ATTACK);
        strengthExp = Skills.getExperience(Skill.STRENGTH);
        defenceExp = Skills.getExperience(Skill.DEFENCE);
        mageExp = Skills.getExperience(Skill.MAGIC);
        alchProfit = 0;
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(builderOverlay);
    }

    @Subscribe
    private void onGameTick (GameTick gameTick)
    {
        Alch alch;
        if (gameTicks <= 0)
        {
            alch = Skills.getLevel(Skill.MAGIC) > 54 ? Alch.HIGH_ALCH : Alch.LOW_ALCH;
            if (Inventory.contains(config.alchItem()) && config.highAlch())
            {
                Item alchItem = Inventory.getFirst(config.alchItem());
                if (alch.getSpell().canCast() && alchItem != null)
                {
                    status = "Alching " + alchItem.getName();
                    log.debug("Doing an: " + alch.getSpell() + " on item " + config.alchItem());
                    Magic.cast(alch.getSpell(), alchItem);
                    calculateProfit();
                }
            }
            gameTicks = alch.getTick();
        }
        gameTicks--;
    }

    @Override
    protected int loop()
    {
        Player local = Players.getLocal();
        List<NPC> crab;
        List<NPC> crabRock;

        String crabName = config.location().getCrabName();
        String rockName = config.location().getRockName();

        WorldPoint resetLocation = config.location().getResetLocation();
        WorldPoint killLocation = config.location().getKillLocation();
        WorldArea killArea = new WorldArea(config.location().getKillLocation(), config.crabRadius(), config.crabRadius());
        WorldArea rockArea = new WorldArea(config.location().getKillLocation(), config.rockRadius(), config.rockRadius());
        List<String> lootItems = List.of(config.lootItem().split(","));

        if (!Combat.isRetaliating())
        {
            status = "Setup";
            log.debug("Setting up Retaliate");
            Tabs.open(Tab.COMBAT);
            Time.sleepUntil(() -> Tabs.isOpen(Tab.COMBAT), -2);
            Widget retaliateWidget = Widgets.get(593, 30);
            retaliateWidget.interact(0);
            return -1;
        }

        if (config.eatFood() && Combat.getHealthPercent() <= config.healthPercent())
        {
            Item food = Inventory.getFirst(config.foodName());
            if (food != null)
            {
                status = "Eating";
                log.debug("Eating: " + config.foodName());
                food.interact("Eat");
                return -1;
            }
            log.debug("Error on eating no food");
        }

        if (Movement.isWalking())
        {
            status = "Walking";
            return -1;
        }

        if (local.getInteracting() != null)
        {
            status = "Fighting " + crabName;
        }

        if (local.getWorldLocation().distanceTo(killLocation) < 3 && !timerRunning)
        {
            status = "Setting Timer";
            timerUtil.setSleepTime(Rand.nextInt(8, 10));
            timerRunning = true;
            return -1;
        }

        if (timerUtil.toMinutes((int)timerUtil.getElapsedTime()) >= 10 && timerRunning)
        {
            status = "Re-aggro time";
            if (resetLocation.distanceTo(local.getWorldLocation()) > 4)
            {
                log.debug("Walking to get aggro again.");
                Movement.walkTo(resetLocation);
                return -1;
            }
            timerRunning = false;
            return -1;
        }

        if (!timerRunning && local.getWorldLocation().distanceTo(killLocation) != 0 && !getLoot)
        {
            status = "Walking";
            log.debug("Moving to: " + config.location().getLocationName());
            Movement.walkTo(killLocation);
            return -1;
        }

        List<TileItem> loot = TileItems.getAll(x -> x.distanceTo(local.getWorldLocation()) < 3 && lootItems.contains(x.getName()));

        getLoot = loot.size() > 0;

        if (config.lootItems() && getLoot)
        {
            for (TileItem item : loot)
            {

                status = "Looting " + item.getName();
                item.pickup();
                Time.sleepUntil(() -> item == null, -2);
            }
        }

        if (local.getInteracting() == null && !getLoot)
        {
            log.info("Starting to pull");
            status = "Not in Combat";

            crab = NPCs.getAll(x -> killArea.contains(x.getWorldLocation()) && x.getName().equals(crabName));
            if (!crab.isEmpty())
            {
                for (NPC npc : crab)
                {
                    if (npc.isDead()) continue;
                    status = "Attacking " + crabName;
                    npc.interact("Attack");
                    MousePackets.queueClickPacket();
                    Time.sleepUntil(() -> !local.isIdle(), -2);
                    return -1;
                }
            }

            log.debug("No Crabs around going for rocks");
            crabRock = NPCs.getAll(x -> rockArea.contains(x.getWorldLocation()) && x.getName().equals(rockName));
            if (!crabRock.isEmpty())
            {
                log.debug("Rock count: " + crabRock.size());
                for (NPC npc : crabRock)
                {
                    status = "Moving to " + rockName;
                    Movement.walkTo(npc.getWorldLocation());
                    MousePackets.queueClickPacket();
                    Time.sleepUntil(local::isMoving, -4);
                    return -1;
                }
            }
            status = "No " + rockName + " found";
        }

        if (config.getAmmo() && local.isIdle())
        {
            TileItem ammo = TileItems.getNearest(x -> x.distanceTo(local.getWorldLocation()) < 2 && x.getName().contains(config.ammoName()));
            if (ammo != null)
            {
                status = "Pickup Ammo";
                getLoot = true;
                log.debug("Picking up ammo");
                ammo.pickup();
                Time.sleepUntil(() -> ammo == null, -2);
                getLoot = false;
                return -1;
            }

            Item ammoEquip = Inventory.getFirst(config.ammoName());
            if (ammoEquip != null)
            {
                log.debug("Equipping ammo");
                ammoEquip.interact("Wield");
                Time.sleepUntil(() -> !Inventory.contains(ammoEquip.getName()), -2);
                return -1;
            }
        }

        if (!local.getWorldLocation().equals(killLocation) && local.isIdle())
        {
            status = "Moving back";
            Movement.walk(config.location().getKillLocation());
            Time.sleepUntil(() -> local.getWorldLocation().equals(config.location().getKillLocation()), -2);
            return -1;
        }
        return -1;
    }


    private void calculateProfit()
    {
        if (config.highAlch())
        {
            Item alchItem = Inventory.getFirst(config.alchItem());
            if (alchItem != null)
            {
                ItemComposition alchComp = alchItem.getComposition();
                int buyPrice = Prices.getItemPrice(alchItem.getId());
                int alchPrice = alchComp.getHaPrice();
                int runePrice = Prices.getItemPrice(554) + Prices.getItemPrice(561);
                alchProfit += alchPrice - (buyPrice + runePrice);
            }
        }
    }
}
