import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditTrail {

    private final List<String> events;
    private final List<VoteRecord> votes;
    private final DateTimeFormatter formatter;

    public AuditTrail() {
        this.events = Collections.synchronizedList(new ArrayList<String>());
        this.votes = Collections.synchronizedList(new ArrayList<VoteRecord>());
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public void logEvent(String event) {
        events.add("[" + LocalDateTime.now().format(formatter) + "] " + event);
    }

    public void logVote(VoteRecord record) {
        votes.add(record);
        logEvent("VOTE | " + record.getVoterId() + " | " + record.getVoterType() + " | " + record.getCandidate() + " | " + record.getThreadName());
    }

    public List<String> getEventsSnapshot() {
        synchronized (events) {
            return new ArrayList<>(events);
        }
    }

    public List<VoteRecord> getVotesSnapshot() {
        synchronized (votes) {
            return new ArrayList<>(votes);
        }
    }

    public String getRecentEventsText(int maxCount) {
        List<String> snapshot = getEventsSnapshot();
        int start = Math.max(0, snapshot.size() - maxCount);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < snapshot.size(); i++) {
            sb.append(snapshot.get(i)).append("\n");
        }
        return sb.toString();
    }

    public void clear() {
        events.clear();
        votes.clear();
    }
}
