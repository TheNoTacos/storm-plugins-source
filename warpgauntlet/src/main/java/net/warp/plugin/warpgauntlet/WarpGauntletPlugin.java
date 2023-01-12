package net.warp.plugin.warpgauntlet;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.storm.api.entities.NPCs;
import net.storm.api.entities.Players;
import net.storm.api.game.Combat;
import net.storm.api.interaction.InteractMethod;
import net.storm.api.items.Equipment;
import net.storm.api.items.Inventory;
import net.storm.api.plugins.LoopedPlugin;
import net.storm.api.widgets.Prayers;
import net.storm.api.widgets.Widgets;

import org.pf4j.Extension;

import java.util.Set;
@PluginDescriptor(
        name = "WaRp Hunllef Swapper",
        description = "Helps with Hunllef/Corrupted",
        enabledByDefault = false
)
@Slf4j
@Extension
public class WarpGauntletPlugin extends LoopedPlugin
{
    private final Set<Integer> magicAttackID = Set.of(1707, 1708);
    private final Set<Integer> rangeAttackID = Set.of(1711, 1712);
    private final Set<Integer> prayerAttackID = Set.of(1713, 1714);
    private final int[] bowID = { 23901, 23902, 23903, 23855, 23856, 23857 };
    private final int[] staffID = { 23898, 23899, 23900, 23852, 23853, 23854 };
    private final int[] hunleffID = { 9021, 9022, 9023, 9024, 9035, 9036, 9037, 9038 };
    public static final int hunleffTornado = 8418;
    private final int[] potionID = { 23882, 23883, 23884, 23885 };
    private final int[] foodID = { 23874, 25958 };

    private NPC hunllef = null;
    private int attackCount = 4;
    private AttackPhase attackPhase;
    @Provides
    WarpGauntletConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpGauntletConfig.class);
    }
    @Inject
    WarpGauntletConfig config;
    @Inject
    private Client client;
    @Subscribe
    private void onVarbitChanged (VarbitChanged varbitChanged)
    {
        if (varbitChanged.getVarbitId() == 9178)
        {
            log.debug("Entered Gauntlet resetting attackPhase");
            attackPhase = AttackPhase.RANGE;
            attackCount = 4;
        }

        if (varbitChanged.getVarbitId() == 9177 && hunllef != null)
        {
            log.debug("Entering Hunlef toggle MISSILE Prayer");
            togglePrayer(Prayer.PROTECT_FROM_MISSILES);
        }
    }
    @Subscribe
    private void onGameTick (GameTick gameTick)
    {
        if (isHunllefVarbitSet())
        {
            hunllef = NPCs.getNearest(hunleffID);
            if (npcHeadIcon(hunllef) == HeadIcon.MAGIC && swapWeapon(bowID))
            {
                log.debug("Equipping bow");
                Inventory.getFirst(bowID).interact(InteractMethod.PACKETS, "Wield");
            }

            if (npcHeadIcon(hunllef) == HeadIcon.RANGED && swapWeapon(staffID))
            {
                log.debug("Equipping Staff");
                Inventory.getFirst(staffID).interact(InteractMethod.PACKETS, "Wield");
            }
        }
    }
    @Subscribe
    private void onProjectileSpawned (ProjectileSpawned projectileSpawned)
    {
        if (isHunllefVarbitSet())
        {
            Projectile projectile = projectileSpawned.getProjectile();
            if (hunllefAttack(projectile))
            {
                --attackCount;
                log.debug("Counting down attacks: " + attackCount);
            }
        }
    }
    @Subscribe
    private void onAnimationChanged(final AnimationChanged animationChanged)
    {
        if (isHunllefVarbitSet())
        {
            final Actor actor = animationChanged.getActor();
            final int animationId = actor.getAnimation();

            if (actor instanceof NPC)
            {
                if (animationId == hunleffTornado)
                {
                    --attackCount;
                    log.debug("Counting down attacks: " + attackCount);
                }
            }
        }
    }
    @Override
    protected int loop()
    {
        if (isHunllefVarbitSet())
        {
            Item potion = Inventory.getFirst(potionID);
            Item food = Inventory.getFirst(foodID);

            if (attackCount == 0)
            {
                attackPhase = attackPhase == AttackPhase.RANGE ? AttackPhase.MAGIC : AttackPhase.RANGE;
                log.debug("Switching attack phase: " + attackPhase);
                attackCount = 4;
                return -1;
            }

            if (!Prayers.isEnabled(attackPhase.getPrayerType()) || playerHeadIcon() == null)
            {
                log.debug("Defencive prayer: " + attackPhase.getPrayerType());
                togglePrayer(attackPhase.getPrayerType());
                return -1;
            }

            if (config.eat() && Combat.getHealthPercent() <= config.healthPercent() && food != null)
            {
                log.debug("Eating");
                food.interact(InteractMethod.PACKETS, "Eat");
                return -1;
            }

            if (config.drinkPot() && Prayers.getPoints() <= config.prayerPoints() && potion != null)
            {
                log.debug("Drinking Prayer pot at " + Prayers.getPoints() + " Prayer points");
                potion.interact(InteractMethod.PACKETS, "Drink");
                return -1;
            }

            if (Equipment.contains(bowID) && !Prayers.isEnabled(config.offencePrayerRange().getPrayerType()))
            {
                log.debug("Offencive prayer: " + config.offencePrayerRange().getPrayerType());
                togglePrayer(config.offencePrayerRange().getPrayerType());
                return -1;
            }

            if (Equipment.contains(staffID) && !Prayers.isEnabled(config.offencePrayerMage().getPrayerType()))
            {
                log.debug("Offencive prayer: " + config.offencePrayerMage().getPrayerType());
                togglePrayer(config.offencePrayerMage().getPrayerType());
                return -1;
            }
        }
        return -1;
    }

    private void togglePrayer(Prayer prayer)
    {
        Widget widget = Widgets.get(prayer.getWidgetInfo());
        if (widget != null)
        {
            widget.interact(InteractMethod.PACKETS, 0);
        }
    }

    private HeadIcon npcHeadIcon(NPC npc)
    {
        return npc.getOverheadIcon();
    }
    private HeadIcon playerHeadIcon()
    {
        return Players.getLocal().getOverheadIcon();
    }
    private boolean swapWeapon(int[] weapon)
    {
        return !Equipment.contains(weapon) && config.swapWeapon();
    }
    private boolean isHunllefVarbitSet()
    {
        return client.getVar(9177) == 1;
    }
    private boolean hunllefAttack(Projectile projectile)
    {
        return magicAttackID.contains(projectile.getId()) ||
            rangeAttackID.contains(projectile.getId()) ||
            prayerAttackID.contains(projectile.getId());
    }
}
