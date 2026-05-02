import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * VotingSystem.java - Main Applet
 *
 * CONCEPTS USED:
 * 1. INHERITANCE    - RegularVoter extends Voter
 * 2. POLYMORPHISM   - castVote() is overridden in RegularVoter
 * 3. MULTITHREADING - VoterThread extends Thread; each vote runs in its own thread
 * 4. FILE HANDLING  - Results are appended to votes.txt session-wise
 *
 * HOW TO RUN:
 *   javac *.java
 *   appletviewer VotingSystem.html
 *
 * LAYOUT:
 *   The applet uses CardLayout to switch between four tabs:
 *   [Vote]  [Candidates]  [Results]  [Save & Reset]
 */
public class VotingSystem extends Applet implements ActionListener {

    // ── Tab buttons (act like tab headers) ──
    private Button tabVoteBtn;
    private Button tabCandBtn;
    private Button tabResultBtn;
    private Button tabSaveBtn;

    // ── CardLayout switches which tab is visible ──
    private CardLayout cardLayout;
    private Panel cardPanel;

    // ── Tab 1: Vote ──
    private TextField voterNameField;
    private Choice candidateChoice;
    private Button castVoteBtn;
    private Label voteStatusLabel;

    // ── Tab 2: Candidates ──
    private TextField newCandidateField;
    private Button addCandBtn;
    private java.awt.List candidateList;   // scrollable list showing current candidates
    private Button removeCandBtn;

    // ── Tab 3: Results ──
    private TextArea resultsArea;
    private Label winnerLabel;
    private Button refreshResultsBtn;

    // ── Tab 4: Save & Reset ──
    private Button saveBtn;
    private Button resetBtn;
    private Label saveStatusLabel;

    // ── Data ──
    private VotingBooth booth;
    private int voterIdCounter;

    // Card names used by CardLayout
    private static final String TAB_VOTE       = "Vote";
    private static final String TAB_CANDIDATES = "Candidates";
    private static final String TAB_RESULTS    = "Results";
    private static final String TAB_SAVE       = "Save & Reset";

    // ─────────────────────────────────────────────────────
    // init() - Called once when the applet starts
    // ─────────────────────────────────────────────────────
    @Override
    public void init() {
        booth = new VotingBooth();
        voterIdCounter = 1;

        setSize(600, 400);
        setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout(0, 0));

        // Title bar at the top
        add(buildTitlePanel(), BorderLayout.NORTH);

