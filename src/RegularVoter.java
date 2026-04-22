/**
 * RegularVoter.java - Child class demonstrating INHERITANCE & POLYMORPHISM
 * 
 * A regular voter who casts a simple vote.
 * Extends the Voter class and overrides castVote() method.
 */
public class RegularVoter extends Voter {

    // Constructor calls parent constructor using super()
    public RegularVoter(String name, String voterId) {
        super(name, voterId); // INHERITANCE: calling parent constructor
    }

    /**
     * POLYMORPHISM: Overriding the parent's castVote() method.
     * Regular voters get a simple vote message.
     */
    @Override
    public String castVote(String candidate) {
        if (hasVoted) {
            return name + " has already voted! Duplicate vote rejected.";
        }
        hasVoted = true;
        return "[Regular Vote] " + name + " voted for " + candidate;
    }

    @Override
    public String toString() {
        return "Regular Voter: " + name + " [ID: " + voterId + "]";
    }
}
