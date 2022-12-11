package net.warp.plugin.warpcutter;

import lombok.Getter;
@Getter
public enum Tree
{
    TREE("Tree"),
    OAK("Oak"),
    WILLOW("Willow"),
    MAPLE("Maple tree"),
    YEW("Yew"),
    MAGIC( "Magic tree");
        private final String treeName;
        Tree(String treeName)
        {
            this.treeName = treeName;
        }

}
