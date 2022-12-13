package net.warp.plugin.warpskiller;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.plugins.TaskPlugin;
import net.warp.plugin.warpskiller.Items.Logs;
import net.warp.plugin.warpskiller.Tasks.*;

import org.pf4j.Extension;

@PluginDescriptor(
        name = "WaRp Skiller",
        description = "Skills at any bank.",
        enabledByDefault = false
)
@Slf4j
@Extension
public class WarpSkillerPlugin extends TaskPlugin
{
    @Provides
    WarpSkillerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WarpSkillerConfig.class);
    }

    @Inject
    private BuilderOverlay builderOverlay;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public Client client;

    public boolean needSetup = true;
    public boolean makeTask;

    //Banking stuff
    public boolean banking;
    public boolean firstRun = true;
    public String item1 = "Null";
    public String item2 = "Null";
    public int item1Amount = 0;
    public int item2Amount = 0;
    public Item staff = null;
    public int[] staffID;
    public int rune1;
    public int rune2;
    //Paint info
    public long startTime;
    public int herbloreExp;
    public int magicExp;
    public int craftingExp;
    public int fletchExp;
    public int profit;
    public PluginStatus state;
    public String status;

    @Subscribe
    private void onConfigChanged (ConfigChanged configChanged)
    {
        if (configChanged.getGroup().equals("warpskiller"))
        {
            log.debug("Resetting");
            itemSetup();
            needSetup = true;
        }
    }
    @Override
    protected void startUp()
    {
        if (config.paint()) overlayManager.add(builderOverlay);
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        itemSetup();
        state = PluginStatus.IDLE;
        craftingExp = Skills.getExperience(Skill.CRAFTING);
        magicExp = Skills.getExperience(Skill.MAGIC);
        herbloreExp = Skills.getExperience(Skill.HERBLORE);
        fletchExp = Skills.getExperience(Skill.FLETCHING);
        profit = 0;
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(builderOverlay);
    }

    @Inject
    public WarpSkillerConfig config;
    private final Task[] taskList =
            {
            new SetupTask(this),
            new BankTask(this),
            new MagicTask(this),
            new MakeTask(this),
            new GrimyHerbTask(this)
            };

    @Override
    public Task[] getTasks()
    {
        return taskList;
    }

    private String getLogName()
    {
        if (config.log() == Logs.LOGS) return Logs.LOGS.getLogName();
        return config.log().getLogName() + "logs";
    }

    public void itemSetup()
    {
        switch (config.skillTask())
        {
            case HERBLORE:
                switch(config.herbloreType())
                {
                    case CLEAN:
                        item1 = "Grimy";
                        item1Amount = 28;
                        item2 = "Null";
                        item2Amount = 0;
                        makeTask = false;
                        break;

                    case POTION:
                        item1 = config.potionItem1();
                        item1Amount = 14;
                        item2 = config.potionItem2();
                        item2Amount = 14;
                        makeTask = true;
                        break;

                }
                break;

            case FLETCHING:
                item1 = "Knife";
                item1Amount = 1;
                item2 = getLogName();
                item2Amount = 27;
                makeTask = true;
                break;

            case CRAFTING:
                switch(config.craftTask())
                {
                    case GEMCUTTING:
                        String gemName = "Uncut " + config.gemType().getGemName();
                        item1 = "Chisel";
                        item1Amount = 1;
                        item2 = gemName;
                        item2Amount = 27;
                        makeTask = true;
                        break;

                    case GLASSBLOW:
                        item1 = "Glassblowing pipe";
                        item1Amount = 1;
                        item2 = "Molten glass";
                        item2Amount = 27;
                        makeTask = true;
                        break;

                    case AMETHYST:
                        item1 = "Chisel";
                        item1Amount = 1;
                        item2 = "Amethyst";
                        item2Amount = 27;
                        makeTask = true;
                        break;
                }
                break;

            case MAGIC:
                switch (config.spellType())
                {
                    case SUPERHEAT_GLASS:
                        rune1 = 0;
                        rune2 = 9075;
                        staffID = new int[]{12000};
                        item1 = "Bucket of sand";
                        item1Amount = 18;
                        item2 = "Giant seaweed";
                        item2Amount = 3;
                        makeTask = false;
                        break;

                    case PLANK_MAKE:
                        rune1 = 561;
                        rune2 = 9075;
                        staffID = new int[]{1385, 1399, 1407, 6563};
                        item1 = "Mahogany logs";
                        item1Amount = 26;
                        item2 = "Null";
                        makeTask = false;
                        break;

                    case SUPERHEAT:
                        rune1 = 561;
                        staffID = new int[]{12000, 1387, 1393, 1401};
                        item1 = config.barType().getOres();
                        item1Amount = 27;
                        item2 = "Nature rune";
                        item2Amount = 3743743;
                        makeTask = false;
                        break;

                    case LOW_ALCH:
                    case HIGH_ALCH:
                        rune1 = 561;
                        rune2 = 554;
                        staffID = new int[]{12000, 1387, 1393, 1401};
                        item1 = config.alchItem();
                        item1Amount = 684849;
                        item2 = "Nature rune";
                        item2Amount = 3743743;
                        makeTask = false;
                        break;
                }
                break;
        }
    }
}
