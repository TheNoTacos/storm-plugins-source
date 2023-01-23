package net.warp.plugin.warpskiller.Items;

import lombok.Getter;

@Getter
public enum Herbs
{
    GRIMY_GUAM("Grimy guam leaf", 3),
    GRIMY_MARRENTIL("Grimy marrentil", 5),
    GRIMY_TARROMIN("Grimy tarromin", 11),
    GRIMY_HARRALANDER("Grimy harralander", 20),
    GRIMY_RANARR("Grimy ranarr weed", 25),
    GRIMY_TOADFLAX("Grimy toadflax", 30),
    GRIMY_IRIT("Grimy irit leaf", 40),
    GRIMY_AVANTOE("Grimy avantoe", 48),
    GRIMY_KWUARM("Grimy kwuarm", 54),
    GRIMY_SNAPDRAGON("Grimy snapdragon", 59),
    GRIMY_CADANTINE("Grimy cadantine", 65),
    GRIMY_LANTADYME("Grimy lantadyme", 67),
    GRIMY_DWARF("Grimy dwarf weed", 70),
    GRIMY_TORSTOL("Grimy torstol", 75);

    private final String herbName;
    private final int cleanLevel;

    Herbs(String herbName, int cleanLevel)
    {
        this.herbName = herbName;
        this.cleanLevel = cleanLevel;
    }
}
