# ☕ Java Voting System — Mini Project

> A desktop voting system built with **Java AWT** that demonstrates core Object-Oriented Programming and Multithreading concepts through an interactive GUI.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![AWT](https://img.shields.io/badge/GUI-Java_AWT-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

---

## 📋 Table of Contents

- [About the Project](#-about-the-project)
- [OOP Concepts Demonstrated](#-oop-concepts-demonstrated)
- [Project Structure](#-project-structure)
- [Class Diagram](#-class-diagram)
- [How to Run](#-how-to-run)
- [Features](#-features)
- [Screenshots](#-screenshots)
- [How It Works](#-how-it-works)
- [License](#-license)

---

## 📖 About the Project

This is a **Java Mini Project** designed for academic purposes. It implements a fully functional **Voting System** with a graphical user interface using Java AWT. The project is structured to clearly demonstrate the following core Java concepts:

| # | Concept           | Implementation                                      |
|---|-------------------|-----------------------------------------------------|
| 1 | **Inheritance**   | `RegularVoter` & `VIPVoter` extend the `Voter` class |
| 2 | **Polymorphism**  | `castVote()` method behaves differently per subclass |
| 3 | **Multithreading**| Each vote runs in a separate `VoterThread`           |
| 4 | **Race Condition**| Unsafe concurrent voting without `synchronized`      |
| 5 | **Deadlock**      | Two locks acquired in reverse order cause a hang     |

---

## 🧬 OOP Concepts Demonstrated

### 1. Inheritance
```
Voter (Base Class)
 ├── RegularVoter   → extends Voter
 └── VIPVoter       → extends Voter
```
Both child classes inherit fields (`name`, `voterId`, `hasVoted`) and methods from `Voter`, and call `super()` in their constructors.

### 2. Polymorphism
The `castVote(String candidate)` method exists in all three classes but produces **different output**:
- **Voter**: `"John (General Voter) voted for Alice"`
- **RegularVoter**: `"[Regular Vote] John voted for Alice"`
- **VIPVoter**: `"[VIP Priority Vote] John voted for Alice ★"`

The GUI uses a `Voter` reference to call `castVote()`, and the actual behavior depends on the runtime object type.

### 3. Multithreading
`VoterThread` extends `Thread`. Each vote is cast inside a separate thread via `thread.start()`, and the main GUI waits for completion using `thread.join()`.

### 4. Race Condition
The **"Demo: Race Condition"** button launches 20 threads simultaneously:
- **Unsafe mode** (no `synchronized`): Threads read stale values → some votes are lost.
- **Safe mode** (with `synchronized`): All 20 votes are counted correctly.

### 5. Deadlock
The **"Demo: Deadlock"** button creates two threads that lock two shared objects in **reverse order**:
- Thread 1: `lockBooth` → `lockCounter`
- Thread 2: `lockCounter` → `lockBooth`

This causes circular waiting. A 3-second timeout detects the deadlock.

---

## 📁 Project Structure

```
Java-Mini-Project/
├── src/
│   ├── Voter.java              # Base class (Inheritance)
│   ├── RegularVoter.java       # Subclass — standard vote (Polymorphism)
│   ├── VIPVoter.java           # Subclass — priority vote (Polymorphism)
│   ├── VoterThread.java        # Thread per voter (Multithreading)
│   ├── VotingBooth.java        # Shared resource (Race Condition & Deadlock)
│   ├── VotingSystemGUI.java    # Main GUI entry point (Java AWT)
│   ├── InputValidator.java     # Input validation utilities
│   ├── VoteRecord.java         # Vote transaction model
│   ├── AuditTrail.java         # Event and vote auditing module
│   ├── ElectionAnalytics.java  # Reporting and analytics module
│   └── FileReportExporter.java # CSV export module
├── .gitignore
├── LICENSE
└── README.md
```

---

## 🏗 Class Diagram

```
┌──────────────────────┐
│       Voter          │  ← Base class
│──────────────────────│
│ # name: String       │
│ # voterId: String    │
│ # hasVoted: boolean  │
│──────────────────────│
│ + castVote(String)   │  ← Overridden in subclasses
│ + getName()          │
│ + getVoterId()       │
│ + hasVoted()         │
│ + resetVote()        │
│ + toString()         │
└──────────┬───────────┘
           │ extends
     ┌─────┴──────┐
     │            │
┌────▼─────┐ ┌───▼──────┐
│ Regular  │ │   VIP    │
│  Voter   │ │  Voter   │
│──────────│ │──────────│
│+castVote │ │+castVote │  ← Different behavior (Polymorphism)
│+toString │ │+toString │
└──────────┘ └──────────┘

┌──────────────────────┐     ┌──────────────────────┐
│    VoterThread        │────▶│     VotingBooth      │
│   (extends Thread)    │     │  (shared resource)   │
│───────────────────────│     │──────────────────────│
│ - voter: Voter        │     │ - voteCount: HashMap │
│ - candidate: String   │     │ - lockBooth: Object  │
│ - booth: VotingBooth  │     │ - lockCounter: Object│
│───────────────────────│     │──────────────────────│
│ + run()               │     │ + vote()             │
│ + getResult()         │     │ + voteSafe()         │
└───────────────────────┘     │ + voteUnsafe()       │
                              │ + deadlockVote_T1()  │
┌──────────────────────┐      │ + deadlockVote_T2()  │
│  VotingSystemGUI     │──────│ + getResults()       │
│  (extends Frame)     │      └──────────────────────┘
│  Main entry point    │
└──────────────────────┘
```

---

## 🚀 How to Run

### Prerequisites
- **Java JDK 8** or higher installed
- A terminal or command prompt

> Note: If multiple Java versions are installed, use matching versions for `java` and `javac`.

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/ankushkhakale/Java-Mini-Project.git
cd Java-Mini-Project/src

# 2. Compile all Java files
javac *.java

# 3. Run the application
java VotingSystemGUI
```

The GUI window will launch — you can start voting immediately!

---

## ✨ Features

| Feature | Description |
|---|---|
| 🗳 **Add Candidates** | Dynamically add election candidates |
| 🧑 **Cast Votes** | Vote as Regular or VIP voter type |
| 📊 **View Results** | See real-time election tallies |
| 📈 **Election Analytics** | Turnout, vote share, and leading candidate report |
| 🧾 **Audit Trail** | Timestamped log of vote operations and key events |
| 💾 **Export Reports** | Export election results and audit records as CSV |
| ✅ **Input Validation** | Candidate and voter names are validated before processing |
| ⚠️ **Race Condition Demo** | 20 threads vote unsafely vs. safely |
| 🔒 **Deadlock Demo** | Two threads deadlock with reverse lock order |
| ↻ **Reset** | Clear all votes and start fresh |

---

## 🖥 Screenshots

> _Run the application to see the Java AWT GUI in action._

The application features:
- A **dark header** with candidate management
- A **voting form** with auto-generated voter IDs
- A **terminal-style log area** (dark background, green text)
- **Action buttons** for results, race condition demo, and deadlock demo

---

## ⚙ How It Works

### Normal Voting Flow
```
User enters name → Selects candidate & voter type
    → VoterThread.start()  [New thread created]
        → VotingBooth.vote()  [synchronized]
            → voter.castVote()  [Polymorphism — Regular or VIP]
    → thread.join()  [Wait for completion]
    → Display result in log
```

### Race Condition Flow
```
20 VoterThreads created → All start simultaneously
    → VotingBooth.voteUnsafe()  [NO synchronization]
        → Thread.sleep(10ms)  [Artificial delay]
        → Multiple threads read same stale value
        → Lost updates → vote count < 20

Then repeated with synchronized:
    → VotingBooth.voteSafe()  [WITH synchronization]
        → Mutual exclusion → All 20 votes counted
```

### Deadlock Flow
```
Thread 1: lock(booth)   → sleep → lock(counter)  [BLOCKED waiting for counter]
Thread 2: lock(counter) → sleep → lock(booth)    [BLOCKED waiting for booth]
    → Circular wait → DEADLOCK detected after 3s timeout
```

---

## 👤 Author

**Ankush Khakale**
- GitHub: [@ankushkhakale](https://github.com/ankushkhakale)

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