        // Tab buttons just below the title
        add(buildTabBar(), BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────
    // Title Panel
    // ─────────────────────────────────────────────────────
    private Panel buildTitlePanel() {
        Panel p = new Panel(new BorderLayout());
        p.setBackground(new Color(51, 51, 51));

        Label title = new Label("  Voting System", Label.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        p.add(title, BorderLayout.WEST);

        Label session = new Label("Session " + booth.getSessionNumber() + "  ", Label.RIGHT);
        session.setFont(new Font("Arial", Font.PLAIN, 12));
        session.setForeground(new Color(180, 180, 180));
        p.add(session, BorderLayout.EAST);

        return p;
    }

    // ─────────────────────────────────────────────────────
    // Tab bar + CardLayout content
    // ─────────────────────────────────────────────────────
    private Panel buildTabBar() {
        Panel wrapper = new Panel(new BorderLayout());

        // Row of tab buttons
        Panel tabRow = new Panel(new GridLayout(1, 4, 2, 0));
        tabRow.setBackground(new Color(200, 200, 200));

        tabVoteBtn   = makeTabButton(TAB_VOTE);
        tabCandBtn   = makeTabButton(TAB_CANDIDATES);
        tabResultBtn = makeTabButton(TAB_RESULTS);
        tabSaveBtn   = makeTabButton(TAB_SAVE);

        tabRow.add(tabVoteBtn);
        tabRow.add(tabCandBtn);
        tabRow.add(tabResultBtn);
        tabRow.add(tabSaveBtn);

        wrapper.add(tabRow, BorderLayout.NORTH);

        // CardLayout holds all tab content panels
        cardLayout = new CardLayout();
        cardPanel  = new Panel(cardLayout);
        cardPanel.setBackground(new Color(245, 245, 245));

        cardPanel.add(buildVoteTab(),       TAB_VOTE);
        cardPanel.add(buildCandidatesTab(), TAB_CANDIDATES);
        cardPanel.add(buildResultsTab(),    TAB_RESULTS);
        cardPanel.add(buildSaveTab(),       TAB_SAVE);

        wrapper.add(cardPanel, BorderLayout.CENTER);

        // Show Vote tab first and highlight its button
        cardLayout.show(cardPanel, TAB_VOTE);
        highlightTab(tabVoteBtn);

        return wrapper;
    }

    // ─────────────────────────────────────────────────────
    // TAB 1 — Vote
    // ─────────────────────────────────────────────────────
    private Panel buildVoteTab() {
        Panel p = new Panel(null); // absolute positioning for simplicity
        p.setBackground(new Color(245, 245, 245));

        int lx = 30, fx = 160, fw = 200, lh = 24, fh = 26, gap = 40;
        int y = 40;

        // Voter Name
        Label l1 = new Label("Voter Name:");
        l1.setBounds(lx, y, 120, lh);
        p.add(l1);

        voterNameField = new TextField();
        voterNameField.setBounds(fx, y, fw, fh);
        p.add(voterNameField);

        y += gap;

        // Select Candidate
        Label l2 = new Label("Vote For:");
        l2.setBounds(lx, y, 120, lh);
        p.add(l2);

        candidateChoice = new Choice();
        candidateChoice.setBounds(fx, y, fw, fh);
        p.add(candidateChoice);

        y += gap + 10;

        // Cast Vote button
        castVoteBtn = new Button("Cast Vote");
        castVoteBtn.setBounds(fx, y, 100, 30);
        castVoteBtn.setBackground(new Color(70, 130, 180));
        castVoteBtn.setForeground(Color.WHITE);
        castVoteBtn.setFont(new Font("Arial", Font.BOLD, 13));
        castVoteBtn.addActionListener(this);
        p.add(castVoteBtn);

        y += 50;

        // Status label shows result of last vote action
        voteStatusLabel = new Label("", Label.LEFT);
        voteStatusLabel.setBounds(lx, y, 400, lh);
        voteStatusLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        p.add(voteStatusLabel);

        return p;
    }

    // ─────────────────────────────────────────────────────
    // TAB 2 — Candidates
    // ─────────────────────────────────────────────────────
    private Panel buildCandidatesTab() {
        Panel p = new Panel(null);
        p.setBackground(new Color(245, 245, 245));

        int lx = 30, fx = 160, lh = 24, fh = 26;

        // Add Candidate
        Label l1 = new Label("Candidate Name:");
        l1.setBounds(lx, 40, 130, lh);
        p.add(l1);

        newCandidateField = new TextField();
        newCandidateField.setBounds(fx, 40, 160, fh);
        p.add(newCandidateField);

        addCandBtn = new Button("Add");
        addCandBtn.setBounds(330, 40, 60, fh);
        addCandBtn.setBackground(new Color(60, 160, 80));
        addCandBtn.setForeground(Color.WHITE);
        addCandBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addCandBtn.addActionListener(this);
        p.add(addCandBtn);

        // Current candidates list (scrollable)
        Label l2 = new Label("Current Candidates:");
        l2.setBounds(lx, 85, 150, lh);
        p.add(l2);

        candidateList = new java.awt.List(6, false);  // 6 visible rows, single select
        candidateList.setBounds(lx, 110, 260, 140);
        p.add(candidateList);

        removeCandBtn = new Button("Remove Selected");
        removeCandBtn.setBounds(lx, 260, 140, 28);
        removeCandBtn.setBackground(new Color(190, 60, 50));
        removeCandBtn.setForeground(Color.WHITE);
        removeCandBtn.setFont(new Font("Arial", Font.BOLD, 12));
        removeCandBtn.addActionListener(this);
        p.add(removeCandBtn);

        return p;
    }

    // ─────────────────────────────────────────────────────
    // TAB 3 — Results
    // ─────────────────────────────────────────────────────
    private Panel buildResultsTab() {
        Panel p = new Panel(null);
        p.setBackground(new Color(245, 245, 245));

        // Results text area (light, readable)
        resultsArea = new TextArea("", 8, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
        resultsArea.setBounds(30, 30, 400, 180);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultsArea.setEditable(false);
        resultsArea.setBackground(new Color(255, 255, 255));
        resultsArea.setForeground(new Color(30, 30, 30));
        p.add(resultsArea);

        // Winner label
        winnerLabel = new Label("", Label.LEFT);
        winnerLabel.setBounds(30, 220, 450, 24);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 13));
        winnerLabel.setForeground(new Color(30, 100, 30));
        p.add(winnerLabel);

        // Refresh button
        refreshResultsBtn = new Button("Refresh Results");
        refreshResultsBtn.setBounds(30, 255, 130, 28);
        refreshResultsBtn.setBackground(new Color(100, 80, 180));
        refreshResultsBtn.setForeground(Color.WHITE);
        refreshResultsBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshResultsBtn.addActionListener(this);
        p.add(refreshResultsBtn);

        return p;
    }

    // ─────────────────────────────────────────────────────
    // TAB 4 — Save & Reset
    // ─────────────────────────────────────────────────────
    private Panel buildSaveTab() {
        Panel p = new Panel(null);
        p.setBackground(new Color(245, 245, 245));

        // Save button
        saveBtn = new Button("Save Results to File");
        saveBtn.setBounds(60, 50, 200, 35);
        saveBtn.setBackground(new Color(60, 160, 80));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.addActionListener(this);
        p.add(saveBtn);

        Label saveInfo = new Label("Appends this session's votes to votes.txt");
        saveInfo.setBounds(60, 90, 360, 20);
        saveInfo.setFont(new Font("Arial", Font.PLAIN, 11));
        saveInfo.setForeground(new Color(100, 100, 100));
        p.add(saveInfo);

        // Divider gap
        Label spacer = new Label("─────────────────────────────────");
        spacer.setBounds(60, 118, 320, 18);
        spacer.setForeground(new Color(180, 180, 180));
        p.add(spacer);

        // Reset button
        resetBtn = new Button("Start New Session (Reset)");
        resetBtn.setBounds(60, 145, 220, 35);
        resetBtn.setBackground(new Color(160, 80, 60));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFont(new Font("Arial", Font.BOLD, 13));
        resetBtn.addActionListener(this);
        p.add(resetBtn);

        Label resetInfo = new Label("Auto-saves first, then clears all candidates and votes.");
        resetInfo.setBounds(60, 185, 420, 20);
        resetInfo.setFont(new Font("Arial", Font.PLAIN, 11));
        resetInfo.setForeground(new Color(100, 100, 100));
        p.add(resetInfo);

        // Status label shows feedback after clicking Save or Reset
        saveStatusLabel = new Label("", Label.LEFT);
        saveStatusLabel.setBounds(60, 220, 450, 24);
        saveStatusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        saveStatusLabel.setForeground(new Color(50, 100, 50));
        p.add(saveStatusLabel);

        return p;
    }

    // ─────────────────────────────────────────────────────
    // EVENT HANDLING
    // ─────────────────────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // Tab switching
        if (src == tabVoteBtn) {
            cardLayout.show(cardPanel, TAB_VOTE);
            highlightTab(tabVoteBtn);

        } else if (src == tabCandBtn) {
            cardLayout.show(cardPanel, TAB_CANDIDATES);
            highlightTab(tabCandBtn);

        } else if (src == tabResultBtn) {
            refreshResults(); // auto-refresh when switching to Results tab
            cardLayout.show(cardPanel, TAB_RESULTS);
            highlightTab(tabResultBtn);

        } else if (src == tabSaveBtn) {
            cardLayout.show(cardPanel, TAB_SAVE);
            highlightTab(tabSaveBtn);

        // Feature actions
        } else if (src == castVoteBtn)       castVote();
        else if (src == addCandBtn)          addCandidate();
        else if (src == removeCandBtn)       removeCandidate();
        else if (src == refreshResultsBtn)   refreshResults();
        else if (src == saveBtn)             saveToFile();
        else if (src == resetBtn)            resetSession();
    }

