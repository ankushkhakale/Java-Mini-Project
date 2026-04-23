import java.util.LinkedHashMap;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * VotingBooth.java - Vote counter with FILE HANDLING
 *
 * CONCEPT: FILE HANDLING
 * Every time "Save to File" is clicked, the current session's
 * results are APPENDED to votes.txt (not overwritten).
 * This means every session's history is preserved in the file.
 *
 * The file is saved to the same folder where the .class files are,
 * using the absolute path so it is always visible in the project directory.
 *
 * CONCEPT: MULTITHREADING (synchronized)
 * The vote() method is synchronized so only one thread
 * can update the vote count at a time.
 */
public class VotingBooth {

    // LinkedHashMap keeps candidates in the order they were added
    private LinkedHashMap<String, Integer> voteCount;

    // Path to votes.txt — set by the applet using getCodeBase()
    private String filePath;

    // Session number — increases every time Reset is called
    private int sessionNumber;

    /**
     * Constructor accepts the file path from the applet.
     * The applet uses getCodeBase().getPath() to get the
     * project folder path (this is allowed inside an applet).
     */
    public VotingBooth(String filePath) {
        voteCount = new LinkedHashMap<>();
        sessionNumber = 1;
        // votes.txt will be saved in the same folder as the .class files
        this.filePath = filePath;
    }

    // Add a new candidate
    public void addCandidate(String name) {
        // Don't add if already exists
        if (!voteCount.containsKey(name)) {
            voteCount.put(name, 0);
        }
    }

    // Remove a candidate
    public void removeCandidate(String name) {
        voteCount.remove(name);
    }

    // Check if a candidate already exists
    public boolean hasCandidate(String name) {
        return voteCount.containsKey(name);
    }

    /**
     * Cast a vote for a candidate.
     * 'synchronized' ensures only one thread votes at a time.
     *
     * CONCEPT: POLYMORPHISM
     * voter.castVote() runs the right version depending on
     * whether voter is a RegularVoter or VIPVoter.
     */
    public synchronized String vote(String candidate, Voter voter) {
        if (!voteCount.containsKey(candidate)) {
            return "Error: Candidate '" + candidate + "' not found!";
        }

        // POLYMORPHISM in action
        String message = voter.castVote(candidate);

        if (voter.hasVoted()) {
            int current = voteCount.get(candidate);
            voteCount.put(candidate, current + 1);
        }

        return message;
    }

    // Get vote count of one candidate
    public int getVotes(String candidate) {
        return voteCount.getOrDefault(candidate, 0);
    }

    // Get all candidate names as array
    public String[] getCandidates() {
        return voteCount.keySet().toArray(new String[0]);
    }

    // Check if there are any candidates
    public boolean hasCandidates() {
        return !voteCount.isEmpty();
    }

    /**
     * Get total votes cast across all candidates.
     */
    public int getTotalVotes() {
        int total = 0;
        for (int v : voteCount.values()) {
            total += v;
        }
        return total;
    }

    /**
     * FILE HANDLING: APPEND this session's results to votes.txt
     *
     * Each time Save is clicked, a new session block is added to the file.
     * Old sessions are never deleted — the file keeps growing.
     *
     * Format of each session block:
     * ──────────────────────────
     * SESSION 1 - 23-Apr-2025 11:30 AM
     * Alice   : 3 votes
     * Bob     : 2 votes
     * Charlie : 1 vote
     * Total   : 6 votes
     * ──────────────────────────
     */
    public String appendSessionToFile() throws IOException {
        // Get current date and time for the session header
        String timestamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a").format(new Date());

        // Open file in APPEND mode (true = append, do not overwrite)
        FileWriter fw = new FileWriter(filePath, true);

        fw.write("--------------------------------------------------\n");
        fw.write("SESSION " + sessionNumber + " - " + timestamp + "\n");
        fw.write("--------------------------------------------------\n");

        for (String candidate : voteCount.keySet()) {
            fw.write(candidate + " : " + voteCount.get(candidate) + " votes\n");
        }

        fw.write("Total Votes : " + getTotalVotes() + "\n");
        fw.write("--------------------------------------------------\n\n");

        fw.close(); // Always close the file

        return filePath; // Return path so GUI can show it to user
    }

    /**
     * Get the results as a string for display in the log area.
     */
    public String getResults() {
        if (voteCount.isEmpty()) {
            return "No candidates found. Please add candidates first.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("====== SESSION " + sessionNumber + " RESULTS ======\n");

        for (String candidate : voteCount.keySet()) {
            sb.append("  " + candidate + " : " + voteCount.get(candidate) + " votes\n");
        }

        sb.append("  Total : " + getTotalVotes() + " votes\n");
        sb.append("=====================================");
        return sb.toString();
    }

    /**
     * Reset: Clear all candidates and votes completely.
     * The session number increases so next session is tracked separately.
     */
    public void reset() {
        voteCount.clear(); // Remove all candidates
        sessionNumber++;   // Move to next session number
    }

    // Get current session number (used by GUI)
    public int getSessionNumber() {
        return sessionNumber;
    }

    /**
     * Find and return the winner — the candidate with the most votes.
     * If no votes have been cast yet, returns a message saying so.
     * If two candidates are tied, it shows a tie message.
     */
    public String getWinner() {
        if (voteCount.isEmpty()) {
            return "No candidates to determine a winner.";
        }

        String winner = null;
        int maxVotes = -1;
        boolean tie = false;

        // Loop through all candidates to find the highest vote count
        for (String candidate : voteCount.keySet()) {
            int votes = voteCount.get(candidate);
            if (votes > maxVotes) {
                maxVotes = votes;
                winner = candidate;
                tie = false;
            } else if (votes == maxVotes) {
                // Two or more candidates have the same top vote count
                tie = true;
            }
        }

        if (maxVotes == 0) {
            return ">>> No votes cast yet — no winner.";
        }

        if (tie) {
            return ">>> It's a TIE! Multiple candidates have " + maxVotes + " votes.";
        }

        return ">>> WINNER: " + winner + " with " + maxVotes + " votes! <<<";
    }
}
