package library.ui;

import library.model.Book;
import library.service.LibraryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class MainWindow extends JFrame {

    private final LibraryService service = new LibraryService();
    private BookTableModel tableModel;
    private JTable table;
    private JLabel lblTotal, lblAvailable, lblIssued, lblOverdue;

    public MainWindow() {
        setTitle("Library Book Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_DARK);

        setLayout(new BorderLayout(0, 0));
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── Header ────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_CARD);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Title
        JLabel title = new JLabel("📚  Library Book Tracker");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT);

        // Stats row
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        stats.setOpaque(false);
        lblTotal     = statLabel("Total",     String.valueOf(service.totalBooks()),    Theme.TEXT_MUTED);
        lblAvailable = statLabel("Available", String.valueOf(service.availableCount()), Theme.SUCCESS);
        lblIssued    = statLabel("Issued",    String.valueOf(service.issuedCount()),    Theme.ACCENT);
        lblOverdue   = statLabel("Overdue",   String.valueOf(service.overdueCount()),   Theme.DANGER);
        stats.add(lblTotal);
        stats.add(lblAvailable);
        stats.add(lblIssued);
        stats.add(lblOverdue);

        header.add(title, BorderLayout.WEST);
        header.add(stats, BorderLayout.EAST);
        return header;
    }

    private JLabel statLabel(String key, String val, Color color) {
        JLabel lbl = new JLabel(key + ": " + val);
        lbl.setFont(Theme.FONT_HEADING);
        lbl.setForeground(color);
        return lbl;
    }

    private void refreshStats() {
        lblTotal    .setText("Total: "     + service.totalBooks());
        lblAvailable.setText("Available: " + service.availableCount());
        lblIssued   .setText("Issued: "    + service.issuedCount());
        lblOverdue  .setText("Overdue: "   + service.overdueCount());
    }

    // ── Centre (search bar + table + action panel) ─────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(12, 16, 0, 16));

        center.add(buildSearchBar(),  BorderLayout.NORTH);
        center.add(buildTablePane(),  BorderLayout.CENTER);
        center.add(buildActionPanel(),BorderLayout.EAST);
        return center;
    }

    // ── Search bar ────────────────────────────────────────────
    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 4, 0));

        JTextField searchField = new JTextField();
        styleField(searchField);
        searchField.setFont(Theme.FONT_BODY);
        searchField.putClientProperty("JTextField.placeholderText", "Search by title, author or genre…");

        StyledButton btnSearch = new StyledButton("Search", Theme.ACCENT);
        btnSearch.setPreferredSize(new Dimension(110, 36));
        StyledButton btnReset  = new StyledButton("Reset",  Theme.BG_SURFACE);
        btnReset.setPreferredSize(new Dimension(90, 36));

        btnSearch.addActionListener(e -> {
            String q = searchField.getText().trim();
            if (q.isEmpty()) { tableModel.refresh(); return; }
            List<Book> results = service.search(q);
            tableModel.setRows(results);
        });
        btnReset.addActionListener(e -> { searchField.setText(""); tableModel.refresh(); });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnSearch);
        btnRow.add(btnReset);

        bar.add(searchField, BorderLayout.CENTER);
        bar.add(btnRow,      BorderLayout.EAST);
        return bar;
    }

    // ── Table ─────────────────────────────────────────────────
    private JScrollPane buildTablePane() {
        tableModel = new BookTableModel(service);
        table = new JTable(tableModel);
        table.setBackground(Theme.BG_CARD);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Theme.ACCENT_SOFT);
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.getTableHeader().setBackground(Theme.BG_SURFACE);
        table.getTableHeader().setForeground(Theme.TEXT_MUTED);
        table.getTableHeader().setFont(Theme.FONT_HEADING);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Column widths
        int[] widths = {60, 220, 160, 110, 90, 140, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Status cell colours
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setFont(Theme.FONT_SMALL);
                String s = val.toString();
                if ("Available".equals(s))   { setForeground(Theme.SUCCESS); }
                else if ("Issued".equals(s)) { setForeground(Theme.WARNING); }
                setBackground(sel ? Theme.ACCENT_SOFT : Theme.BG_CARD);
                setOpaque(true);
                return this;
            }
        });

        // Alternating row colours
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (col == 4) return table.getColumnModel().getColumn(4)
                        .getCellRenderer()
                        .getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(sel ? Theme.ACCENT_SOFT :
                        (row % 2 == 0 ? Theme.BG_CARD : Theme.BG_SURFACE));
                setForeground(Theme.TEXT_PRIMARY);
                setFont(Theme.FONT_BODY);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                setOpaque(true);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(Theme.BG_CARD);
        scroll.getViewport().setBackground(Theme.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        return scroll;
    }

    // ── Right-side action panel ───────────────────────────────
    private JPanel buildActionPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0, 12, 0, 0));
        panel.setPreferredSize(new Dimension(185, 0));

        StyledButton btnAdd     = new StyledButton("＋  Add Book",     Theme.SUCCESS);
        StyledButton btnRemove  = new StyledButton("✕  Remove Book",  Theme.DANGER);
        StyledButton btnIssue   = new StyledButton("↗  Issue Book",   Theme.ACCENT);
        StyledButton btnReturn  = new StyledButton("↩  Return Book",  new Color(88, 130, 200));
        StyledButton btnOverdue = new StyledButton("⚠  Overdue List", Theme.WARNING);

        for (StyledButton b : new StyledButton[]{btnAdd, btnRemove, btnIssue, btnReturn, btnOverdue}) {
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        btnAdd    .addActionListener(e -> dialogAddBook());
        btnRemove .addActionListener(e -> dialogRemoveBook());
        btnIssue  .addActionListener(e -> dialogIssueBook());
        btnReturn .addActionListener(e -> dialogReturnBook());
        btnOverdue.addActionListener(e -> showOverdueDialog());

        panel.add(sectionLabel("ACTIONS"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnAdd);    panel.add(Box.createVerticalStrut(8));
        panel.add(btnRemove); panel.add(Box.createVerticalStrut(8));
        panel.add(btnIssue);  panel.add(Box.createVerticalStrut(8));
        panel.add(btnReturn); panel.add(Box.createVerticalStrut(8));
        panel.add(btnOverdue);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_MUTED);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    // ── Footer ────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Theme.BG_CARD);
        footer.setBorder(new EmptyBorder(6, 0, 6, 0));
        JLabel lbl = new JLabel("Library Book Tracker  •  BYOP Java Project");
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_MUTED);
        footer.add(lbl);
        return footer;
    }

    // ── Dialogs ───────────────────────────────────────────────
    private void dialogAddBook() {
        JTextField fTitle  = new JTextField(20); styleField(fTitle);
        JTextField fAuthor = new JTextField(20); styleField(fAuthor);
        JTextField fGenre  = new JTextField(20); styleField(fGenre);

        JPanel form = darkForm();
        form.add(darkLabel("Title:"));  form.add(fTitle);
        form.add(darkLabel("Author:")); form.add(fAuthor);
        form.add(darkLabel("Genre:"));  form.add(fGenre);

        int res = darkDialog("Add New Book", form);
        if (res != JOptionPane.OK_OPTION) return;
        if (fTitle.getText().isBlank() || fAuthor.getText().isBlank()) {
            warn("Title and Author are required."); return;
        }
        service.addBook(fTitle.getText().trim(), fAuthor.getText().trim(), fGenre.getText().trim());
        refresh(); info("Book added successfully.");
    }

    private void dialogRemoveBook() {
        Book b = selectedBook(); if (b == null) return;
        if (b.isIssued()) { warn("Cannot remove an issued book."); return; }
        int c = JOptionPane.showConfirmDialog(this,
                "Remove \"" + b.getTitle() + "\"?", "Confirm Remove",
                JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        service.removeBook(b.getBookId());
        refresh(); info("Book removed.");
    }

    private void dialogIssueBook() {
        Book b = selectedBook(); if (b == null) return;
        if (b.isIssued()) { warn("Book already issued to " + b.getIssuedTo()); return; }

        JTextField fName = new JTextField(20); styleField(fName);
        JPanel form = darkForm();
        form.add(darkLabel("Student Name:")); form.add(fName);

        int res = darkDialog("Issue Book — " + b.getTitle(), form);
        if (res != JOptionPane.OK_OPTION) return;
        String msg = service.issueBook(b.getBookId(), fName.getText());
        refresh(); info(msg);
    }

    private void dialogReturnBook() {
        Book b = selectedBook(); if (b == null) return;
        if (!b.isIssued()) { warn("This book is not currently issued."); return; }
        String msg = service.returnBook(b.getBookId());
        refresh(); info(msg);
    }

    private void showOverdueDialog() {
        java.util.List<Book> overdue = service.getOverdueBooks();
        if (overdue.isEmpty()) { info("No overdue books. 🎉"); return; }

        StringBuilder sb = new StringBuilder();
        for (Book b : overdue) {
            long days = service.getOverdueDays(b);
            sb.append(String.format("%-8s %-30s %-18s Due: %s  (%d day(s) overdue, Fine: ₹%d)\n",
                    b.getBookId(), b.getTitle(), b.getIssuedTo(),
                    b.getDueDate(), days, days * 5));
        }
        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(Theme.FONT_MONO);
        ta.setBackground(Theme.BG_SURFACE);
        ta.setForeground(Theme.DANGER);
        ta.setEditable(false);
        ta.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(700, 200));
        JOptionPane.showMessageDialog(this, sp,
                "⚠ Overdue Books (" + overdue.size() + ")",
                JOptionPane.WARNING_MESSAGE);
    }

    // ── Helpers ───────────────────────────────────────────────
    private Book selectedBook() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Please select a book from the table."); return null; }
        return tableModel.getBookAt(row);
    }

    private void refresh() {
        tableModel.refresh();
        refreshStats();
    }

    private JPanel darkForm() {
        JPanel p = new JPanel(new java.awt.GridLayout(0, 2, 8, 10));
        p.setBackground(Theme.BG_CARD);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        return p;
    }

    private JLabel darkLabel(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(Theme.TEXT_MUTED);
        l.setFont(Theme.FONT_BODY);
        return l;
    }

    private void styleField(JTextField f) {
        f.setBackground(Theme.BG_SURFACE);
        f.setForeground(Theme.TEXT_PRIMARY);
        f.setCaretColor(Theme.ACCENT);
        f.setFont(Theme.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(4, 8, 4, 8)));
    }

    private int darkDialog(String title, JPanel form) {
        UIManager.put("OptionPane.background", Theme.BG_CARD);
        UIManager.put("Panel.background",      Theme.BG_CARD);
        return JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info",    JOptionPane.INFORMATION_MESSAGE);
    }
    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
