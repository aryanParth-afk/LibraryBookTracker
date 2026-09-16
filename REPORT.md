# 📚 Library Book Tracker — Project Report

> **Course:** Java-Vityarthi Coursework  
> **Author:** ARYAN PARTH  
> **University:** VIT Bhopal University  
> **Enrollment:** 24BCY10337  
> **Date:** September 2026  

---

## 1. Project Overview

**Library Book Tracker** is a desktop application built with **Java Swing** that enables a librarian to manage a small library's catalogue entirely through a graphical user interface. It covers the full lifecycle of a library book — from adding a book to the catalogue through issuing it to a student, tracking its due date, and calculating overdue fines on return.

The project has **zero external dependencies**; it runs on any machine with Java 14+ using only the JDK standard library.

---

## 2. Objectives

| # | Objective |
|---|-----------|
| 1 | Demonstrate core Object-Oriented Programming concepts in Java |
| 2 | Implement a functional, multi-panel Java Swing GUI |
| 3 | Separate concerns using the **MVC (Model-View-Controller)** pattern |
| 4 | Apply the **Java Time API** for real date arithmetic |
| 5 | Use **Java Streams** for efficient search and aggregation |
| 6 | Deliver a polished, dark-themed user experience without third-party libraries |

---

## 3. Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 14+ |
| GUI Framework | Java Swing (JDK built-in) |
| Look & Feel | Nimbus LAF with custom dark-theme overrides |
| Date Handling | `java.time` (LocalDate, ChronoUnit, DateTimeFormatter) |
| Collections | `java.util` (LinkedHashMap, ArrayList, List, streams) |
| Build Tool | Plain `javac` / any Java IDE |
| External Libraries | **None** |

---

## 4. Project Structure

```
LibraryBookTracker/
├── Book.java              <- Data model (library.model package)
├── LibraryService.java    <- Business / service layer (library.service package)
├── Main.java              <- Application entry point (library package)
├── MainWindow.java        <- Main Swing window / View (library.ui package)
├── BookTableModel.java    <- MVC table adapter (library.ui package)
├── Theme.java             <- Centralised colour & font constants (library.ui)
├── RoundedPanel.java      <- Custom rounded-corner panel widget (library.ui)
├── StyledButton.java      <- Custom hover-aware button widget (library.ui)
├── README.md              <- User-facing documentation
└── REPORT.md              <- This file
```

### Package Layout

```
library
│
├── Main.java                  (root package)
│
├── model/
│   └── Book.java
│
├── service/
│   └── LibraryService.java
│
└── ui/
    ├── MainWindow.java
    ├── BookTableModel.java
    ├── Theme.java
    ├── RoundedPanel.java
    └── StyledButton.java
```

---

## 5. Architecture — MVC Pattern

The project follows a clear **Model-View-Controller** separation:

```
+-------------------------------------------------------------+
|                        VIEW LAYER                           |
|   MainWindow.java  <->  BookTableModel.java                 |
|   (JFrame, panels, dialogs, buttons, table display)         |
+----------------------------+--------------------------------+
                             |  calls
+----------------------------v--------------------------------+
|                      SERVICE LAYER                          |
|                  LibraryService.java                        |
|       (CRUD, issue/return logic, search, fine calc)         |
+----------------------------+--------------------------------+
                             |  operates on
+----------------------------v--------------------------------+
|                       MODEL LAYER                           |
|                       Book.java                             |
|          (data fields, getters, setters, toString)          |
+-------------------------------------------------------------+
```

- **Model** — `Book.java` is a pure POJO; it holds book data and exposes accessors.
- **Service / Controller** — `LibraryService.java` encapsulates all business rules. It is the only class that mutates `Book` objects.
- **View** — `MainWindow.java` constructs and arranges all Swing components. `BookTableModel.java` bridges the service data to the `JTable`.

---

## 6. Class-by-Class Analysis

### 6.1 `Book.java` — Model
**Package:** `library.model`  
**Lines:** 47  

The core data entity representing a single library book.