    // ─────────────────────────────────────────────────────
    // FEATURE METHODS
    // ─────────────────────────────────────────────────────

    /**
     * Add a candidate to the booth and update both the
     * candidateList (Tab 2) and the candidateChoice dropdown (Tab 1).
     */
    private void addCandidate() {
        String name = newCandidateField.getText().trim();

        if (name.isEmpty()) {
            return;
        }

        if (booth.hasCandidate(name)) {
            voteStatusLabel.setText("'" + name + "' already exists.");
            return;
        }

        booth.addCandidate(name);
        candidateChoice.add(name);    // update Vote tab dropdown
        candidateList.add(name);      // update Candidates tab list
        newCandidateField.setText("");
    }

    /**
     * Remove the selected candidate from the list, the dropdown, and the booth.
     */
    private void removeCandidate() {
        int selected = candidateList.getSelectedIndex();
        if (selected == -1) {
            return;  // nothing selected
        }

        String name = candidateList.getSelectedItem();

        booth.removeCandidate(name);
        candidateList.remove(selected);

        // Rebuild the vote-for dropdown to stay in sync
        rebuildCandidateChoice();
    }

    /**
     * Cast a vote.
     *
     * MULTITHREADING: runs in a separate VoterThread.
     * INHERITANCE:    RegularVoter extends Voter and overrides castVote().
     */
    private void castVote() {
        String name = voterNameField.getText().trim();

        if (name.isEmpty()) {
            voteStatusLabel.setText("Please enter your name.");
            return;
        }

        if (!booth.hasCandidates()) {
            voteStatusLabel.setText("No candidates yet. Go to the Candidates tab.");
            return;
        }

        String candidate = candidateChoice.getSelectedItem();
        if (candidate == null) {
            voteStatusLabel.setText("Please select a candidate.");
            return;
        }

        String voterId = "V" + voterIdCounter++;

        // INHERITANCE: RegularVoter extends Voter
        Voter voter = new RegularVoter(name, voterId);

        // MULTITHREADING: vote runs in its own thread
        VoterThread thread = new VoterThread(voter, candidate, booth);
        thread.start();

        try {
            thread.join(); // wait for the thread to finish
        } catch (InterruptedException ex) {
            voteStatusLabel.setText("Vote interrupted.");
            return;
        }

        voteStatusLabel.setText("Vote recorded successfully.");
        voterNameField.setText("");
    }

