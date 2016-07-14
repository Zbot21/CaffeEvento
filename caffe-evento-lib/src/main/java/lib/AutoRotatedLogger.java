package lib;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by chris on 7/14/16.
 */
public class AutoRotatedLogger<T> extends RotatedLogger<T> {
    private RotatedLogger<T> delegate;
    private ScheduledExecutorService executorService;

    public static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;
    public static final long DEFAULT_DELAY = 100;
    public static final long DEFAULT_PERIOD = 120;

    public AutoRotatedLogger() {
        this(DEFAULT_DELAY, DEFAULT_PERIOD, DEFAULT_UNIT, DEFAULT_MAX);
    }

    public AutoRotatedLogger(ScheduledExecutorService executorService, long delay, long period, TimeUnit unit, int numLogs) {
        this.executorService = executorService;
        delegate = new RotatedLogger<>(numLogs);

        executorService.scheduleAtFixedRate(() -> delegate.rotateLog(), delay, period, unit);
    }

    public AutoRotatedLogger(long delay, long period, TimeUnit unit, int numLogs) {
        this(Executors.newScheduledThreadPool(1), delay, period, unit, numLogs);
    }

    @Override
    public void add(T element) {
        delegate.add(element);
    }

    @Override
    public boolean contains(T element) {
        return delegate.contains(element);
    }

    @Override
    public void rotateLog() {
        delegate.rotateLog();
    }

}