| Field | Type | Purpose |
|-------|------|---------|
| `bookId` | `String` | Auto-generated unique ID (e.g., `B001`) |
| `title` | `String` | Book title |
| `author` | `String` | Author name |
| `genre` | `String` | Genre / category |
| `isIssued` | `boolean` | Whether the book is currently issued |
| `issuedTo` | `String` | Name of the student who borrowed it |
| `issueDate` | `String` | Date issued (`yyyy-MM-dd`) |
| `dueDate` | `String` | Return deadline (`yyyy-MM-dd`) |

**Key Design Decisions:**
- Dates are stored as `String` (formatted `yyyy-MM-dd`) rather than `LocalDate` to keep the model a simple POJO.
- All date arithmetic is centralised in `LibraryService`.
- `toString()` provides a human-readable representation for debugging.

---

### 6.2 `LibraryService.java` — Business Logic
**Package:** `library.service`  
**Lines:** 118  

The single source of truth for all library operations. Holds the book catalogue in a `LinkedHashMap<String, Book>` (O(1) lookup, preserves insertion order).

**Constants:**

| Constant | Value | Meaning |
|----------|-------|---------|
| `LOAN_DAYS` | `14` | Borrow period in days |
| Fine rate | Rs.5 / day | Charged for each overdue day |

**Public API:**

| Method | Signature | Description |
|--------|-----------|-------------|
| Add | `addBook(title, author, genre)` | Auto-generates ID and stores book |
| Remove | `removeBook(bookId)` | Deletes only if book is not issued |
| Issue | `issueBook(bookId, studentName)` | Sets issue/due dates; returns status string |
| Return | `returnBook(bookId)` | Clears issue state; computes and returns fine message |
| Search | `search(query)` | Case-insensitive filter on title, author, genre via streams |
| Overdue | `getOverdueBooks()` | Returns list of all books past their due date |
| Stats | `totalBooks()`, `availableCount()`, `issuedCount()`, `overdueCount()` | Live statistics for the header bar |

**Seed Data (6 preloaded books):**

| ID | Title | Author | Genre |
|----|-------|--------|-------|
| B001 | Clean Code | Robert C. Martin | Technology |
| B002 | The Pragmatic Programmer | David Thomas | Technology |
| B003 | Sapiens | Yuval Noah Harari | History |
| B004 | Atomic Habits | James Clear | Self-Help |
| B005 | 1984 | George Orwell | Fiction |
| B006 | Deep Work | Cal Newport | Self-Help |

---

### 6.3 `MainWindow.java` — Main GUI Window
**Package:** `library.ui`  
**Lines:** 371  

The primary `JFrame` that assembles the complete UI. Uses `BorderLayout` to divide the window into three zones:

```
+-------------------------------------------------+
|  HEADER: App title  |  Total / Available / ...  |
+----------------------------+--------------------+
|                            |                    |
|   CENTRE                   |   ACTION PANEL     |
|   Search bar               |   + Add Book       |
|   ──────────               |   x Remove Book    |
|   Book Table (JTable)      |   ^ Issue Book     |
|                            |   v Return Book    |
|                            |   ! Overdue List   |
+----------------------------+--------------------+
|  FOOTER: "Library Book Tracker - BYOP Java"     |
+-------------------------------------------------+
```

**Component Breakdown:**

| Builder Method | Component Built |
|----------------|----------------|
| `buildHeader()` | Title label + live stat labels |
| `buildCenter()` | Container for search bar + table + actions |
| `buildSearchBar()` | `JTextField` + Search / Reset buttons |
| `buildTablePane()` | `JScrollPane` wrapping a styled `JTable` |
| `buildActionPanel()` | Vertical list of `StyledButton` actions |
| `buildFooter()` | Status / branding footer bar |

**Dialog Methods:**

| Method | Triggered By | Description |
|--------|-------------|-------------|
| `dialogAddBook()` | Add Book button | Form dialog: title, author, genre fields |
| `dialogRemoveBook()` | Remove Book button | Confirm dialog before deletion |
| `dialogIssueBook()` | Issue Book button | Form dialog: student name input |
| `dialogReturnBook()` | Return Book button | Immediate action, shows fine result |
| `showOverdueDialog()` | Overdue List button | Scrollable monospaced text area |

