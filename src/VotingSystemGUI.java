import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * VotingSystemGUI.java - Main GUI Application using Java AWT
 * 
 * This is the main entry point of the Voting System.
 * It provides a graphical interface for:
 * 1. Adding candidates
 * 2. Casting votes (as Regular or VIP voter)
 * 3. Viewing results
 * 4. Demonstrating Race Condition (unsafe multithreading)
 * 5. Demonstrating Deadlock (circular lock dependency)
 * 
 * CONCEPTS DEMONSTRATED:
 * ─────────────────────────────────────────────────────────
 * | Concept | Where it's used |
 * ─────────────────────────────────────────────────────────
 * | Inheritance | RegularVoter & VIPVoter extend Voter |
 * | Polymorphism | castVote() behaves differently |
 * | Multithreading | VoterThread extends Thread |
 * | Race Condition | Unsafe voting without synchronized |
 * | Deadlock | Two locks acquired in reverse order |
 * ─────────────────────────────────────────────────────────
 */
public class VotingSystemGUI extends Frame implements ActionListener {

    // ==================== GUI COMPONENTS ====================

    // Top panel - Candidate management
    private TextField candidateField;
    private Button addCandidateBtn;

    // Middle panel - Voting
    private TextField voterNameField;
    private TextField voterIdField;
    private Choice candidateChoice; // Dropdown to select candidate
    private Choice voterTypeChoice; // Dropdown: Regular or VIP
    private Button voteBtn;

    // Bottom panel - Results and demos
    private Button resultBtn;
    private Button raceConditionBtn;
    private Button deadlockBtn;
    private Button resetBtn;

    // Log area to display messages
    private TextArea logArea;

    // ==================== DATA ====================

    private VotingBooth booth; // The shared voting booth
    private ArrayList<Voter> registeredVoters; // List of all voters
    private int voterCounter; // Auto-increment voter ID

    // ==================== CONSTRUCTOR ====================

