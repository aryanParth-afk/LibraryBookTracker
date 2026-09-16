# 📚 Library Book Tracker

A Java Swing desktop application for managing a small library — track books, issue and return them, search the catalogue, and monitor overdue borrowings with automatic fine calculation.

Built as a **BYOP (Bring Your Own Project)** capstone for an Object-Oriented Programming in Java course.

---

## ✨ Features

| Feature | Description |
|---|---|
| ➕ Add Book | Add a new book with title, author, and genre; ID auto-assigned |
| ❌ Remove Book | Remove any book that is not currently issued |
| ↗ Issue Book | Issue a book to a student; due date set 14 days from today |
| ↩ Return Book | Return a book; overdue fine (₹5/day) calculated automatically |
| 🔍 Search | Filter by title, author, or genre in real time |
| ⚠ Overdue List | View all overdue books with borrower names and fine amounts |
| 📊 Live Stats | Header bar shows Total / Available / Issued / Overdue counts |

---

## 🖼 Screenshots

> _Run the project and interact with the dark-themed GUI._

The main window shows a searchable book table with colour-coded status badges (green = Available, amber = Issued). The right sidebar contains all action buttons.

---

## 🛠 Tech Stack

- **Language:** Java 14+
- **GUI:** Java Swing (no external dependencies)
- **Build:** Plain `javac` / any Java IDE (IntelliJ IDEA, Eclipse, VS Code)

---

## 🚀 Getting Started

### Prerequisites

- Java JDK **14 or higher** installed  
  Check: `java -version`

### Clone the repository

```bash
git clone https://github.com/<your-username>/LibraryBookTracker.git
cd LibraryBookTracker
```

### Compile

```bash
# From the project root
javac -d out -sourcepath src src/library/Main.java
```

### Run

```bash
java -cp out library.Main
```

### Using an IDE (recommended)

1. Open the project folder in **IntelliJ IDEA** or **Eclipse**.
2. Mark `src/` as the **Sources Root**.
3. Run `library.Main` directly.

---

## 📁 Project Structure

```
LibraryBookTracker/
├── src/
│   └── library/
│       ├── Main.java                  ← Entry point
│       ├── model/
│       │   └── Book.java              ← Data model
│       ├── service/
│       │   └── LibraryService.java    ← Business logic
│       └── ui/
│           ├── MainWindow.java        ← Main GUI window
│           ├── BookTableModel.java    ← MVC table model
│           ├── Theme.java             ← Colours & fonts
│           ├── RoundedPanel.java      ← Custom panel
│           └── StyledButton.java      ← Custom button
├── REPORT.md                          ← Project report
└── README.md                          ← This file
```

---

## 🎮 How to Use

1. **Launch the app** — 6 demo books are preloaded.
2. **Add a book** — Click *Add Book*, fill in the form, click OK.
3. **Issue a book** — Click a row to select it, click *Issue Book*, enter the student's name.
4. **Return a book** — Select an issued book, click *Return Book*. Any fine is shown instantly.
5. **Search** — Type in the search bar and click *Search* (or press Enter). Click *Reset* to show all books.
6. **View overdue** — Click *Overdue List* to see all overdue borrowings and fines.

---

## 🔢 Business Rules

- Loan period: **14 days**
- Overdue fine: **₹5 per day**
- A book cannot be removed while it is issued
- Book IDs are auto-generated in the format `B001`, `B002`, …

---

## 📖 OOP Concepts Demonstrated

- **Encapsulation** — `Book` class with private fields and public getters/setters
- **Abstraction** — `LibraryService` hides data management from the UI layer
- **Collections** — `LinkedHashMap` for O(1) lookup with insertion-order display
- **Java Streams** — Search and aggregation queries
- **Java Time API** — `LocalDate` and `ChronoUnit` for date arithmetic
- **MVC Pattern** — `BookTableModel extends AbstractTableModel`
- **Custom Painting** — `RoundedPanel` and `StyledButton` with `paintComponent` overrides
- **Event Handling** — `ActionListener` and `MouseAdapter`

---

## 🔮 Potential Enhancements

- [ ] File / JSON persistence across sessions
- [ ] Student register with borrowing history
- [ ] Export overdue report to CSV
- [ ] Multiple copies of the same book
- [ ] Role-based login (Librarian / Student)

---

## 👤 Author

**KAJAL PAREEK**  
VIT BHOPAL UNIVERSITY 
[24BHI10086]

---

## 📄 License

This project is submitted as academic coursework. Feel free to use it as a reference.
