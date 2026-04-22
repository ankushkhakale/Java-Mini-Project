/**
 * VIPVoter.java - Another child class demonstrating INHERITANCE & POLYMORPHISM
 * 
 * A VIP voter who gets a priority acknowledgment when voting.
 * Extends the Voter class and overrides castVote() method differently.
 */
public class VIPVoter extends Voter {

    // Constructor calls parent constructor using super()
    public VIPVoter(String name, String voterId) {
        super(name, voterId); // INHERITANCE: calling parent constructor
    }

    /**
     * POLYMORPHISM: Same method name as RegularVoter but different behavior.
     * VIP voters get a priority acknowledgment message.
     */
    @Override
    public String castVote(String candidate) {
        if (hasVoted) {
            return name + " (VIP) has already voted! Duplicate vote rejected.";
        }
        hasVoted = true;
        return "[VIP Priority Vote] " + name + " voted for " + candidate + " ★";
    }

    @Override
    public String getVoterType() {
        return "VIP";
    }

    @Override
    public String toString() {
        return "VIP Voter: " + name + " [ID: " + voterId + "] ★";
    }
}
