package net.warp.plugin.warpcutter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.plugins.LoopedPlugin;

import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.Set;


@PluginDescriptor(
        name = "WaRp Woodcutter",
        description = "Chops trees",
        enabledByDefault = false
)
@Slf4j
@Extension
public class WarpCutterPlugin extends LoopedPlugin
{
    @Provides
    WarpCutterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpCutterConfig.class);
    }
    @Inject
    private WarpCutterConfig config;
    private Set<String> bankObjects = Set.of("Bank booth", "Grand Exchange booth", "Bank chest");
    private final String[] axeNames = {"Bronze axe", "Iron axe", "Steel axe", "Mithril axe", "Adamant axe", "Rune axe", "Dragon axe"};
    private final String[] bankText = {"Bank", "Use"};
    @Inject
    public Client client;
    @Inject
    public OverlayManager overlayManager;
    @Inject
    private BuilderOverlay builderOverlay;

    public PluginStatus status;
    public int startXP;
    public int logs;
    public int nests;
    public long startTime;

    @Override
    protected void startUp()
    {
        overlayManager.add(builderOverlay);
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        logs = 0;
        status = PluginStatus.IDLE;
        startXP = Skills.getExperience(Skill.WOODCUTTING);
        startTime = System.currentTimeMillis();
    }
    @Subscribe
    private void onChatMessage (ChatMessage m)
    {
        if (m.getMessage().contains("You get some"))
        {
            ++logs;
        }
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(builderOverlay);
    }


    @Override
    protected int loop()
    {
        Player local = Players.getLocal();

        var tree = TileObjects
                .getSurrounding(Players.getLocal().getWorldLocation(), 12, config.treeType().getTreeName())
                .stream()
                .min(Comparator.comparing(x -> x.distanceTo(Players.getLocal().getWorldLocation())))
                .orElse(null);

        var bankObject = TileObjects
                .getNearest(x -> x.hasAction(bankText) &&
                        bankObjects.contains(x.getName()));


        var birdNest = TileItems.getNearest(x -> x.getName().contains("nest"));

        if (config.useSpecial() && Equipment.fromSlot(EquipmentInventorySlot.WEAPON).getName().equals("Dragon axe") && Combat.getSpecEnergy() == 100)
        {
            log.debug("Special attack");
            Combat.toggleSpec();
        }

        if (Movement.isWalking())
        {
            status = PluginStatus.WALKING;
            return -1;
        }

        if (local.isAnimating())
        {
            status = PluginStatus.CHOPPING;
            return -1;
        }

        if (birdNest != null && !Inventory.isFull() && config.getNest())
        {
            log.debug("Getting Bird nest");
            birdNest.pickup();
            ++nests;
            return -3;
        }

        if (config.locationName() == Location.POWERCHOP)
        {
            status = PluginStatus.DROPPING;
            for (Item log : Inventory.getAll(x -> x.getName().toLowerCase().contains("logs")))
            {
                log.drop();
                Time.sleep(160);
            }

            if (tree != null)
            {
                status = PluginStatus.CHOPPING;
                log.debug("Chopping " + tree.getName());
                tree.interact("Chop down");
                return -4;
            }
            return -1;
        }

        if (Inventory.isFull() && !Bank.isOpen())
        {

            if (config.locationName().getBankArea().distanceTo(local.getWorldLocation()) > 3)
            {
                status = PluginStatus.WALKING;
                if (config.locationName() == Location.PORT_SARIM)
                {
                    log.debug("Moving to bank: " + config.locationName().getLocationName());
                    Movement.walkTo(config.locationName().getBankArea().toWorldPoint());
                    return -1;
                }
                log.debug("Moving to bank: " + config.locationName().getLocationName());
                Movement.walk(config.locationName().getBankArea().toWorldPoint());
                return -1;
            }

            if (bankObject != null && !Bank.isOpen())
            {
                status = PluginStatus.BANK;
                log.debug("Opening bank");
                bankObject.interact(bankText);
                return -2;
            }
        }

        if (Bank.isOpen())
        {
            if (Inventory.contains(x -> x.getName().toLowerCase().contains("logs")) || Inventory.contains(x -> x.getName().contains("nest")))
            {
                log.debug("Deposit logs");
                Bank.depositAllExcept(axeNames);
                return -1;
            }
        }

        if (!Inventory.isFull())
        {
            if (!config.locationName().getTreeArea().contains(local.getWorldLocation()))
            {
                status = PluginStatus.WALKING;
                if (config.locationName() == Location.PORT_SARIM)
                {
                    log.debug("Moving to tree location: " + config.locationName());
                    Movement.walkTo(getChopPoint());
                    return -1;
                }
                log.debug("Moving to tree's");
                Movement.walk(getChopPoint());
                return -1;
            }

            if (tree != null)
            {
                status = PluginStatus.CHOPPING;
                log.debug("Chopping " + tree.getName());
                tree.interact("Chop down");
                return -4;
            }
        }
        return Rand.nextInt(3794, 8738);
    }


    private WorldPoint getChopPoint()
    {
        if (config.locationName() == Location.WOODCUTTING_GUILD)
        {
            if (config.treeType() == Tree.YEW)
            {
                return new WorldPoint(1594, 3489, 0);
            }

            if (config.treeType() == Tree.MAGIC)
            {
                return new WorldPoint(1578, 3491, 0);
            }
        }
        return config.locationName().getTreeArea().toWorldPoint();
    }
}
