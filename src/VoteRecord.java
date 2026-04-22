import java.time.LocalDateTime;

public class VoteRecord {

    private final String voterId;
    private final String voterName;
    private final String voterType;
    private final String candidate;
    private final String threadName;
    private final LocalDateTime timestamp;

    public VoteRecord(String voterId, String voterName, String voterType, String candidate, String threadName, LocalDateTime timestamp) {
        this.voterId = voterId;
        this.voterName = voterName;
        this.voterType = voterType;
        this.candidate = candidate;
        this.threadName = threadName;
        this.timestamp = timestamp;
    }

    public String getVoterId() {
        return voterId;
    }

    public String getVoterName() {
        return voterName;
    }

    public String getVoterType() {
        return voterType;
    }

    public String getCandidate() {
        return candidate;
    }

    public String getThreadName() {
        return threadName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