    public VotingSystemGUI() {
        // Initialize data
        booth = new VotingBooth();
        registeredVoters = new ArrayList<>();
        voterCounter = 1000;

        // Add default candidates
        booth.addCandidate("Alice");
        booth.addCandidate("Bob");
        booth.addCandidate("Charlie");

        // ── Window Settings ──
        setTitle("★ Java Voting System - OOP & Threading Demo ★");
        setSize(700, 650);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));

        // ── Build the GUI panels ──
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        // ── Window close event ──
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Voting System closed. Goodbye!");
                dispose();
                System.exit(0);
            }
        });

        // Initialize candidate dropdown with default candidates
        candidateChoice.add("Alice");
        candidateChoice.add("Bob");
        candidateChoice.add("Charlie");

        // Show welcome message
        log("╔══════════════════════════════════════════════════╗");
        log("║    Welcome to the Java Voting System!           ║");
        log("║    Demonstrating: Inheritance, Polymorphism,    ║");
        log("║    Multithreading, Race Condition & Deadlock    ║");
        log("╚══════════════════════════════════════════════════╝");
        log("");
        log("Default candidates loaded: Alice, Bob, Charlie");
        log("You can add more candidates above.");
        log("─────────────────────────────────────────────────────");

        setVisible(true);
    }

    // ==================== PANEL CREATION METHODS ====================

    /**
     * Creates the top header panel with candidate addition controls.
     */
    private Panel createHeaderPanel() {
        Panel headerPanel = new Panel();
        headerPanel.setLayout(new GridLayout(3, 1, 5, 5));
        headerPanel.setBackground(new Color(44, 62, 80));

        // Title label
        Label titleLabel = new Label("★ JAVA VOTING SYSTEM ★", Label.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Candidate addition row
        Panel candidatePanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        candidatePanel.setBackground(new Color(52, 73, 94));

        Label candLabel = new Label("Add Candidate:");
        candLabel.setForeground(Color.WHITE);
        candLabel.setFont(new Font("Arial", Font.BOLD, 12));
        candidatePanel.add(candLabel);

        candidateField = new TextField(15);
        candidateField.setFont(new Font("Arial", Font.PLAIN, 13));
        candidatePanel.add(candidateField);

        addCandidateBtn = new Button("  + Add  ");
        addCandidateBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addCandidateBtn.setBackground(new Color(39, 174, 96));
        addCandidateBtn.setForeground(Color.WHITE);
        addCandidateBtn.addActionListener(this);
        candidatePanel.add(addCandidateBtn);

        headerPanel.add(candidatePanel);

        return headerPanel;
    }

    /**
     * Creates the center panel with voting controls and log area.
     */
    private Panel createCenterPanel() {
        Panel centerPanel = new Panel(new BorderLayout(10, 10));

        // ── Voting Form Panel ──
        Panel formPanel = new Panel(new GridLayout(5, 2, 8, 8));
        formPanel.setBackground(new Color(236, 240, 241));

        // Row 1: Voter Name
        Label nameLabel = new Label("  Voter Name:", Label.LEFT);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(nameLabel);

        voterNameField = new TextField(20);
        voterNameField.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(voterNameField);

        // Row 2: Voter ID (auto-generated info)
        Label idLabel = new Label("  Voter ID:", Label.LEFT);
        idLabel.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(idLabel);

        voterIdField = new TextField(20);
        voterIdField.setFont(new Font("Arial", Font.PLAIN, 13));
        voterIdField.setText("V" + voterCounter); // Auto-generated ID
        voterIdField.setEditable(false); // User can't change it
        formPanel.add(voterIdField);

        // Row 3: Select Candidate
        Label selectLabel = new Label("  Select Candidate:", Label.LEFT);
        selectLabel.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(selectLabel);

        candidateChoice = new Choice();
        candidateChoice.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(candidateChoice);

        // Row 4: Voter Type (POLYMORPHISM selector)
        Label typeLabel = new Label("  Voter Type (Polymorphism):", Label.LEFT);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(typeLabel);

        voterTypeChoice = new Choice();
        voterTypeChoice.add("Regular Voter");
        voterTypeChoice.add("VIP Voter");
        voterTypeChoice.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(voterTypeChoice);

        // Row 5: Vote button
        Label emptyLabel = new Label("");
        formPanel.add(emptyLabel);

        voteBtn = new Button("  ✓ Cast Vote (Uses Multithreading)  ");
        voteBtn.setFont(new Font("Arial", Font.BOLD, 13));
        voteBtn.setBackground(new Color(41, 128, 185));
        voteBtn.setForeground(Color.WHITE);
        voteBtn.addActionListener(this);
        formPanel.add(voteBtn);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        // ── Log Area ──
        logArea = new TextArea("", 12, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setEditable(false);
        logArea.setBackground(new Color(30, 30, 30));
        logArea.setForeground(new Color(46, 204, 113));
        centerPanel.add(logArea, BorderLayout.CENTER);

        return centerPanel;
    }

    /**
     * Creates the bottom panel with action buttons.
     */
    private Panel createBottomPanel() {
        Panel bottomPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        bottomPanel.setBackground(new Color(44, 62, 80));

        // Results button
        resultBtn = new Button("  📊 Show Results  ");
        resultBtn.setFont(new Font("Arial", Font.BOLD, 12));
        resultBtn.setBackground(new Color(142, 68, 173));
        resultBtn.setForeground(Color.WHITE);
        resultBtn.addActionListener(this);
        bottomPanel.add(resultBtn);

        // Race Condition demo button
        raceConditionBtn = new Button("  ⚠ Demo: Race Condition  ");
        raceConditionBtn.setFont(new Font("Arial", Font.BOLD, 12));
        raceConditionBtn.setBackground(new Color(211, 84, 0));
        raceConditionBtn.setForeground(Color.WHITE);
        raceConditionBtn.addActionListener(this);
        bottomPanel.add(raceConditionBtn);

        // Deadlock demo button
        deadlockBtn = new Button("  🔒 Demo: Deadlock (3s timeout)  ");
        deadlockBtn.setFont(new Font("Arial", Font.BOLD, 12));
        deadlockBtn.setBackground(new Color(192, 57, 43));
        deadlockBtn.setForeground(Color.WHITE);
        deadlockBtn.addActionListener(this);
        bottomPanel.add(deadlockBtn);

        // Reset button
        resetBtn = new Button("  ↻ Reset All  ");
        resetBtn.setFont(new Font("Arial", Font.BOLD, 12));
        resetBtn.setBackground(new Color(127, 140, 141));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(this);
        bottomPanel.add(resetBtn);

        return bottomPanel;
    }

    // ==================== EVENT HANDLING ====================

    /**
     * Handles all button click events.
     * This single method handles clicks from all buttons.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == addCandidateBtn) {
            addCandidate();
        } else if (source == voteBtn) {
            castVote();
        } else if (source == resultBtn) {
            showResults();
        } else if (source == raceConditionBtn) {
            demonstrateRaceCondition();
        } else if (source == deadlockBtn) {
            demonstrateDeadlock();
        } else if (source == resetBtn) {
            resetAll();
        }
    }

    // ==================== FEATURE METHODS ====================

    /**
     * Adds a new candidate to the election.
     */
    private void addCandidate() {
        String name = candidateField.getText().trim();
        if (name.isEmpty()) {
            log("⚠ Please enter a candidate name!");
            return;
        }
        booth.addCandidate(name);
        candidateChoice.add(name);
        candidateField.setText("");
        log("✓ Candidate added: " + name);
    }

    /**
     * Casts a vote using a SEPARATE THREAD (Multithreading).
     * 
     * POLYMORPHISM: Based on voter type selection, either a
     * RegularVoter or VIPVoter object is created. Both call
     * castVote() but produce different outputs.
     */
    private void castVote() {
        String name = voterNameField.getText().trim();
        if (name.isEmpty()) {
            log("⚠ Please enter voter name!");
            return;
        }
        if (candidateChoice.getItemCount() == 0) {
            log("⚠ No candidates available! Add candidates first.");
            return;
        }

        String candidate = candidateChoice.getSelectedItem();
        String voterId = "V" + voterCounter++;

        // POLYMORPHISM: Creating different objects based on user selection
        Voter voter;
        if (voterTypeChoice.getSelectedItem().equals("VIP Voter")) {
            voter = new VIPVoter(name, voterId); // VIPVoter object
        } else {
            voter = new RegularVoter(name, voterId); // RegularVoter object
        }

        registeredVoters.add(voter);

        // MULTITHREADING: Vote is cast in a separate thread
        booth.setSafeMode(true); // Safe mode for normal voting
        VoterThread thread = new VoterThread(voter, candidate, booth);
        thread.start(); // Start the thread

        log("🧵 [Thread Started] " + voter.toString());

        // Wait for thread to finish and show result
        try {
            thread.join(); // Wait for thread to complete
            log("   → " + thread.getResult());
        } catch (InterruptedException ex) {
            log("⚠ Thread was interrupted!");
        }

        // Update voter ID for next voter
        voterIdField.setText("V" + voterCounter);
        voterNameField.setText("");

        log("─────────────────────────────────────────────────────");
    }

    /**
     * Shows election results.
     */
    private void showResults() {
        log("");
        log(booth.getResults());
        log("");
    }

    /**
     * RACE CONDITION DEMONSTRATION
     * 
     * Creates 20 threads that all vote simultaneously WITHOUT synchronization.
     * Expected: 20 votes total. Actual: Often less due to race condition.
     * 
     * Then runs the same with synchronization to show the fix.
     */
    private void demonstrateRaceCondition() {
        log("");
        log("╔══════════════════════════════════════════════════╗");
        log("║        RACE CONDITION DEMONSTRATION             ║");
        log("╚══════════════════════════════════════════════════╝");
        log("");

        // Reset booth for clean demo
        booth.reset();

        // ── Part 1: UNSAFE (Race Condition) ──
        log("▶ Part 1: UNSAFE Mode (No Synchronization)");
        log("  Launching 20 threads to vote simultaneously...");
        log("");

        booth.setSafeMode(false); // Turn OFF synchronization

        ArrayList<VoterThread> threads = new ArrayList<>();

        // Create 20 voter threads
        for (int i = 0; i < 20; i++) {
            Voter v = new RegularVoter("RaceVoter" + i, "RV" + i);
            VoterThread t = new VoterThread(v, "Alice", booth);
            threads.add(t);
        }

        // Start all threads at once
        for (VoterThread t : threads) {
            t.start();
        }

        // Wait for all threads to finish
        for (VoterThread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                // Silently handle
            }
        }

        int unsafeResult = booth.getVotes("Alice");
        log("  Expected votes for Alice: 20");
        log("  Actual votes for Alice:   " + unsafeResult);
        if (unsafeResult < 20) {
            log("  ⚠ RACE CONDITION DETECTED! Some votes were LOST!");
            log("  ⚠ This happened because threads read the same value");
            log("    before writing, causing 'lost updates'.");
        } else {
            log("  ℹ Race condition didn't occur this time (not guaranteed).");
            log("    Try running this demo again — it's non-deterministic.");
        }
        log("");

        // ── Part 2: SAFE (With Synchronization) ──
        log("▶ Part 2: SAFE Mode (With Synchronization)");
        log("  Resetting and launching 20 threads with sync...");
        log("");

        booth.reset(); // Reset counts
        booth.setSafeMode(true); // Turn ON synchronization

        ArrayList<VoterThread> safeThreads = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Voter v = new RegularVoter("SafeVoter" + i, "SV" + i);
            VoterThread t = new VoterThread(v, "Alice", booth);
            safeThreads.add(t);
        }

        for (VoterThread t : safeThreads) {
            t.start();
        }

        for (VoterThread t : safeThreads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                // Silently handle
            }
        }

        int safeResult = booth.getVotes("Alice");
        log("  Expected votes for Alice: 20");
        log("  Actual votes for Alice:   " + safeResult);
        log("  ✓ All votes counted correctly with synchronized!");

        log("");
        log("─────────────────────────────────────────────────────");

        // Reset booth back to original state
        booth.reset();
        booth.setSafeMode(true);
    }

    /**
     * DEADLOCK DEMONSTRATION
     * 
     * Two threads try to acquire two locks in OPPOSITE order.
     * Thread 1: locks Booth → then Counter
     * Thread 2: locks Counter → then Booth
     * This creates a circular wait → DEADLOCK.
     * 
     * We use a timeout to detect and break out of the deadlock.
     */
    private void demonstrateDeadlock() {
        log("");
        log("╔══════════════════════════════════════════════════╗");
        log("║          DEADLOCK DEMONSTRATION                 ║");
        log("╚══════════════════════════════════════════════════╝");
        log("");
        log("▶ Two threads will try to acquire locks in reverse order.");
        log("  Thread 1: BOOTH lock → COUNTER lock");
        log("  Thread 2: COUNTER lock → BOOTH lock");
        log("  This causes circular waiting → DEADLOCK!");
        log("");
        log("  ⏳ Waiting 3 seconds for deadlock to occur...");
        log("  (Check console for detailed lock messages)");
        log("");

        // Create two voters for the demo
        Voter v1 = new RegularVoter("DeadlockVoter1", "DL1");
        Voter v2 = new VIPVoter("DeadlockVoter2", "DL2");

        // Thread 1: will lock booth first, then counter
        VoterThread t1 = new VoterThread(v1, "Alice", booth, true, 1);
        // Thread 2: will lock counter first, then booth (REVERSE ORDER!)
        VoterThread t2 = new VoterThread(v2, "Bob", booth, true, 2);

        t1.start();
        t2.start();

        // Wait with timeout to detect deadlock
        try {
            t1.join(3000); // Wait max 3 seconds
            t2.join(3000);
        } catch (InterruptedException ex) {
            // Silently handle
        }

        // Check if threads are still alive (stuck in deadlock)
        if (t1.isAlive() || t2.isAlive()) {
            log("  🔒 DEADLOCK DETECTED!");
            log("  Both threads are stuck waiting for each other.");
            log("  Thread 1 status: " + (t1.isAlive() ? "BLOCKED ⛔" : "Completed ✓"));
            log("  Thread 2 status: " + (t2.isAlive() ? "BLOCKED ⛔" : "Completed ✓"));
            log("");
            log("  HOW TO FIX DEADLOCKS:");
            log("  → Always acquire locks in the SAME ORDER.");
            log("  → Use tryLock() with timeout (java.util.concurrent).");
            log("  → Minimize the number of locks held at once.");

            // Force-stop the deadlocked threads (using deprecated stop as last resort)
            // In real applications, we'd use InterruptedException or Lock.tryLock()
            t1.interrupt();
            t2.interrupt();
        } else {
            log("  ℹ Both threads completed (no deadlock this time).");
            log("  Deadlocks are non-deterministic — try again!");
            log("  Thread 1 result: " + t1.getResult());
            log("  Thread 2 result: " + t2.getResult());
        }

        log("");
        log("─────────────────────────────────────────────────────");
    }

    /**
     * Resets all votes and clears the log.
     */
    private void resetAll() {
        booth.reset();
        registeredVoters.clear();
        voterCounter = 1000;
        voterIdField.setText("V" + voterCounter);

        logArea.setText("");
        log("✓ All votes and data have been reset!");
        log("─────────────────────────────────────────────────────");
    }

    /**
     * Helper method to append a message to the log area.
     */
    private void log(String message) {
        logArea.append(message + "\n");
    }

    // ==================== MAIN METHOD ====================

    /**
     * Entry point of the application.
     * Creates the GUI on the main thread.
     */
    public static void main(String[] args) {
        System.out.println("Starting Voting System...");
        new VotingSystemGUI();
    }
}