**Table Visual Features:**
- Alternating row colours (`BG_CARD` / `BG_SURFACE`) for readability.
- Custom `DefaultTableCellRenderer` for the **Status** column — green for "Available", amber for "Issued".
- Selection highlight uses a semi-transparent accent colour (`ACCENT_SOFT`).

---

### 6.4 `BookTableModel.java` — MVC Table Adapter
**Package:** `library.ui`  
**Lines:** 56  

Extends `AbstractTableModel` to bridge `LibraryService` data to a `JTable`.

**Columns exposed:**

| Col | Header | Source |
|-----|--------|--------|
| 0 | ID | `book.getBookId()` |
| 1 | Title | `book.getTitle()` |
| 2 | Author | `book.getAuthor()` |
| 3 | Genre | `book.getGenre()` |
| 4 | Status | `"Available"` / `"Issued"` |
| 5 | Issued To | `book.getIssuedTo()` or `"—"` |
| 6 | Due Date | `book.getDueDate()` or `"—"` |

Key methods:
- `refresh()` — re-fetches all books from the service and fires a table data change event.
- `setRows(List<Book>)` — replaces the display list with filtered search results.
- `getBookAt(int row)` — used by `MainWindow` to retrieve the selected `Book` object.

---

### 6.5 `Theme.java` — Design System
**Package:** `library.ui`  
**Lines:** 29  

A single static constants class that acts as the entire design system. Centralising all colours and fonts here means changing the look of the app requires editing one file only.

**Colour Palette:**

| Constant | Hex Approx. | Role |
|----------|-------------|------|
| `BG_DARK` | #0D1117 | App background |
| `BG_CARD` | #161B22 | Panel / card background |
| `BG_SURFACE` | #1E252E | Input fields, table alternate rows |
| `ACCENT` | #58A6FF | Primary blue — titles, issued stat |
| `ACCENT_SOFT` | #388BFD (alpha) | Table row selection highlight |
| `SUCCESS` | #3FB950 | Available status, Add button |
| `WARNING` | #D29922 | Issued status, Overdue button |
| `DANGER` | #F85149 | Danger actions, overdue text |
| `TEXT_PRIMARY` | #E6EDF3 | Main readable text |
| `TEXT_MUTED` | #8B949E | Labels, footer, column headers |
| `BORDER` | #30363D | Subtle borders around panels |

**Font Scale:**

| Constant | Family | Style | Size |
|----------|--------|-------|------|
| `FONT_TITLE` | Segoe UI | Bold | 22 |
| `FONT_HEADING` | Segoe UI | Bold | 14 |
| `FONT_BODY` | Segoe UI | Plain | 13 |
| `FONT_SMALL` | Segoe UI | Plain | 11 |
| `FONT_MONO` | Consolas | Plain | 12 |

Corner radius constant: `RADIUS = 10` (used by `RoundedPanel` and `StyledButton`).

---

### 6.6 `RoundedPanel.java` — Custom UI Widget
**Package:** `library.ui`  
**Lines:** 26  

Extends `JPanel` and overrides `paintComponent()` to draw a rounded rectangle background using Java2D (`Graphics2D.fillRoundRect`). Anti-aliasing is always enabled for smooth edges. Used wherever the design calls for card-style containers.

---

### 6.7 `StyledButton.java` — Custom UI Widget
**Package:** `library.ui`  
**Lines:** 42  

Extends `JButton` and provides:
- **Rounded pill shape** — drawn with `paintComponent()` override and `Theme.RADIUS`.
- **Hover effect** — a `MouseAdapter` toggles a `hovered` flag; the button brightens on `mouseEntered` and reverts on `mouseExited`.
- **Consistent typography** — always uses `Theme.FONT_HEADING` and `Theme.TEXT_PRIMARY`.
- Standard Swing decorations (border paint, content area fill, focus paint) are disabled so only the custom rendering is visible.

