package net.warp.plugin.warpskiller;
/*
    Full credits to Keegan!
    He made this and shared with me.
*/
import com.google.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.storm.api.game.Skills;
import net.storm.api.widgets.Widgets;

import javax.inject.Inject;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

@Singleton
class BuilderOverlay extends Overlay
{
    private static final Font FONT = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
    private static final Color YELLOW = new Color(255, 214, 0);

    private NumberFormat myFormat;

    private final WarpSkillerPlugin plugin;
    private final WarpSkillerConfig config;

    @Inject
    private Client client;

    @Inject
    BuilderOverlay(Client client, WarpSkillerPlugin plugin, WarpSkillerConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGHEST);
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {
        graphics2D.setFont(FONT);

        if (plugin.startTime > 0)
        {
            renderStats(graphics2D);
        }
        return null;
    }
    private void renderStats(Graphics2D graphics)
    {

        int secondsRunning = (int)(System.currentTimeMillis() - plugin.startTime) / 1000;
        String mainText = "Warp Skiller V0.2.2";
        String taskString = "Current task: " + plugin.config.skillTask().getTaskName() + " - " + plugin.state.getStatus();
        String runtimeString = "Runtime: " + getRuntimeString(secondsRunning);
        String statusString = "Status: " + plugin.status;

        Rectangle slotBounds;
        double hoursAsDouble = (double) secondsRunning / 3600.0;
        int craftGained = (Skills.getExperience(Skill.CRAFTING) - plugin.craftingExp);
        int magicGained = (Skills.getExperience(Skill.MAGIC) - plugin.magicExp);
        int herbloreGained = (Skills.getExperience(Skill.HERBLORE) - plugin.herbloreExp);
        int fletchingGained = (Skills.getExperience(Skill.FLETCHING) - plugin.fletchExp);

        String moneyMadeString = "Profit: " + plugin.profit + " (" + (int) (plugin.profit / hoursAsDouble / 1000) + "k/hr)";
        String craftString =  "Crafting exp: " + craftGained + " (" + (int) (craftGained / hoursAsDouble / 1000) + "k/hr)";
        String magicString = "Magic exp: "  + magicGained + " (" + (int) (magicGained / hoursAsDouble / 1000) + "k/hr)";
        String herbloreString = "Herblore exp: "  + herbloreGained + " (" + (int) (herbloreGained / hoursAsDouble / 1000) + "k/hr)";
        String fletchingString = "Fletching exp: "  + fletchingGained + " (" + (int) (fletchingGained / hoursAsDouble / 1000) + "k/hr)";

        List<Widget> chatboxRoot = Widgets.get(162);
        Widget box = chatboxRoot.get(34);
        if (box != null)
        {
            slotBounds = box.getBounds();
            Point mouse = plugin.client.getCanvas().getMousePosition();
            graphics.setColor(new Color(0, 0, 0, 255));

            if (mouse != null)
            {
                if (slotBounds.contains(mouse))
                {
                    graphics.setColor(new Color(0, 0, 0, 125));
                }
            }
            graphics.fill(slotBounds);
        }

        int textX = 20;
        int textY = 370;
        graphics.setFont(FONT);
        graphics.setColor(Color.BLACK);
        graphics.drawString(mainText, textX + 1, textY + 1);
        graphics.setColor(YELLOW);
        graphics.drawString(mainText, textX, textY);

        textY += 20;
        graphics.setColor(Color.BLACK);
        graphics.drawString(moneyMadeString, textX + 1, textY + 1);
        graphics.setColor(YELLOW);
        graphics.drawString(moneyMadeString, textX, textY);

        textY += 20;
        graphics.setFont(FONT);
        graphics.setColor(Color.BLACK);
        graphics.drawString(runtimeString, textX + 1, textY + 1);
        graphics.setColor(YELLOW);
        graphics.drawString(runtimeString, textX, textY);

        textY += 20;
        graphics.setColor(Color.BLACK);
        graphics.drawString(statusString, textX + 1, textY + 1);
        graphics.setColor(YELLOW);
        graphics.drawString(statusString, textX, textY);

        textY += 20;
        graphics.setFont(FONT);
        graphics.setColor(Color.BLACK);
        graphics.drawString(taskString, textX + 1, textY + 1);
        graphics.setColor(YELLOW);
        graphics.drawString(taskString, textX, textY);

        textX = 270;
        textY = 370;
        if (craftGained > 0)
        {
            graphics.setFont(FONT);
            graphics.setColor(Color.BLACK);
            graphics.drawString(craftString, textX + 1, textY + 1);
            graphics.setColor(YELLOW);
            graphics.drawString(craftString, textX, textY);
        }

        if (magicGained > 0)
        {
            textX += 0;
            textY += 20;
            graphics.setFont(FONT);
            graphics.setColor(Color.BLACK);
            graphics.drawString(magicString, textX + 1, textY + 1);
            graphics.setColor(YELLOW);
            graphics.drawString(magicString, textX, textY);
        }

        if (herbloreGained > 0)
        {
            textX += 0;
            textY += 20;
            graphics.setFont(FONT);
            graphics.setColor(Color.BLACK);
            graphics.drawString(herbloreString, textX + 1, textY + 1);
            graphics.setColor(YELLOW);
            graphics.drawString(herbloreString, textX, textY);
        }

        if (fletchingGained > 0)
        {
            textX += 0;
            textY += 20;
            graphics.setFont(FONT);
            graphics.setColor(Color.BLACK);
            graphics.drawString(fletchingString, textX + 1, textY + 1);
            graphics.setColor(YELLOW);
            graphics.drawString(fletchingString, textX, textY);
        }

    }
    private String getRuntimeString(int secondsRunning)
    {
        int hours = secondsRunning / 3600;
        secondsRunning -= (hours * 3600);
        int minutes = secondsRunning / 60;
        secondsRunning -= (minutes * 60);
        int seconds = secondsRunning;
        String secondString = "" + seconds;
        String minuteString = "" + minutes;
        String hourString = "" + hours;
        if (seconds < 10)
        {
            secondString = "0" + seconds;
            if (seconds == 0)
            {
                secondString = "00";
            }
        }
        if (minutes < 10)
        {
            minuteString = "0" + minutes;
            if (minutes == 0)
            {
                minuteString = "00";
            }
        }
        if (hours < 10)
        {
            hourString = "0" + hours;
            if (hours == 0)
            {
                hourString = "00";
            }
        }
        return hourString + ":" + minuteString + ":" + secondString;
    }
}