    /**
     * Refresh the Results tab content (vote counts + winner).
     */
    private void refreshResults() {
        resultsArea.setText(booth.getResults());
        winnerLabel.setText(booth.getWinner());
    }

    /**
     * FILE HANDLING: Append this session's results to votes.txt.
     */
    private void saveToFile() {
        if (!booth.hasCandidates()) {
            saveStatusLabel.setText("Nothing to save. Add candidates first.");
            return;
        }

        try {
            // FILE HANDLING: appendSessionToFile() uses FileWriter in append mode.
            // It writes to vote.txt without erasing previous sessions.
            booth.appendSessionToFile();
            saveStatusLabel.setText("Saved to: vote.txt  (in Mini Project folder)");
        } catch (IOException ex) {
            saveStatusLabel.setText("Error: " + ex.getMessage());
        }
    }

    /**
     * Reset: auto-save, then clear everything for a fresh new session.
     */
    private void resetSession() {
        // Auto-save before clearing
        if (booth.hasCandidates() && booth.getTotalVotes() > 0) {
            try {
                booth.appendSessionToFile();
            } catch (IOException ex) {
                saveStatusLabel.setText("Warning: Could not save before reset.");
            }
        }

        // Clear all data
        booth.reset();
        voterIdCounter = 1;

        // Clear all UI lists and dropdowns
        candidateChoice.removeAll();
        candidateList.removeAll();
        resultsArea.setText("");
        winnerLabel.setText("");
        voteStatusLabel.setText("");
        saveStatusLabel.setText("Session reset. New session " + booth.getSessionNumber() + " started.");
    }

    // ─────────────────────────────────────────────────────
    // HELPER METHODS
    // ─────────────────────────────────────────────────────

    /**
     * Rebuild the Vote tab's candidate dropdown from the current booth list.
     * Called after removing a candidate to keep it in sync.
     */
    private void rebuildCandidateChoice() {
        candidateChoice.removeAll();
        for (String c : booth.getCandidates()) {
            candidateChoice.add(c);
        }
    }

    /**
     * Creates a tab button with consistent styling.
     */
    private Button makeTabButton(String label) {
        Button btn = new Button(label);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBackground(new Color(210, 210, 210));
        btn.setForeground(new Color(50, 50, 50));
        btn.addActionListener(this);
        return btn;
    }

    /**
     * Highlights the active tab button and resets all others to normal.
     */
    private void highlightTab(Button active) {
        Button[] allTabs = { tabVoteBtn, tabCandBtn, tabResultBtn, tabSaveBtn };
        for (Button b : allTabs) {
            b.setBackground(new Color(210, 210, 210));
            b.setForeground(new Color(50, 50, 50));
        }
        active.setBackground(new Color(255, 255, 255));
        active.setForeground(new Color(0, 0, 0));
    }
}