---

### 6.8 `Main.java` — Entry Point
**Package:** `library`  
**Lines:** 35  

Bootstraps the application:
1. Enables system font anti-aliasing (`awt.useSystemAAFontSettings`, `swing.aatext`).
2. Installs the **Nimbus** Look & Feel.
3. Overrides Nimbus UI defaults with dark theme colours from `Theme.java`.
4. Dispatches `MainWindow` construction onto the **Event Dispatch Thread** via `SwingUtilities.invokeLater`.

---

## 7. Feature Walkthrough

### 7.1 Add Book
- Click **+ Add Book** in the sidebar.
- A dark-themed dialog appears with fields: Title (required), Author (required), Genre (optional).
- On OK, `LibraryService.addBook()` assigns the next sequential ID and stores the book.
- The table and header stats refresh immediately.

### 7.2 Remove Book
- Select a row in the table.
- Click **x Remove Book**.
- If the book is currently issued, a warning is shown and removal is blocked.
- A confirmation dialog prevents accidental deletion.

### 7.3 Issue Book
- Select an available book.
- Click **Issue Book**.
- Enter the borrowing student's name.
- `LibraryService.issueBook()` records today's date as `issueDate` and `today + 14 days` as `dueDate`.
- The status column immediately turns amber ("Issued") and the student name and due date appear.

### 7.4 Return Book
- Select an issued book.
- Click **Return Book**.
- `LibraryService.returnBook()` computes overdue days using `ChronoUnit.DAYS.between(dueDate, today)`.
- If overdue, the fine message is: `"Returned. OVERDUE by N day(s). Fine: Rs. N x 5"`.
- If on time: `"Returned successfully. No fine."`.
- All issue fields on the book are cleared.

### 7.5 Search
- Type any keyword in the search field (title, author, or genre).
- Click **Search** (or use the Reset button to restore all books).
- `LibraryService.search()` uses a Java Stream with case-insensitive `contains()` matching.

### 7.6 Overdue List
- Click **Overdue List**.
- A scrollable monospaced dialog lists every currently overdue book with: Book ID, title, borrower name, due date, number of overdue days, and total fine.

---

## 8. OOP Concepts Demonstrated

| Concept | Where Applied |
|---------|--------------|
| **Encapsulation** | `Book.java` — all fields are `private`; state is mutated only through public setters |
| **Abstraction** | `LibraryService` hides the internal `Map` and all business rules from the UI layer |
| **Inheritance** | `BookTableModel extends AbstractTableModel`, `StyledButton extends JButton`, `RoundedPanel extends JPanel` |
| **Polymorphism** | `paintComponent()` is overridden in both `StyledButton` and `RoundedPanel`; `DefaultTableCellRenderer` is also overridden inline |
| **Collections** | `LinkedHashMap<String, Book>` for O(1) lookup; `ArrayList<Book>` for filtered lists |
| **Java Streams** | `search()`, `getOverdueBooks()`, `availableCount()`, `issuedCount()`, `overdueCount()` |
| **Java Time API** | `LocalDate.now()`, `LocalDate.plusDays()`, `ChronoUnit.DAYS.between()`, `DateTimeFormatter` |
| **MVC Pattern** | `Book` = Model, `LibraryService` = Controller/Service, `MainWindow` + `BookTableModel` = View |
| **Event Handling** | `ActionListener` on all buttons, `MouseAdapter` for hover effects in `StyledButton` |
| **Custom Painting** | Java2D `Graphics2D.fillRoundRect()` with anti-aliasing in `RoundedPanel` and `StyledButton` |

---

## 9. Business Rules

| Rule | Detail |
|------|--------|
| Loan Period | **14 days** from issue date |
| Fine Rate | **Rs.5 per overdue day** |
| Book ID format | Auto-generated: `B001`, `B002`, ... (zero-padded to 3 digits) |
| Remove restriction | A book **cannot** be removed while issued |
| Issue restriction | A book **cannot** be issued if already issued |
| Student name | Cannot be blank when issuing |

