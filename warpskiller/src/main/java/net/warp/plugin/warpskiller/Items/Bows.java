package net.warp.plugin.warpskiller.Items;

import lombok.Getter;

@Getter
public enum Bows
{
    SHORTBOW("shortbow (u)"),
    LONGBOW("longbow (u)");

    private String bowName;

    Bows (String bowName)
    {
        this.bowName = bowName;
    }
}
