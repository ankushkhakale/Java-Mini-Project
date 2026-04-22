import java.util.HashMap;

/**
 * VotingBooth.java - Shared resource demonstrating RACE CONDITION & DEADLOCK
 * 
 * This class represents the voting booth where votes are counted.
 * Multiple threads (voters) access this shared resource simultaneously.
 * 
 * CONCEPT: Race Condition - When multiple threads modify voteCount without synchronization.
 * CONCEPT: Deadlock - Demonstrated using two locks that can cause circular waiting.
 * CONCEPT: Synchronized - Used to fix race conditions.
 */
public class VotingBooth {

    // Shared data: candidate names and their vote counts
    private HashMap<String, Integer> voteCount;

    // Two lock objects used to demonstrate DEADLOCK
    private final Object lockBooth = new Object();   // Lock for the voting booth
    private final Object lockCounter = new Object();  // Lock for the vote counter

    // Flag to choose between safe and unsafe (race condition) voting
    private boolean safeMode;

    // Flag to simulate deadlock scenario
    private boolean deadlockMode;

    // Total number of votes cast (shared variable - prone to race condition)
    private int totalVotes;

    // Constructor
    public VotingBooth() {
        voteCount = new HashMap<>();
        totalVotes = 0;
        safeMode = true;       // Default: safe mode ON
        deadlockMode = false;  // Default: deadlock simulation OFF
    }

    // Add a candidate to the election
    public void addCandidate(String name) {
        voteCount.put(name, 0);
    }

    // Set safe mode ON or OFF
    public void setSafeMode(boolean safe) {
        this.safeMode = safe;
    }

    // Set deadlock mode ON or OFF
    public void setDeadlockMode(boolean deadlock) {
        this.deadlockMode = deadlock;
    }

    /**
     * Cast a vote for a candidate.
     * 
     * If safeMode is ON  → synchronized method prevents race condition.
     * If safeMode is OFF → no synchronization, race condition can happen.
     */
    public String vote(String candidate, Voter voter) {
        if (safeMode) {
            return voteSafe(candidate, voter);
        } else {
            return voteUnsafe(candidate, voter);
        }
    }

    /**
     * SAFE VOTING - Uses synchronized to prevent race condition.
     * Only one thread can execute this block at a time.
     */
    private String voteSafe(String candidate, Voter voter) {
        // synchronized block ensures mutual exclusion
        synchronized (lockBooth) {
            if (!voteCount.containsKey(candidate)) {
                return "Error: Candidate '" + candidate + "' not found!";
            }

            // Polymorphism in action: castVote() behaves differently
            // for RegularVoter and VIPVoter
            String message = voter.castVote(candidate);

            if (voter.hasVoted()) {
                // Safely update the vote count
                voteCount.put(candidate, voteCount.get(candidate) + 1);
                totalVotes++;
            }

            return message;
        }
    }

    /**
     * UNSAFE VOTING - No synchronization!
     * 
     * RACE CONDITION EXPLAINED:
     * When two threads read voteCount at the same time, both see the same
     * old value. Both add 1 and write back. So instead of +2, we get +1.
     * This is called a "lost update" — a classic race condition.
     */
    private String voteUnsafe(String candidate, Voter voter) {
        // ⚠ NO synchronized keyword — race condition is possible here!
        if (!voteCount.containsKey(candidate)) {
            return "Error: Candidate '" + candidate + "' not found!";
        }

        String message = voter.castVote(candidate);

        if (voter.hasVoted()) {
            // ⚠ RACE CONDITION: Two threads can read the same value here
            int current = voteCount.get(candidate);

            // Simulating a small delay to increase chance of race condition
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Thread was interrupted, continue silently
            }

            // ⚠ Both threads may write the same incremented value
            voteCount.put(candidate, current + 1);
            totalVotes++;
        }

        return message;
    }

    /**
     * DEADLOCK DEMONSTRATION
     * 
     * DEADLOCK EXPLAINED:
     * Thread 1 locks 'lockBooth' first, then tries to lock 'lockCounter'.
     * Thread 2 locks 'lockCounter' first, then tries to lock 'lockBooth'.
     * Both threads wait for each other forever → DEADLOCK!
     * 
     * This method is called by Thread 1 (locks booth → then counter).
     */
    public String deadlockVote_Thread1(String candidate, Voter voter) {
        System.out.println(voter.getName() + " trying to lock BOOTH...");

        synchronized (lockBooth) {
            System.out.println(voter.getName() + " locked BOOTH, waiting for COUNTER...");

            // Small delay to ensure both threads grab one lock each
            try { Thread.sleep(100); } catch (InterruptedException e) { }

            synchronized (lockCounter) {
                System.out.println(voter.getName() + " got both locks!");
                String msg = voter.castVote(candidate);
                if (voter.hasVoted() && voteCount.containsKey(candidate)) {
                    voteCount.put(candidate, voteCount.get(candidate) + 1);
                    totalVotes++;
                }
                return msg;
            }
        }
    }

    /**
     * This method is called by Thread 2 (locks counter → then booth).
     * Notice the REVERSE lock order compared to deadlockVote_Thread1.
     */
    public String deadlockVote_Thread2(String candidate, Voter voter) {
        System.out.println(voter.getName() + " trying to lock COUNTER...");

        synchronized (lockCounter) {
            System.out.println(voter.getName() + " locked COUNTER, waiting for BOOTH...");

            // Small delay to ensure both threads grab one lock each
            try { Thread.sleep(100); } catch (InterruptedException e) { }

            synchronized (lockBooth) {
                System.out.println(voter.getName() + " got both locks!");
                String msg = voter.castVote(candidate);
                if (voter.hasVoted() && voteCount.containsKey(candidate)) {
                    voteCount.put(candidate, voteCount.get(candidate) + 1);
                    totalVotes++;
                }
                return msg;
            }
        }
    }

    // Get the vote count for a specific candidate
    public int getVotes(String candidate) {
        return voteCount.getOrDefault(candidate, 0);
    }

    // Get total votes cast
    public int getTotalVotes() {
        return totalVotes;
    }

    // Get all results as a formatted string
    public String getResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== ELECTION RESULTS =====\n");
        for (String candidate : voteCount.keySet()) {
            sb.append(candidate).append(": ").append(voteCount.get(candidate)).append(" votes\n");
        }
        sb.append("Total Votes Recorded: ").append(totalVotes).append("\n");
        sb.append("============================");
        return sb.toString();
    }

    // Reset all votes (for restarting)
    public void reset() {
        for (String key : voteCount.keySet()) {
            voteCount.put(key, 0);
        }
        totalVotes = 0;
    }
}
