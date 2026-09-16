package library.service;

import library.model.Book;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryService {

    private static final int LOAN_DAYS = 14;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Map<String, Book> books = new LinkedHashMap<>();
    private int idCounter = 1;

    public LibraryService() {
        seedData();
    }

    // ── Seed demo data ────────────────────────────────────────
    private void seedData() {
        addBook("Clean Code",              "Robert C. Martin", "Technology");
        addBook("The Pragmatic Programmer","David Thomas",     "Technology");
        addBook("Sapiens",                 "Yuval Noah Harari","History");
        addBook("Atomic Habits",           "James Clear",      "Self-Help");
        addBook("1984",                    "George Orwell",    "Fiction");
        addBook("Deep Work",               "Cal Newport",      "Self-Help");
    }

    // ── CRUD ──────────────────────────────────────────────────
    public Book addBook(String title, String author, String genre) {
        String id = String.format("B%03d", idCounter++);
        Book b = new Book(id, title, author, genre);
        books.put(id, b);
        return b;
    }

    public boolean removeBook(String bookId) {
        Book b = books.get(bookId);
        if (b == null || b.isIssued()) return false;
        books.remove(bookId);
        return true;
    }

    // ── Issue / Return ────────────────────────────────────────
    public String issueBook(String bookId, String studentName) {
        Book b = books.get(bookId);
        if (b == null)       return "Book not found.";
        if (b.isIssued())    return "Book is already issued to " + b.getIssuedTo() + ".";
        if (studentName == null || studentName.isBlank()) return "Student name cannot be empty.";

        LocalDate today = LocalDate.now();
        LocalDate due   = today.plusDays(LOAN_DAYS);
        b.setIssued(true);
        b.setIssuedTo(studentName.trim());
        b.setIssueDate(today.format(FMT));
        b.setDueDate(due.format(FMT));
        return "Issued successfully. Due date: " + due.format(FMT);
    }

    public String returnBook(String bookId) {
        Book b = books.get(bookId);
        if (b == null)    return "Book not found.";
        if (!b.isIssued()) return "Book was not issued.";

        long overdueDays = getOverdueDays(b);
        b.setIssued(false);
        b.setIssuedTo("");
        b.setIssueDate("");
        b.setDueDate("");
        if (overdueDays > 0)
            return "Returned. OVERDUE by " + overdueDays + " day(s). Fine: ₹" + (overdueDays * 5);
        return "Returned successfully. No fine.";
    }

    // ── Search ────────────────────────────────────────────────
    public List<Book> search(String query) {
        String q = query.toLowerCase().trim();
        return books.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q)
                          || b.getAuthor().toLowerCase().contains(q)
                          || b.getGenre().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ── Overdue ───────────────────────────────────────────────
    public List<Book> getOverdueBooks() {
        return books.values().stream()
                .filter(b -> b.isIssued() && getOverdueDays(b) > 0)
                .collect(Collectors.toList());
    }

    public long getOverdueDays(Book b) {
        if (!b.isIssued() || b.getDueDate().isBlank()) return 0;
        LocalDate due = LocalDate.parse(b.getDueDate(), FMT);
        long days = ChronoUnit.DAYS.between(due, LocalDate.now());
        return Math.max(0, days);
    }

    // ── Accessors ─────────────────────────────────────────────
    public Collection<Book> getAllBooks()      { return books.values(); }
    public Book             getBook(String id) { return books.get(id);  }
    public int              totalBooks()       { return books.size();   }

    public long availableCount() {
        return books.values().stream().filter(b -> !b.isIssued()).count();
    }
    public long issuedCount() {
        return books.values().stream().filter(Book::isIssued).count();
    }
    public long overdueCount() {
        return getOverdueBooks().size();
    }
}
