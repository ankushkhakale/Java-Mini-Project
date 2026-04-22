import java.util.Map;

public class ElectionAnalytics {

    public String buildAnalyticsReport(Map<String, Integer> snapshot, int totalVotes, int registeredVoters) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== ELECTION ANALYTICS =====\n");
        sb.append("Registered Voters: ").append(registeredVoters).append("\n");
        sb.append("Total Votes: ").append(totalVotes).append("\n");

        double turnout = registeredVoters == 0 ? 0.0 : (totalVotes * 100.0) / registeredVoters;
        sb.append(String.format("Turnout: %.2f%%\n", turnout));

        String leadingCandidate = "N/A";
        int maxVotes = -1;
        for (Map.Entry<String, Integer> entry : snapshot.entrySet()) {
            String candidate = entry.getKey();
            int votes = entry.getValue();
            double share = totalVotes == 0 ? 0.0 : (votes * 100.0) / totalVotes;
            sb.append(String.format("%s -> %d votes (%.2f%%)\n", candidate, votes, share));
            if (votes > maxVotes) {
                maxVotes = votes;
                leadingCandidate = candidate;
            }
        }

        if (maxVotes >= 0) {
            sb.append("Leading Candidate: ").append(leadingCandidate).append(" (" + maxVotes + " votes)\n");
        }
        sb.append("==============================");
        return sb.toString();
    }
}
