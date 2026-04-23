/**
 * Voter.java - Base class (Parent class)
 * 
 * CONCEPT: INHERITANCE
 * This is the parent class. RegularVoter and VIPVoter 
 * will inherit (extend) from this class.
 * 
 * CONCEPT: POLYMORPHISM
 * The castVote() method will be overridden in child classes 
 * to give different behavior.
 */
public class Voter {

    // 'protected' means child classes can access these directly
    protected String name;
    protected String voterId;
    protected boolean hasVoted;

    // Constructor
    public Voter(String name, String voterId) {
        this.name = name;
        this.voterId = voterId;
        this.hasVoted = false;
    }

    /**
     * POLYMORPHISM: This method is overridden in child classes.
     * Each child class gives a different vote message.
     */
    public String castVote(String candidate) {
        if (hasVoted) {
            return name + " has already voted!";
        }
        hasVoted = true;
        return name + " voted for " + candidate;
    }

    // Getter methods
    public String getName()     { return name; }
    public String getVoterId()  { return voterId; }
    public boolean hasVoted()   { return hasVoted; }
}
