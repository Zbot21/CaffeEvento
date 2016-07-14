package lib;


import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chris on 7/14/16.
 */
public class RotatedLogger<T> {
    public static final int DEFAULT_MAX = 3;
    private Set<T> currentLog;
    private EvictingQueue<Set<T>> logs;

    public RotatedLogger() {
        this(DEFAULT_MAX);
    }

    public RotatedLogger(int numLogs) {
        logs = EvictingQueue.create(numLogs);
        currentLog = new HashSet<>();
        logs.add(currentLog);
    }

    public void add(T element) {
        currentLog.add(element);
    }

    public boolean contains(T element) {
        return logs.stream().anyMatch(l -> l.contains(element));
    }

    public void rotateLog() {
        currentLog = new HashSet<>();
        logs.add(currentLog);
    }
}
