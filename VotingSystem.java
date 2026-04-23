import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * VotingSystem.java - Main Applet (Entry Point)
 *
 * CONCEPTS DEMONSTRATED:
 * 1. INHERITANCE    → RegularVoter extends Voter
 * 2. POLYMORPHISM   → castVote() behaves differently per voter type
 * 3. MULTITHREADING → Each vote runs in its own Thread
 * 4. FILE HANDLING  → Results are APPENDED to votes.txt (session-wise)
 *
 * HOW TO RUN:
 *   javac *.java
 *   appletviewer VotingSystem.html
 */
public class VotingSystem extends Applet implements ActionListener {

    // ─── INPUT FIELDS ───
    private TextField voterNameField;        // Voter's name input
    private TextField newCandidateField;     // New candidate name input

    // ─── DROPDOWNS ───
    private Choice candidateChoice;          // Select candidate to vote for
    private Choice removeCandidateChoice;    // Select candidate to remove

    // ─── BUTTONS ───
    private Button voteBtn;
    private Button resultBtn;
    private Button saveBtn;       // Appends session to votes.txt
    private Button resetBtn;      // Clears everything for a new session
    private Button addCandBtn;    // Add a new candidate
    private Button removeCandBtn; // Remove a candidate

    // ─── LOG AREA ───
    private TextArea logArea;     // Displays all messages and results

    // ─── DATA ───
    private VotingBooth booth;
    private int voterIdCounter;

    // ─────────────────────────────────────────────
    // init() — Called once when the applet starts
    // ─────────────────────────────────────────────
    @Override
    public void init() {
        // getCodeBase().getPath() returns the project folder path.
        // Applets are allowed to use getCodeBase() unlike System.getProperty().
        String filePath = getCodeBase().getPath() + "votes.txt";
        booth = new VotingBooth(filePath);

        // Applet window settings
        setSize(680, 560);
        setLayout(new BorderLayout(6, 6));
        setBackground(new Color(235, 240, 245));

        // Add all panels
        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        // Show welcome message
        log("======================================");
        log("   Welcome to the Voting System!");
        log("   Session 1 started.");
        log("======================================");
        log("Add candidates below, then cast votes.");
        log("");
    }

    // ─────────────────────────────────────────────
    // PANEL BUILDERS
    // ─────────────────────────────────────────────

