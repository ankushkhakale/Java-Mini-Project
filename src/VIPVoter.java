/**
 * VIPVoter.java - Another child class of Voter
 * 
 * CONCEPT: INHERITANCE - This class also extends Voter.
 * CONCEPT: POLYMORPHISM - castVote() gives a different (priority) message.
 */
public class VIPVoter extends Voter {

    // Constructor - calls the parent's constructor using super()
    public VIPVoter(String name, String voterId) {
        super(name, voterId);
    }

    // POLYMORPHISM: VIP voters get a special priority message
    @Override
    public String castVote(String candidate) {
        if (hasVoted) {
            return name + " (VIP) has already voted!";
        }
        hasVoted = true;
        return "[VIP] " + name + " voted for " + candidate + " (Priority)";
    }
}
