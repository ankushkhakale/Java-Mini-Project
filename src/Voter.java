/**
 * Voter.java - Base class demonstrating INHERITANCE
 * 
 * This is the parent class for all types of voters.
 * It contains common properties like name and voterId.
 * 
 * CONCEPT: Inheritance - RegularVoter and VIPVoter will extend this class.
 * CONCEPT: Polymorphism - The castVote() method is overridden in child classes.
 */
public class Voter {

    // Protected so child classes can access these fields directly
    protected String name;
    protected String voterId;
    protected boolean hasVoted;

    // Constructor to initialize voter details
    public Voter(String name, String voterId) {
        this.name = name;
        this.voterId = voterId;
        this.hasVoted = false; // No one has voted initially
    }

    /**
     * POLYMORPHISM: This method will be overridden by child classes
     * to provide different voting behavior.
     * 
     * @param candidate - The candidate being voted for
     * @return A message describing the vote action
     */
    public String castVote(String candidate) {
        if (hasVoted) {
            return name + " has already voted!";
        }
        hasVoted = true;
        return name + " (General Voter) voted for " + candidate;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getVoterId() {
        return voterId;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    // Reset vote status (used when restarting election)
    public void resetVote() {
        this.hasVoted = false;
    }

    /**
     * POLYMORPHISM: toString() is overridden from Object class
     * to give a readable description of the voter.
     */
    @Override
    public String toString() {
        return "Voter: " + name + " [ID: " + voterId + "]";
    }
}
