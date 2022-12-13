package net.warp.plugin.warpskiller.Items;

import lombok.Getter;

@Getter
public enum Logs
{
    LOGS("Logs"),
    OAK("Oak "),
    WILLOW("Willow "),
    MAPLE("Maple "),
    YEW("Yew "),
    MAGIC("Magic ");

    private String logName;

    Logs (String logName)
    {
        this.logName = logName;
    }

}