---

## 10. GUI Layout & Window Specifications

| Property | Value |
|----------|-------|
| Window Size | 1100 x 700 px (default) |
| Minimum Size | 900 x 580 px |
| Opening Position | Centred on screen |
| Look & Feel | Nimbus (overridden to dark theme) |
| Font | Segoe UI throughout; Consolas for monospaced output |
| Table Row Height | 30 px |
| Action Panel Width | 185 px (fixed) |
| Column Widths | ID:60, Title:220, Author:160, Genre:110, Status:90, IssuedTo:140, DueDate:100 |

---

## 11. Data Flow Diagram

```
User Action (button click)
        |
        v
MainWindow (ActionListener)
        |  calls
        v
LibraryService method
        |  reads/writes
        v
Book objects in LinkedHashMap
        |
        v  (via tableModel.refresh())
BookTableModel.fireTableDataChanged()
        |
        v
JTable re-renders  -->  User sees updated UI
```

---

## 12. Limitations & Known Constraints

| Limitation | Impact |
|------------|--------|
| **No persistence** | All data is lost when the application closes |
| **In-memory storage only** | Not suitable for large catalogues |
| **Single user** | No concurrent access or user authentication |
| **No student register** | Borrowing history is not tracked after return |
| **Date as String** | Date validation relies on well-formed input only |
| **No multi-copy support** | Only one copy of any title can exist |

---

## 13. Potential Enhancements

| Enhancement | Description |
|------------|-------------|
| File / JSON Persistence | Save and load the catalogue between sessions |
| Student Register | Maintain borrowing history per student |
| CSV Export | Export overdue report for offline sharing |
| Multiple Copies | Allow N copies of the same title |
| Role-Based Login | Separate librarian and student views |
| Due Date Notifications | Alert on app startup for near-due books |
| FlatLaf Integration | Third-party LAF for a more modern look on all platforms |

---

## 14. How to Compile & Run

### Prerequisites
- Java JDK **14 or higher**
- Verify with: `java -version`

### Command Line

```bash
# Clone
git clone https://github.com/aryanParth-afk/LibraryBookTracker.git
cd LibraryBookTracker

# Compile (all .java files in the same directory for this flat layout)
javac -d out *.java

# Run
java -cp out library.Main
```

### IDE (Recommended)
1. Open the project folder in **IntelliJ IDEA**, **Eclipse**, or **VS Code**.
2. Ensure `library` is treated as the root package.
3. Run `Main.java` (the `main` method entry point).

---

## 15. Code Metrics Summary

| File | Package | Lines | Responsibility |
|------|---------|-------|---------------|
| `Main.java` | `library` | 35 | App bootstrap, LAF config |
| `Book.java` | `library.model` | 47 | Data model / POJO |
| `LibraryService.java` | `library.service` | 118 | Business rules, CRUD, search |
| `BookTableModel.java` | `library.ui` | 56 | JTable <-> service bridge |
| `Theme.java` | `library.ui` | 29 | Design system constants |
| `RoundedPanel.java` | `library.ui` | 26 | Custom rounded panel widget |
| `StyledButton.java` | `library.ui` | 42 | Custom hover-aware button |
| `MainWindow.java` | `library.ui` | 371 | Full GUI, dialogs, layout |
| **Total** | | **~724** | |

---

## 16. Conclusion

**Library Book Tracker** successfully demonstrates a fully functional, visually polished Java Swing desktop application. The project exhibits clean MVC separation, proper encapsulation, and makes thoughtful use of the Java standard library — including the modern `java.time` package for date arithmetic and Java Streams for queries — without any external dependencies.

The dark theme, colour-coded status indicators, and interactive hover effects on custom-painted widgets elevate it beyond a basic academic submission into a near-production quality prototype. The architecture is also extensible: adding persistence, a student register, or role-based login are natural next steps that the existing service/model separation would accommodate with minimal refactoring.

---

*Report generated for academic submission — VIT Bhopal University, September 2026.*
