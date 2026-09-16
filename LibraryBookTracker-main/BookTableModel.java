package library.ui;

import library.model.Book;
import library.service.LibraryService;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookTableModel extends AbstractTableModel {

    private static final String[] COLS =
            {"ID", "Title", "Author", "Genre", "Status", "Issued To", "Due Date"};

    private List<Book> rows = new ArrayList<>();
    private final LibraryService service;

    public BookTableModel(LibraryService service) {
        this.service = service;
        refresh();
    }

    public void refresh() {
        rows = new ArrayList<>(service.getAllBooks());
        fireTableDataChanged();
    }

    public void setRows(List<Book> filtered) {
        rows = new ArrayList<>(filtered);
        fireTableDataChanged();
    }

    public Book getBookAt(int row) {
        return rows.get(row);
    }

    @Override public int getRowCount()    { return rows.size(); }
    @Override public int getColumnCount() { return COLS.length; }
    @Override public String getColumnName(int col) { return COLS[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Book b = rows.get(row);
        return switch (col) {
            case 0 -> b.getBookId();
            case 1 -> b.getTitle();
            case 2 -> b.getAuthor();
            case 3 -> b.getGenre();
            case 4 -> b.isIssued() ? "Issued" : "Available";
            case 5 -> b.isIssued() ? b.getIssuedTo() : "—";
            case 6 -> b.isIssued() ? b.getDueDate()  : "—";
            default -> "";
        };
    }
}
