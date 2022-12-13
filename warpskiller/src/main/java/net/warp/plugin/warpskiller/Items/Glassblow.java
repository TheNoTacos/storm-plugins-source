package net.warp.plugin.warpskiller.Items;

import lombok.Getter;

@Getter
public enum Glassblow
{
    BEER_GLASS("Beer glass", 14),
    CANDLE_LANTERN("Lantern", 15),
    OIL_LAMP("Oil Lamp", 16),
    VIAL("Vial", 17),
    FISHBOWL("Fishbowl", 18),
    STAFF_ORB("Orb", 19),
    LANTERN_LENS("Lantern lens", 20);

    private final String glassName;
    private final int widgetChild;

    Glassblow (String glassName, int widgetChild)
    {
        this.glassName = glassName;
        this.widgetChild = widgetChild;
    }
}
