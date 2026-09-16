package library.model;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String genre;
    private boolean isIssued;
    private String issuedTo;       // student name
    private String issueDate;      // yyyy-MM-dd
    private String dueDate;        // yyyy-MM-dd

    public Book(String bookId, String title, String author, String genre) {
        this.bookId   = bookId;
        this.title    = title;
        this.author   = author;
        this.genre    = genre;
        this.isIssued = false;
        this.issuedTo = "";
        this.issueDate = "";
        this.dueDate   = "";
    }

    // ── Getters ──────────────────────────────────────────────
    public String  getBookId()    { return bookId;    }
    public String  getTitle()     { return title;     }
    public String  getAuthor()    { return author;    }
    public String  getGenre()     { return genre;     }
    public boolean isIssued()     { return isIssued;  }
    public String  getIssuedTo()  { return issuedTo;  }
    public String  getIssueDate() { return issueDate; }
    public String  getDueDate()   { return dueDate;   }

    // ── Setters ──────────────────────────────────────────────
    public void setIssued(boolean issued)      { this.isIssued  = issued;    }
    public void setIssuedTo(String issuedTo)   { this.issuedTo  = issuedTo;  }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
    public void setDueDate(String dueDate)     { this.dueDate   = dueDate;   }

    @Override
    public String toString() {
        return String.format("[%s] %s by %s (%s) — %s",
                bookId, title, author, genre,
                isIssued ? "Issued to " + issuedTo : "Available");
    }
}
