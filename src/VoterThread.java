/**
 * VoterThread.java - Demonstrates MULTITHREADING
 * 
 * CONCEPT: MULTITHREADING
 * Each voter casts their vote in a separate thread.
 * This means multiple voters can vote at the same time.
 * 
 * This class extends the built-in Thread class.
 * When we call thread.start(), Java automatically calls the run() method.
 */
public class VoterThread extends Thread {

    private Voter voter;         // The voter (Regular or VIP)
    private String candidate;    // Who they are voting for
    private VotingBooth booth;   // The shared voting booth
    private String result;       // Stores the result message

    // Constructor
    public VoterThread(Voter voter, String candidate, VotingBooth booth) {
        this.voter = voter;
        this.candidate = candidate;
        this.booth = booth;
        this.result = "";
    }

    /**
     * run() is called automatically when thread.start() is used.
     * This is where the voting happens in a separate thread.
     */
    @Override
    public void run() {
        result = booth.vote(candidate, voter);
    }

    // Get the result after the thread finishes
    public String getResult() {
        return result;
    }
}
