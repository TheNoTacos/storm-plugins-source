package net.warp.plugin.warpskiller.Items;

import lombok.Getter;

@Getter
public enum Bars
{
    IRON("Iron", "Iron ore"),
    GOLD("Gold", "Gold ore");
    private String barName;
    private String ores;

    Bars(String barName, String ores)
    {
        this.barName = barName;
        this.ores = ores;
    }
}
