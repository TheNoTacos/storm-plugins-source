package net.warp.plugin.warpcutter;

import lombok.Getter;

@Getter
public enum PluginStatus {
    IDLE("Idle"),
    CHOPPING("Chopping"),
    BANK("Banking"),
    DROPPING("Dropping"),
    WALKING("Walking");

    private final String status;

    PluginStatus(String status)
    {
        this.status = status;
    }
}
