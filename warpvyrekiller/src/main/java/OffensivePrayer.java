import lombok.Getter;
import net.runelite.api.Prayer;

@Getter
public enum OffensivePrayer
{
    ULTIMATE_STRENGTH("Ultimate Strength", Prayer.ULTIMATE_STRENGTH),
    PIETY("Piety", Prayer.PIETY);

    private final String prayerName;
    private final Prayer prayer;

    OffensivePrayer(String prayerName, Prayer prayer)
    {
        this.prayerName = prayerName;
        this.prayer = prayer;
    }
}
