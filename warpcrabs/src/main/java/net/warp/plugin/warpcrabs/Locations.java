package net.warp.plugin.warpcrabs;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
@Getter
public enum Locations
{
    SANDCRAB1("Sandcrab1", new WorldPoint(1737, 3469, 0), new WorldPoint(1760, 3509, 0), "Sand Crab", "Sandy rocks"),
    SANDCRAB2("Sandcrab2", new WorldPoint(1749, 3469, 0), new WorldPoint(1760, 3509, 0), "Sand Crab", "Sandy rocks"),
    SANDCRAB3("Sandcrab3", new WorldPoint(1765, 3468, 0), new WorldPoint(1760, 3509, 0), "Sand Crab", "Sandy rocks"),
    SANDCRAB4("Sandcrab4", new WorldPoint(1773, 3461, 0), new WorldPoint(1760, 3509, 0), "Sand Crab", "Sandy rocks"),
    NORTH_EAST_ISLAND("North East Island", new WorldPoint(1780, 3483, 0), new WorldPoint(1748, 3408, 0), "Sand Crab", "Sandy rocks"),
    NORTH_WEST_ISLAND("North West Island", new WorldPoint(1764, 3445, 0), new WorldPoint(1777, 3409, 0), "Sand Crab", "Sandy rocks"),
    WEST_ISLAND("West Island", new WorldPoint(1751, 3425, 0), new WorldPoint(1792, 3392, 0), "Sand Crab", "Sandy rocks"),
    SOUTH_ISLAND("South Island", new WorldPoint(1768, 3409, 0), new WorldPoint(1767, 3448, 0), "Sand Crab", "Sandy rocks"),
    EAST_ISLAND("East Island", new WorldPoint(1768, 3404, 0), new WorldPoint(1750, 3428, 0), "Sand Crab", "Sandy rocks"),
    NORTH_FOSSIL("North Island", new WorldPoint(3718, 3896, 0), new WorldPoint(3716, 3815, 0), "Ammonite Crab", "Fossil Rock"),
    SOUTH_EAST_FOSSIL("South East Island", new WorldPoint(3734, 3846, 0), new WorldPoint(3716, 3815, 0), "Ammonite Crab", "Fossil Rock"),
    MIDDLE_FOSSIL("Middle Island", new WorldPoint(3717, 3881, 0), new WorldPoint(3716, 3815, 0), "Ammonite Crab", "Fossil Rock");

    private final String locationName;
    private final WorldPoint killLocation;
    private final WorldPoint resetLocation;
    private final String crabName;
    private final String rockName;

    Locations(String locationName, WorldPoint killLocation, WorldPoint resetLocation, String crabName, String rockName)
    {
        this.locationName = locationName;
        this.killLocation = killLocation;
        this.resetLocation = resetLocation;
        this.crabName = crabName;
        this.rockName = rockName;
    }
}
