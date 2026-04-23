/**
 * RegularVoter.java - Child class of Voter
 * 
 * CONCEPT: INHERITANCE - This class extends Voter (inherits name, voterId, etc.)
 * CONCEPT: POLYMORPHISM - castVote() is overridden with different behavior.
 */
public class RegularVoter extends Voter {

    // Constructor - calls the parent's constructor using super()
    public RegularVoter(String name, String voterId) {
        super(name, voterId);
    }

    // INHERITANCE: Overrides castVote() from the parent Voter class
    @Override
    public String castVote(String candidate) {
        if (hasVoted) {
            return name + " has already voted!";
        }
        hasVoted = true;
        return "[Regular] " + name + " voted for " + candidate;
    }
}
