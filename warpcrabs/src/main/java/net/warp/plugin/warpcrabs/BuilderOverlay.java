package net.warp.plugin.warpcrabs;

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

    private final WarpCrabsPlugin plugin;
    private final WarpCrabsConfig config;

    @Inject
    private Client client;

    @Inject
    BuilderOverlay(Client client, WarpCrabsPlugin plugin, WarpCrabsConfig config)
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
        String mainText = "Warp Sand Crabs V0.1.5";
        String statusString = "Status: " + plugin.status;
        String runtimeString = "Runtime: " + getRuntimeString(secondsRunning);
        String timerString = "Time on location: " + plugin.timerUtil.toMinutes((int)plugin.timerUtil.getElapsedTime());

        Rectangle slotBounds;
        double hoursAsDouble = (double) secondsRunning / 3600.0;
        int rangeGained = (Skills.getExperience(Skill.RANGED) - plugin.rangeExp);
        int magicGained = (Skills.getExperience(Skill.MAGIC) - plugin.mageExp);
        int attackGained = (Skills.getExperience(Skill.ATTACK) - plugin.attackExp);
        int strengthGained = (Skills.getExperience(Skill.STRENGTH) - plugin.strengthExp);
        int defenceGained = (Skills.getExperience(Skill.DEFENCE) - plugin.defenceExp);



        String moneyMadeString = "Profit: " + plugin.alchProfit + " (" + (int) (plugin.alchProfit / hoursAsDouble / 1000) + "k/hr)";
        String rangeString =  "Range exp: " + rangeGained + " (" + (int) (rangeGained / hoursAsDouble / 1000) + "k/hr)";
        String magicString = "Magic exp: "  + magicGained + " (" + (int) (magicGained / hoursAsDouble / 1000) + "k/hr)";
        String attackString = "Attack exp: "  + attackGained + " (" + (int) (attackGained / hoursAsDouble / 1000) + "k/hr)";
        String strengthString = "Strength exp: "  + strengthGained + " (" + (int) (strengthGained / hoursAsDouble / 1000) + "k/hr)";
        String defenceString = "Defence exp: "  + defenceGained + " (" + (int) (defenceGained / hoursAsDouble / 1000) + "k/hr)";

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
        graphics.setFont(FONT);
        graphics.setColor(Color.BLACK);
        graphics.drawString(statusString, textX + 1, textY + 1);
        graphics.setColor(YELLOW);
        graphics.drawString(statusString, textX, textY);

        textY += 20;
        graphics.setFont(FONT);
        graphics.setColor(Color.BLACK);
        graphics.drawString(runtimeString, textX + 1, textY + 1);
        graphics.setColor(YELLOW);
        graphics.drawString(runtimeString, textX, textY);

        textY += 20;
        graphics.setColor(Color.BLACK);
        graphics.drawString(timerString, textX + 1, textY + 1);
        graphics.setColor(YELLOW);
        graphics.drawString(timerString, textX, textY);

        textY += 20;
        graphics.setColor(Color.BLACK);
        graphics.drawString(moneyMadeString, textX + 1, textY + 1);
        graphics.setColor(YELLOW);
        graphics.drawString(moneyMadeString, textX, textY);

        textX = 270;
        textY = 370;
        if (rangeGained > 0)
        {
            graphics.setFont(FONT);
            graphics.setColor(Color.BLACK);
            graphics.drawString(rangeString, textX + 1, textY + 1);
            graphics.setColor(YELLOW);
            graphics.drawString(rangeString, textX, textY);
        }

        if (magicGained > 0)
        {
            textY += 20;
            graphics.setFont(FONT);
            graphics.setColor(Color.BLACK);
            graphics.drawString(magicString, textX + 1, textY + 1);
            graphics.setColor(YELLOW);
            graphics.drawString(magicString, textX, textY);
        }

        if (attackGained > 0)
        {
            textX += 0;
            textY += 20;
            graphics.setFont(FONT);
            graphics.setColor(Color.BLACK);
            graphics.drawString(attackString, textX + 1, textY + 1);
            graphics.setColor(YELLOW);
            graphics.drawString(attackString, textX, textY);
        }

        if (strengthGained > 0)
        {
            textX += 0;
            textY += 20;
            graphics.setFont(FONT);
            graphics.setColor(Color.BLACK);
            graphics.drawString(strengthString, textX + 1, textY + 1);
            graphics.setColor(YELLOW);
            graphics.drawString(strengthString, textX, textY);
        }

        if (defenceGained > 0)
        {
            textX += 0;
            textY += 20;
            graphics.setFont(FONT);
            graphics.setColor(Color.BLACK);
            graphics.drawString(defenceString, textX + 1, textY + 1);
            graphics.setColor(YELLOW);
            graphics.drawString(defenceString, textX, textY);
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
