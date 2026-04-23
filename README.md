<![CDATA[# 🗳️ Java Voting System — Applet-Based Mini Project

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![AWT](https://img.shields.io/badge/AWT-Applet-blue?style=for-the-badge)
![OOP](https://img.shields.io/badge/OOP-Concepts-green?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)

**A beginner-friendly Java Applet application that brings four core Java concepts to life through an interactive, GUI-based voting system.**

[📋 Features](#-features) • [🧱 Architecture](#-architecture--oop-concepts) • [🚀 Getting Started](#-getting-started) • [📂 Project Structure](#-project-structure) • [📖 How It Works](#-how-it-works)

</div>

---

## 📖 About the Project

This mini project was built as a practical exploration of four fundamental Java topics that every Computer Science student encounters: **Inheritance**, **Polymorphism**, **Multithreading**, and **File Handling**. Rather than demonstrating these concepts in isolation through dry code examples, this project weaves them together into a single cohesive application — a live, interactive voting system with a graphical user interface.

The idea is simple: imagine you're running a small election. Before the voting begins, you add candidates to the ballot. Voters then step up one by one, type their name, choose who they're voting for, and click **Vote**. Behind the scenes, each vote is cast inside its own Java `Thread`, candidate tallies are updated in a thread-safe way, and at any point the results can be displayed or saved permanently to a file. When the session ends, a fresh session can be started without losing the history of previous rounds.

This end-to-end flow makes the project genuinely usable — not just a textbook exercise — while keeping the implementation accessible to beginners who are still learning the fundamentals.

---

## ✨ Features

| Feature | Description |
|---|---|
| ➕ **Add Candidates** | Dynamically add any number of candidates before or during a session |
| ➖ **Remove Candidates** | Remove a candidate from the ballot at any time |
| 🗳️ **Cast Votes** | Enter your name, select a candidate, and click Vote |
| 📊 **View Results** | See a live tally of all votes and the current winner |
| 💾 **Save to File** | Append the session's results to `votes.txt` (never overwrites) |
| 🔄 **New Session** | Reset the booth for a fresh round; old data is auto-saved first |
| 🖥️ **Console Log** | A dark green terminal-style log displays every action in real time |
| 🏆 **Winner Detection** | Automatically detects the winner — or announces a tie |

---

## 🧱 Architecture & OOP Concepts

This project is deliberately structured so that each Java file maps to one or more of the four core concepts. Here is how the pieces fit together:

### 1. Inheritance — `Voter.java`, `RegularVoter.java`, `VIPVoter.java`

The foundation of the voter hierarchy is the abstract parent class `Voter`. It defines the three fields every voter has — `name`, `voterId`, and `hasVoted` — as well as a default `castVote()` method. Two child classes then extend this parent:

- **`RegularVoter`** — represents a standard voter. When they cast a vote, the log reads `[Regular] Alice voted for Bob`.
- **`VIPVoter`** — represents a priority voter. Their log entry reads `[VIP] Alice voted for Bob (Priority)`.

Both child classes use `super(name, voterId)` to call the parent's constructor, demonstrating how inheritance eliminates code duplication: neither child class needs to re-declare the `name`, `voterId`, or `hasVoted` fields.

```java
// RegularVoter inherits everything from Voter
public class RegularVoter extends Voter {
    public RegularVoter(String name, String voterId) {
        super(name, voterId);  // calls Voter's constructor
    }
    // ... overrides castVote()
}
```

### 2. Polymorphism — `Voter.java`, `RegularVoter.java`, `VIPVoter.java`, `VotingBooth.java`

Polymorphism is the ability of a single method call to behave differently depending on the actual type of the object at runtime. In this project, `VotingBooth.vote()` accepts a `Voter` reference — it doesn't know or care whether the object is a `RegularVoter` or a `VIPVoter`. It simply calls `voter.castVote(candidate)`, and Java determines at runtime which version to execute:

```java
// VotingBooth.java — polymorphism in action
public synchronized String vote(String candidate, Voter voter) {
    String message = voter.castVote(candidate);  // dynamic dispatch
    // ...
}
```

This means that if you were to add a third voter type (say `GuestVoter`) in the future, you wouldn't need to change `VotingBooth` at all — you'd just create a new class extending `Voter` and override `castVote()`. That is the power of polymorphism.

### 3. Multithreading — `VoterThread.java`, `VotingBooth.java`

Every time a voter clicks **Vote**, the application doesn't just process the vote inline on the main thread. Instead, it creates a new `VoterThread` — a class that extends Java's built-in `Thread` — and hands the vote off to it. The `run()` method inside `VoterThread` calls `booth.vote()`, which is marked `synchronized` to prevent race conditions when multiple threads try to update vote counts at the same time.

```java
// VotingSystem.java — launching the vote in a new thread
VoterThread thread = new VoterThread(voter, candidate, booth);
thread.start();   // triggers run() in a new thread
thread.join();    // wait for it to finish before reading result
```

The `synchronized` keyword on `VotingBooth.vote()` acts as a lock: only one thread can be inside that method at any given moment. This ensures that even if two people voted simultaneously, the vote counts would still be updated correctly — no votes would be lost or double-counted.

### 4. File Handling — `VotingBooth.java`

Voting data is precious. Rather than holding results only in memory (where they vanish when the app closes), the project writes session results to a plain text file called `votes.txt`. Crucially, the file is opened in **append mode** — meaning each new session's results are added to the *end* of the file without ever erasing what came before. The full history of every session is preserved.

```java
// VotingBooth.java — append mode preserves history
FileWriter fw = new FileWriter(filePath, true);  // true = append
fw.write("SESSION " + sessionNumber + " - " + timestamp + "\n");
// ... write each candidate's vote count ...
fw.close();
```

The file path is determined by calling `getCodeBase().getPath()` inside the applet. This is the safe, applet-approved way to get the project directory path — unlike `System.getProperty("user.dir")`, which is blocked by the applet security manager.

A typical `votes.txt` entry looks like this:

```
--------------------------------------------------
SESSION 1 - 23-Apr-2026 11:30 AM
--------------------------------------------------
Alice : 5 votes
Bob   : 3 votes
Charlie : 1 vote
Total Votes : 9
--------------------------------------------------
```

---

## 📂 Project Structure

```
Java-Mini-Project/
│
├── src/                        # All Java source files
│   ├── Voter.java              # Base class (Inheritance + Polymorphism)
│   ├── RegularVoter.java       # Child class — standard voter
│   ├── VIPVoter.java           # Child class — priority voter
│   ├── VoterThread.java        # Thread wrapper (Multithreading)
│   ├── VotingBooth.java        # Vote counter + file handler (File Handling)
│   └── VotingSystem.java       # Main Applet — GUI entry point
│
├── VotingSystem.html           # HTML launcher for appletviewer
├── votes.txt                   # Generated at runtime (session results)
├── .gitignore
├── LICENSE
└── README.md
```

---

## 🖥️ The User Interface

The applet window is divided into three zones:

**Header (dark navy)** — Contains the title and a 4-row form:
1. Voter Name — text field where the voter types their name
2. Vote For — dropdown populated with all current candidates
3. Add Candidate — text field + green Add button
4. Remove Candidate — dropdown + red Remove button

**Center (black console)** — A green-on-black terminal-style `TextArea` that logs every event in real time: candidates added, votes cast, results displayed, sessions saved. It is read-only so the user can't accidentally edit the log.

**Footer (dark navy)** — Four action buttons:
- 🔵 **Vote** — casts the vote
- 🟣 **Results** — displays current tallies and winner
- 🟢 **Save to File** — appends session to `votes.txt`
- ⚫ **New Session (Reset)** — auto-saves then clears everything

---

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) **8 or later**
- `appletviewer` tool (bundled with JDK 8; see note for JDK 11+)

> **Note for JDK 11+ users:** `appletviewer` was removed in JDK 11. You can either install JDK 8 alongside your current JDK, or use the [IcedTea-Web](https://icedtea.classpath.org/wiki/IcedTea-Web) plugin as a drop-in replacement.

### Clone the Repository

```bash
git clone https://github.com/ankushkhakale/Java-Mini-Project.git
cd Java-Mini-Project
```

### Compile

Navigate to the `src/` folder and compile all Java files:

```bash
cd src
javac *.java
```

This produces `.class` files in the same directory.

### Run

Go back to the project root and launch the applet:

```bash
cd ..
appletviewer VotingSystem.html
```

The voting system window will open immediately.

### Usage Walkthrough

1. **Add candidates** — Type a name in the "Add Candidate" field and click **Add**. Repeat for as many candidates as you need.
2. **Cast a vote** — Type a voter's name in "Voter Name", select a candidate from the "Vote For" dropdown, then click **Vote**. The console log confirms the vote.
3. **Check results** — Click **Results** to see vote counts for all candidates and the current leading candidate (or a tie message).
4. **Save the session** — Click **Save to File**. The results are appended to `votes.txt` in the project folder. The exact file path is printed in the log.
5. **Start a new session** — Click **New Session (Reset)**. The current session is auto-saved first, then all candidates and tallies are cleared so you can start fresh.

---

## 🧪 Concepts Quick-Reference

| Concept | Where Used | Key Keyword / Technique |
|---|---|---|
| **Inheritance** | `RegularVoter extends Voter`, `VIPVoter extends Voter` | `extends`, `super()` |
| **Polymorphism** | `VotingBooth.vote(Voter voter)` | Method overriding, dynamic dispatch |
| **Multithreading** | `VoterThread extends Thread` | `extends Thread`, `start()`, `join()`, `synchronized` |
| **File Handling** | `VotingBooth.appendSessionToFile()` | `FileWriter`, append mode (`true`) |

---

## 🔒 Security Note (Applet File Permissions)

Java Applets run in a sandboxed security environment. Certain operations such as reading `System.getProperty("user.dir")` are blocked by the applet security manager. This project avoids that restriction entirely by using `getCodeBase().getPath()` to determine the project directory — a method that is explicitly permitted inside applets. The `votes.txt` file is therefore always written next to the compiled `.class` files, in the same directory that was passed to `appletviewer`.

---

## 👤 Author

**Ankush Khakale**  
Full-Stack Developer · DevOps Engineer · Agentic AI Practitioner · Oracle ACE Apprentice

- 🌐 Portfolio: [ankushkhakalepage.netlify.app](https://ankushkhakalepage.netlify.app/)
- 🐙 GitHub: [@ankushkhakale](https://github.com/ankushkhakale)
- 🐦 Twitter: [@Stoic_Ankush](https://twitter.com/Stoic_Ankush)

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

*Built with ❤️ as a Java OOP Mini Project — April 2026*

</div>]]>