package net.warp.plugin.warpskiller.Items;

import lombok.Getter;

@Getter
public enum Amethyst
{
    BOLT_TIPS("bolt tips", 14),
    ARROW_TIPS("arrowtips", 15),
    JAVELIN_HEAD("javelin Heads", 16),
    DART_TIPS("dart tip", 17);

    private final String name;
    private final int widgetChild;

    Amethyst (String name, int widgetChild)
    {
        this.name = name;
        this.widgetChild = widgetChild;
    }

}
