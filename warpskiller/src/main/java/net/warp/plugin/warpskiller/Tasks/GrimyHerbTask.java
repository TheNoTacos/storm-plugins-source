package net.warp.plugin.warpskiller.Tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.storm.api.items.Inventory;
import net.storm.api.plugins.Task;
import net.warp.plugin.warpskiller.PluginStatus;
import net.warp.plugin.warpskiller.Skills.Herblore;
import net.warp.plugin.warpskiller.Skills.SkillTask;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;
@Slf4j
public class GrimyHerbTask implements Task
{
    public GrimyHerbTask(WarpSkillerPlugin plugin)
    {
        this.plugin = plugin;
    }
    WarpSkillerPlugin plugin;
    @Override
    public boolean validate()
    {
        return plugin.config.skillTask() == SkillTask.HERBLORE && plugin.config.herbloreType() == Herblore.CLEAN;
    }

    @Override
    public int execute()
    {
        plugin.state = PluginStatus.CLEANHERBS;
        if (!Inventory.contains(x -> x.getName().contains(plugin.item1)))
        {
            log.debug("Need more Grimy herbs");
            plugin.banking = true;
            return -1;
        }

        Item grimyHerb = Inventory.getFirst(x -> x.getName().contains(plugin.item1));

        if (grimyHerb != null)
        {
            plugin.status = "GrimyTask - Cleaning " + grimyHerb.getName();
            grimyHerb.interact("Clean");
            return -1;
        }

        return -1;
    }
}
