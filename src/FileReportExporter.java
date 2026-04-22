import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class FileReportExporter {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String exportResultsCsv(String outputDir, Map<String, Integer> snapshot, int totalVotes) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create output directory: " + outputDir);
        }

        File file = new File(dir, "election_results.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Candidate,Votes,VoteSharePercent");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : snapshot.entrySet()) {
                double share = totalVotes == 0 ? 0.0 : (entry.getValue() * 100.0) / totalVotes;
                writer.write(csv(entry.getKey()) + "," + entry.getValue() + "," + String.format("%.2f", share));
                writer.newLine();
            }
            writer.write("TOTAL," + totalVotes + ",100.00");
            writer.newLine();
        }
        return file.getAbsolutePath();
    }

    public String exportAuditCsv(String outputDir, List<VoteRecord> records) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create output directory: " + outputDir);
        }

        File file = new File(dir, "vote_audit.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Timestamp,VoterId,VoterName,VoterType,Candidate,Thread");
            writer.newLine();
            for (VoteRecord record : records) {
                writer.write(csv(record.getTimestamp().format(formatter)) + ","
                        + csv(record.getVoterId()) + ","
                        + csv(record.getVoterName()) + ","
                        + csv(record.getVoterType()) + ","
                        + csv(record.getCandidate()) + ","
                        + csv(record.getThreadName()));
                writer.newLine();
            }
        }
        return file.getAbsolutePath();
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
