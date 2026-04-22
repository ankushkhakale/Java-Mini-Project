/**
 * VoterThread.java - Demonstrates MULTITHREADING
 * 
 * Each voter runs in a separate thread to simulate real-world
 * concurrent voting. This class extends Thread.
 * 
 * CONCEPT: Multithreading - Multiple voters vote at the same time.
 * CONCEPT: Polymorphism - voter.castVote() is called, and the actual
 *          method that runs depends on whether it's a RegularVoter or VIPVoter.
 */
public class VoterThread extends Thread {

    private Voter voter;          // The voter (can be RegularVoter or VIPVoter)
    private String candidate;     // The candidate they are voting for
    private VotingBooth booth;    // The shared voting booth
    private String result;        // Stores the result message after voting
    private boolean useDeadlock;  // Whether to use deadlock-prone method
    private int threadNumber;     // 1 or 2, used for deadlock demo

    /**
     * Constructor for normal (non-deadlock) voting
     */
    public VoterThread(Voter voter, String candidate, VotingBooth booth) {
        this.voter = voter;
        this.candidate = candidate;
        this.booth = booth;
        this.result = "";
        this.useDeadlock = false;
        this.threadNumber = 0;
    }

    /**
     * Constructor for deadlock demonstration
     * threadNumber tells which lock order to use (1 or 2)
     */
    public VoterThread(Voter voter, String candidate, VotingBooth booth,
                       boolean useDeadlock, int threadNumber) {
        this.voter = voter;
        this.candidate = candidate;
        this.booth = booth;
        this.result = "";
        this.useDeadlock = useDeadlock;
        this.threadNumber = threadNumber;
    }

    /**
     * MULTITHREADING: The run() method executes when thread.start() is called.
     * Each thread independently casts a vote.
     */
    @Override
    public void run() {
        if (useDeadlock) {
            // Deadlock demo: alternating lock order
            if (threadNumber == 1) {
                result = booth.deadlockVote_Thread1(candidate, voter);
            } else {
                result = booth.deadlockVote_Thread2(candidate, voter);
            }
        } else {
            // Normal voting (safe or unsafe based on booth settings)
            result = booth.vote(candidate, voter);
        }
    }

    // Get the result message after thread finishes
    public String getResult() {
        return result;
    }

    // Get the voter object
    public Voter getVoter() {
        return voter;
    }
}
