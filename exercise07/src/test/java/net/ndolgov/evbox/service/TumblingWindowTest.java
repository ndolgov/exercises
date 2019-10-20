package net.ndolgov.evbox.service;

import net.ndolgov.evbox.domain.Statistics;
import net.ndolgov.evbox.service.Windows.WindowStats;
import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;

import static net.ndolgov.evbox.service.Windows.tumblingWindow;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TumblingWindowTest {
    @Test
    public void testWindowStatsAccumulation() {
        final Statistics zero = new Statistics(0, 0);

        final ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
        when(executor.scheduleAtFixedRate(any(), anyLong(), anyLong(), any())).thenReturn(null);

        final WindowStats<Statistics> stats = tumblingWindow(zero, 60, executor);
        assertEquals(zero, stats.stats());

        stats.onChange(Statistics::incStarted);
        assertEquals(new Statistics(1, 0), stats.stats());

        stats.onChange(Statistics::incFinished);
        assertEquals(new Statistics(1, 1), stats.stats());

        stats.onPeriodEnd();
        assertEquals(zero, stats.stats());

        stats.onChange(Statistics::incStarted);
        assertEquals(new Statistics(1, 0), stats.stats());

        stats.onChange(Statistics::incFinished);
        assertEquals(new Statistics(1, 1), stats.stats());
    }
}