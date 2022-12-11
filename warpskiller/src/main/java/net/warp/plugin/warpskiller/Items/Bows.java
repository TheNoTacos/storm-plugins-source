package net.warp.plugin.warpskiller.Items;

import lombok.Getter;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;

@Getter
public enum Bows {

    SHORTBOW("shortbow (u)"),
    LONGBOW("longbow (u)");

    private String bowName;

    Bows (String bowName) { this.bowName = bowName; }


}
