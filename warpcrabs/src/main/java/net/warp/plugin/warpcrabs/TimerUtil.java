package net.warp.plugin.warpcrabs;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Slf4j
@Singleton
public class TimerUtil
{
    public long sleepTime;
    private long startTime;

    public long toMinutes (int milliSeconds) { return TimeUnit.MILLISECONDS.toMinutes(milliSeconds); }
    private long toMilliseconds (int minutes) { return TimeUnit.MINUTES.toMillis(minutes); }
    private void setStartTime()
    {
        log.debug("Setting startTime");
        startTime = new Date().getTime();
    }
    public void setSleepTime(int time)
    {
        log.debug("Sleeping for: " + time + " Minutes" );
        setStartTime();
        sleepTime = toMilliseconds(time);
    }
    public long getElapsedTime()
    {
        return new Date().getTime() - startTime;
    }

}