    /**
     * Top panel — title + all input fields (voting + candidate management).
     */
    private Panel buildHeaderPanel() {
        Panel wrapper = new Panel(new BorderLayout());
        wrapper.setBackground(new Color(30, 39, 46));

        // Title only — no subtitle
        Label title = new Label("  VOTING SYSTEM", Label.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
        wrapper.add(title, BorderLayout.NORTH);

        // ── Form rows (4 rows: name, vote-for, add candidate, remove candidate) ──
        Panel form = new Panel(new GridLayout(4, 2, 6, 5));
        form.setBackground(new Color(44, 62, 80));

        // Row 1: Voter name
        Label nameLabel = new Label("  Voter Name:");
        styleLabel(nameLabel);
        form.add(nameLabel);

        voterNameField = new TextField();
        voterNameField.setFont(new Font("Arial", Font.PLAIN, 13));
        form.add(voterNameField);

        // Row 2: Select candidate to vote for
        Label selectLabel = new Label("  Vote For:");
        styleLabel(selectLabel);
        form.add(selectLabel);

        candidateChoice = new Choice();
        candidateChoice.setFont(new Font("Arial", Font.PLAIN, 13));
        form.add(candidateChoice);

        // Row 3: Add candidate
        Label addLabel = new Label("  Add Candidate:");
        styleLabel(addLabel);
        form.add(addLabel);

        Panel addRow = new Panel(new BorderLayout(4, 0));
        newCandidateField = new TextField();
        newCandidateField.setFont(new Font("Arial", Font.PLAIN, 13));
        addCandBtn = new Button("Add");
        addCandBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addCandBtn.setBackground(new Color(39, 174, 96));
        addCandBtn.setForeground(Color.WHITE);
        addCandBtn.addActionListener(this);
        addRow.add(newCandidateField, BorderLayout.CENTER);
        addRow.add(addCandBtn, BorderLayout.EAST);
        form.add(addRow);

        // Row 5: Remove candidate
        Label removeLabel = new Label("  Remove Candidate:");
        styleLabel(removeLabel);
        form.add(removeLabel);

        Panel removeRow = new Panel(new BorderLayout(4, 0));
        removeCandidateChoice = new Choice();
        removeCandidateChoice.setFont(new Font("Arial", Font.PLAIN, 13));
        removeCandBtn = new Button("Remove");
        removeCandBtn.setFont(new Font("Arial", Font.BOLD, 12));
        removeCandBtn.setBackground(new Color(192, 57, 43));
        removeCandBtn.setForeground(Color.WHITE);
        removeCandBtn.addActionListener(this);
        removeRow.add(removeCandidateChoice, BorderLayout.CENTER);
        removeRow.add(removeCandBtn, BorderLayout.EAST);
        form.add(removeRow);

        wrapper.add(form, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Center panel — the green log/console area.
     */
    private Panel buildCenterPanel() {
        Panel center = new Panel(new BorderLayout());

        logArea = new TextArea("", 10, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setEditable(false);
        logArea.setBackground(new Color(25, 25, 25));
        logArea.setForeground(new Color(100, 220, 130));
        center.add(logArea, BorderLayout.CENTER);

        return center;
    }

    /**
     * Bottom panel — action buttons.
     */
    private Panel buildButtonPanel() {
        Panel bottom = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        bottom.setBackground(new Color(30, 39, 46));

        voteBtn   = makeButton("  Vote  ",         new Color(41,  128, 185));
        resultBtn = makeButton("  Results  ",      new Color(142, 68,  173));
        saveBtn   = makeButton("  Save to File  ", new Color(39,  174, 96));
        resetBtn  = makeButton("  New Session (Reset)  ", new Color(127, 140, 141));

        bottom.add(voteBtn);
        bottom.add(resultBtn);
        bottom.add(saveBtn);
        bottom.add(resetBtn);

        return bottom;
    }

    // ─────────────────────────────────────────────
    // EVENT HANDLING
    // ─────────────────────────────────────────────

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == voteBtn)       castVote();
        else if (src == resultBtn)     showResults();
        else if (src == saveBtn)       saveToFile();
        else if (src == resetBtn)      resetSession();
        else if (src == addCandBtn)    addCandidate();
        else if (src == removeCandBtn) removeCandidate();
    }

    // ─────────────────────────────────────────────
    // FEATURE METHODS
    // ─────────────────────────────────────────────

    /**
     * Add a new candidate to both the booth and the dropdowns.
     */
    private void addCandidate() {
        String name = newCandidateField.getText().trim();

        if (name.isEmpty()) {
            log("Please type a candidate name first.");
            return;
        }

        if (booth.hasCandidate(name)) {
            log("'" + name + "' is already in the list.");
            return;
        }

        // Add to data
        booth.addCandidate(name);

        // Add to both dropdowns
        candidateChoice.add(name);
        removeCandidateChoice.add(name);

        newCandidateField.setText("");
        log("Candidate added: " + name);
    }

    /**
     * Remove the selected candidate from booth and both dropdowns.
     */
    private void removeCandidate() {
        if (removeCandidateChoice.getItemCount() == 0) {
            log("No candidates to remove.");
            return;
        }

        String name = removeCandidateChoice.getSelectedItem();

        // Remove from data
        booth.removeCandidate(name);

        // Remove from both dropdowns by rebuilding them
        syncDropdownsFromBooth();

        log("Candidate removed: " + name);
    }

    /**
     * Cast a vote — uses MULTITHREADING and INHERITANCE.
     *
     * MULTITHREADING: The vote runs in a separate VoterThread.
     * INHERITANCE: RegularVoter extends Voter and overrides castVote().
     */
    private void castVote() {
        String name = voterNameField.getText().trim();

        if (name.isEmpty()) {
            log("Please enter your name.");
            return;
        }

        if (!booth.hasCandidates()) {
            log("No candidates available. Please add candidates first.");
            return;
        }

        String candidate = candidateChoice.getSelectedItem();
        if (candidate == null) {
            log("Please select a candidate.");
            return;
        }

        String voterId = "V" + voterIdCounter++;

        // INHERITANCE: RegularVoter extends Voter — creates the voter object
        Voter voter = new RegularVoter(name, voterId);

        // MULTITHREADING: run the vote inside a new thread
        VoterThread thread = new VoterThread(voter, candidate, booth);
        thread.start();

        try {
            thread.join(); // Wait for the thread to finish before reading result
        } catch (InterruptedException ex) {
            log("Vote thread was interrupted.");
        }

        log(thread.getResult());
        voterNameField.setText("");
    }

    /**
     * Show the current session's results and the winner in the log area.
     */
    private void showResults() {
        log("");
        log(booth.getResults());   // Prints each candidate's vote count
        log(booth.getWinner());    // Prints the current winner
        log("");
    }

    /**
     * FILE HANDLING: Append current session results to votes.txt.
     *
     * The file is NEVER overwritten — each session's results are
     * added to the bottom of the file.
     * The full path is printed so the user knows exactly where it is.
     */
    private void saveToFile() {
        if (!booth.hasCandidates()) {
            log("Nothing to save — no candidates in this session.");
            return;
        }

        try {
            String savedPath = booth.appendSessionToFile();
            log("Session " + booth.getSessionNumber() + " saved to:");
            log("  " + savedPath);
        } catch (IOException ex) {
            log("Error saving file: " + ex.getMessage());
        }
    }

    /**
     * Reset: Saves the current session first, then clears everything.
     * All candidates and votes are removed for a fresh new session.
     */
    private void resetSession() {
        // Auto-save current session before resetting (if there's anything to save)
        if (booth.hasCandidates() && booth.getTotalVotes() > 0) {
            try {
                booth.appendSessionToFile(); // auto-save before reset
                log("Auto-saved Session " + booth.getSessionNumber() + " to file.");
            } catch (IOException ex) {
                log("Warning: Could not auto-save session.");
            }
        }

        // Reset booth (clears all candidates, bumps session number)
        booth.reset();

        // Clear both dropdowns completely
        candidateChoice.removeAll();
        removeCandidateChoice.removeAll();

        // Reset voter ID counter
        voterIdCounter = 1;

        // Clear log and show new session header
        logArea.setText("");
        log("======================================");
        log("   New Session " + booth.getSessionNumber() + " started!");
        log("   Previous session was auto-saved.");
        log("======================================");
        log("Add new candidates to begin voting.");
        log("");
    }

    // ─────────────────────────────────────────────
    // HELPER METHODS
    // ─────────────────────────────────────────────

    /**
     * Rebuilds both vote-for and remove dropdowns from the booth's
     * current candidate list. Called after removing a candidate.
     */
    private void syncDropdownsFromBooth() {
        candidateChoice.removeAll();
        removeCandidateChoice.removeAll();

        for (String c : booth.getCandidates()) {
            candidateChoice.add(c);
            removeCandidateChoice.add(c);
        }
    }

    /** Append a line to the log area. */
    private void log(String message) {
        logArea.append(message + "\n");
    }

    /** Creates a styled button and registers this class as its listener. */
    private Button makeButton(String label, Color bg) {
        Button btn = new Button(label);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.addActionListener(this);
        return btn;
    }

    /** Styles a label for the dark header panel. */
    private void styleLabel(Label label) {
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(Color.WHITE);
    }
}
