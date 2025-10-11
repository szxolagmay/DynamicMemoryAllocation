import java.util.*;
public class AllocationLog {
    public static class Entry {
        public String action, processId;
        public int size, start, end;
        public long timestamp;
        public Entry(String action, String pid, int sz, int s, int e) {
            this.action = action; this.processId = pid; this.size = sz; this.start = s; this.end = e;
            this.timestamp = System.currentTimeMillis();
        }
        public String toString() {
            return String.format("[%tT] %s: PID=%s Size=%d Blocks=[%d-%d]", 
                new Date(timestamp), action, processId, size, start, end);
        }
    }
    private final List<Entry> logEntries = new ArrayList<>();
    public void add(String action, String pid, int sz, int s, int e) {
        logEntries.add(new Entry(action, pid, sz, s, e));
    }
    public List<Entry> all() { return logEntries; }
    public void clear() { logEntries.clear(); }
}
