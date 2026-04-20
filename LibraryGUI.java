// ─────────────────────────────────────────────────────────────────────────────
// LibraryGUI.java  (FIXED — custom header renderer forces column text visible)
// OOP Concepts:
//   • INHERITANCE   — extends JFrame
//   • ENCAPSULATION — all UI components are private fields
//   • SINGLE RESPONSIBILITY — initUI(), refreshTable(), refreshStats() separate
//   • EVENT HANDLING — ActionListener via lambda expressions
// ─────────────────────────────────────────────────────────────────────────────

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LibraryGUI extends JFrame {

    // ── Colour palette ───────────────────────────────────────────────────────
    private static final Color CLR_BG        = new Color(245, 247, 250);
    private static final Color CLR_STATS_BG  = new Color(244, 246, 249);
    private static final Color CLR_CARD_BG   = Color.WHITE;
    private static final Color CLR_GREEN     = new Color(46,  125,  50);
    private static final Color CLR_RED       = new Color(198,  40,  40);
    private static final Color CLR_BLUE      = new Color(21,  101, 192);
    private static final Color CLR_GRAY      = new Color(97,  97,  97);
    private static final Color CLR_VAL_BLUE  = new Color(25, 118, 210);
    private static final Color CLR_VAL_GREEN = new Color(39, 174,  96);
    private static final Color CLR_ROW_ALT   = new Color(232, 240, 254);
    private static final Color CLR_HDR_BG    = new Color(21,  101, 192);
    private static final Font  FONT_MAIN     = new Font("Segoe UI", Font.PLAIN,  13);
    private static final Font  FONT_BOLD     = new Font("Segoe UI", Font.BOLD,   13);
    private static final Font  FONT_TITLE    = new Font("Segoe UI", Font.BOLD,   15);
    private static final Font  FONT_SMALL    = new Font("Segoe UI", Font.PLAIN,  12);

    // ── Core library ─────────────────────────────────────────────────────────
    private final Library library = new Library();

    // ── Input form ───────────────────────────────────────────────────────────
    private JTextField tfTitle, tfAuthor, tfYear, tfIsbn;

    // ── Table ────────────────────────────────────────────────────────────────
    private JTable            bookTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // ── Search ───────────────────────────────────────────────────────────────
    private JTextField    tfSearch;
    private JComboBox<String> cbSearchBy;

    // ── Status bar ───────────────────────────────────────────────────────────
    private JLabel lblStatus;

    // ── Stats labels ─────────────────────────────────────────────────────────
    private JLabel statTotalBooks, statUniqueAuthors, statNewest,
                   statOldest, statCommonYear, statLastAdded,
                   statLastDeleted, statLastSearch;

    // ════════════════════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        applyUIDefaults();
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════════════════════════════
    public LibraryGUI() {
        super("Library Management System");
        initUI();
        library.loadFromFile();
        refreshTable(library.getAllBooks());
        refreshStats();
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                library.saveToFile();
                dispose();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  initUI()
    // ════════════════════════════════════════════════════════════════════════
    private void initUI() {
        setSize(1150, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(CLR_BG);
        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildCentrePanel(), BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);
    }

    // ────────────────────────────────────────────────────────────────────────
    //  TOP PANEL
    // ────────────────────────────────────────────────────────────────────────
    private JPanel buildTopPanel() {
        JPanel outer = new JPanel(new BorderLayout(6, 8));
        outer.setBackground(CLR_BG);
        outer.setBorder(new EmptyBorder(10, 12, 6, 12));

        JLabel header = new JLabel("Library Management System", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 17));
        header.setForeground(CLR_BLUE);
        outer.add(header, BorderLayout.NORTH);

        // 4 labeled fields
        JPanel fieldsRow = new JPanel(new GridLayout(1, 4, 10, 0));
        fieldsRow.setBackground(CLR_BG);
        tfTitle  = styledField("Book Title");
        tfAuthor = styledField("Author Name");
        tfYear   = styledField("Publish Year");
        tfIsbn   = styledField("ISBN");
        fieldsRow.add(labeledField("Title",        tfTitle));
        fieldsRow.add(labeledField("Author",       tfAuthor));
        fieldsRow.add(labeledField("Publish Year", tfYear));
        fieldsRow.add(labeledField("ISBN",         tfIsbn));

        // 3 buttons
        JButton btnAdd    = styledButton("Add Book",        CLR_GREEN);
        JButton btnDelete = styledButton("Delete by Title", CLR_RED);
        JButton btnClear  = styledButton("Clear Fields",    CLR_GRAY);
        btnAdd.addActionListener(e    -> onAddBook());
        btnDelete.addActionListener(e -> onDeleteBook());
        btnClear.addActionListener(e  -> clearFields());

        JPanel buttonsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonsRow.setBackground(CLR_BG);
        buttonsRow.add(btnAdd);
        buttonsRow.add(btnDelete);
        buttonsRow.add(btnClear);

        JPanel formPanel = new JPanel(new BorderLayout(0, 8));
        formPanel.setBackground(CLR_BG);
        formPanel.add(fieldsRow,  BorderLayout.CENTER);
        formPanel.add(buttonsRow, BorderLayout.SOUTH);

        outer.add(formPanel, BorderLayout.CENTER);
        return outer;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  CENTRE PANEL
    // ────────────────────────────────────────────────────────────────────────
    private JPanel buildCentrePanel() {
        JPanel centre = new JPanel(new BorderLayout(8, 8));
        centre.setBackground(CLR_BG);
        centre.setBorder(new EmptyBorder(0, 12, 0, 12));

        JPanel left = new JPanel(new BorderLayout(6, 6));
        left.setBackground(CLR_BG);
        left.add(buildTablePanel(),  BorderLayout.CENTER);
        left.add(buildSearchPanel(), BorderLayout.SOUTH);

        centre.add(left,              BorderLayout.CENTER);
        centre.add(buildStatsPanel(), BorderLayout.EAST);
        return centre;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  BOOK TABLE — with custom header renderer (THE CORE FIX)
    // ────────────────────────────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        String[] cols = {"#", "Title", "Author", "Publish Year", "ISBN"};

        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 3 ? Integer.class : String.class;
            }
        };

        bookTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? Color.WHITE : CLR_ROW_ALT);
                return c;
            }
        };

        bookTable.setFont(FONT_MAIN);
        bookTable.setRowHeight(28);
        bookTable.setShowGrid(false);
        bookTable.setIntercellSpacing(new Dimension(0, 0));
        bookTable.getTableHeader().setReorderingAllowed(false);
        bookTable.setSelectionBackground(new Color(187, 222, 251));
        bookTable.setSelectionForeground(Color.BLACK);

        // ── THE FIX ──────────────────────────────────────────────────────────
        // Windows/macOS System L&F overrides JTableHeader.setBackground() and
        // setForeground(), so the white text becomes invisible on a white header.
        // Solution: supply a per-column DefaultTableCellRenderer that paints
        // itself directly with setOpaque(true) — this bypasses the L&F entirely.
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                setText(value == null ? "" : value.toString());
                setOpaque(true);                          // force own background
                setBackground(CLR_HDR_BG);               // blue
                setForeground(Color.WHITE);               // white text
                setFont(FONT_BOLD);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(12, 72, 160)),
                    new EmptyBorder(6, 10, 6, 10)
                ));
                return this;
            }
        };

        for (int i = 0; i < cols.length; i++)
            bookTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);

        // Column widths
        int[] widths = {40, 210, 165, 115, 150};
        for (int i = 0; i < widths.length; i++)
            bookTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Click-to-sort
        rowSorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(rowSorter);

        // Row click auto-fills form
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
                int m = bookTable.convertRowIndexToModel(bookTable.getSelectedRow());
                tfTitle.setText ((String) tableModel.getValueAt(m, 1));
                tfAuthor.setText((String) tableModel.getValueAt(m, 2));
                tfYear.setText  (String.valueOf(tableModel.getValueAt(m, 3)));
                tfIsbn.setText  ((String) tableModel.getValueAt(m, 4));
            }
        });

        JScrollPane sp = new JScrollPane(bookTable);
        sp.setBorder(new LineBorder(new Color(200, 210, 230), 1, true));
        return sp;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  SEARCH PANEL
    // ────────────────────────────────────────────────────────────────────────
    private JPanel buildSearchPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setBackground(CLR_BG);

        tfSearch = styledField("Search keyword...");
        tfSearch.setPreferredSize(new Dimension(240, 32));

        cbSearchBy = new JComboBox<>(new String[]{"Title", "Author", "Publish Year"});
        cbSearchBy.setFont(FONT_MAIN);
        cbSearchBy.setPreferredSize(new Dimension(140, 32));

        JButton btnSearch = styledButton("Search",   CLR_BLUE);
        JButton btnReset  = styledButton("Show All", CLR_GRAY);
        btnSearch.addActionListener(e -> onSearch());
        btnReset.addActionListener(e  -> onReset());
        tfSearch.addActionListener(e  -> onSearch());

        JLabel lbl = new JLabel("Search by:");
        lbl.setFont(FONT_BOLD);

        p.add(lbl); p.add(cbSearchBy); p.add(tfSearch);
        p.add(btnSearch); p.add(btnReset);
        return p;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  STATS SIDEBAR
    // ────────────────────────────────────────────────────────────────────────
    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CLR_STATS_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 210, 230), 1, true),
            new EmptyBorder(8, 8, 8, 8)
        ));
        panel.setPreferredSize(new Dimension(235, 0));

        JLabel heading = new JLabel("Live Statistics");
        heading.setFont(FONT_TITLE);
        heading.setForeground(CLR_BLUE);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(8));

        statTotalBooks    = addStatCard(panel, "Total Books",        "0", CLR_VAL_BLUE);
        statUniqueAuthors = addStatCard(panel, "Unique Authors",     "0", CLR_VAL_BLUE);
        statNewest        = addStatCard(panel, "Newest Book",        "—", CLR_VAL_GREEN);
        statOldest        = addStatCard(panel, "Oldest Book",        "—", CLR_VAL_GREEN);
        statCommonYear    = addStatCard(panel, "Most Common Year",   "—", CLR_VAL_BLUE);
        statLastAdded     = addStatCard(panel, "Last Added",         "—", CLR_VAL_GREEN);
        statLastDeleted   = addStatCard(panel, "Last Deleted",       "—", CLR_RED);
        statLastSearch    = addStatCard(panel, "Last Search Query",  "—", CLR_VAL_BLUE);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JLabel addStatCard(JPanel parent, String name, String initial, Color vc) {
        JPanel card = new JPanel(new BorderLayout(4, 2));
        card.setBackground(CLR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 228, 240), 1, true),
            new EmptyBorder(6, 8, 6, 8)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblName  = new JLabel(name);
        lblName.setFont(FONT_BOLD);
        lblName.setForeground(new Color(60, 70, 85));

        JLabel lblValue = new JLabel(initial);
        lblValue.setFont(FONT_SMALL);
        lblValue.setForeground(vc);

        card.add(lblName,  BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        parent.add(card);
        parent.add(Box.createVerticalStrut(5));
        return lblValue;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  STATUS BAR
    // ────────────────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(235, 238, 245));
        bar.setBorder(new EmptyBorder(4, 14, 6, 14));
        lblStatus = new JLabel("Ready — load or add books to get started.");
        lblStatus.setFont(FONT_SMALL);
        bar.add(lblStatus, BorderLayout.WEST);
        return bar;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  refreshTable()
    // ════════════════════════════════════════════════════════════════════════
    private void refreshTable(List<Book> source) {
        tableModel.setRowCount(0);
        int idx = 1;
        for (Book b : source)
            tableModel.addRow(new Object[]{idx++, b.getTitle(), b.getAuthor(),
                                           b.getPublishYear(), b.getIsbn()});
    }

    // ════════════════════════════════════════════════════════════════════════
    //  refreshStats()
    // ════════════════════════════════════════════════════════════════════════
    private void refreshStats() {
        statTotalBooks.setText(String.valueOf(library.getTotalBooks()));
        statUniqueAuthors.setText(String.valueOf(library.getUniqueAuthorCount()));
        library.getNewestBook().ifPresentOrElse(
            b -> statNewest.setText(truncate(b.getTitle(), 22) + " (" + b.getPublishYear() + ")"),
            () -> statNewest.setText("—"));
        library.getOldestBook().ifPresentOrElse(
            b -> statOldest.setText(truncate(b.getTitle(), 22) + " (" + b.getPublishYear() + ")"),
            () -> statOldest.setText("—"));
        statCommonYear.setText(library.getMostCommonYear());
        statLastAdded.setText(truncate(library.getLastAddedTitle(),   24));
        statLastDeleted.setText(truncate(library.getLastDeletedTitle(), 24));
        statLastSearch.setText(truncate(library.getLastSearchQuery(),  24));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ACTION HANDLERS
    // ════════════════════════════════════════════════════════════════════════
    private void onAddBook() {
        String tv = tfTitle.getText().trim(), av = tfAuthor.getText().trim(),
               iv = tfIsbn.getText().trim(),  yv = tfYear.getText().trim();
        if (tv.isEmpty() || av.isEmpty() || iv.isEmpty() || yv.isEmpty()) {
            setStatus("All fields are required.", CLR_RED); return;
        }
        int year;
        try { year = Integer.parseInt(yv); }
        catch (NumberFormatException ex) { setStatus("Publish Year must be a number.", CLR_RED); return; }
        try {
            library.addBook(new Book(tv, av, year, iv));
            refreshTable(library.getAllBooks()); refreshStats(); clearFields();
            setStatus("Book added successfully: \"" + tv + "\"", CLR_GREEN);
        } catch (IllegalArgumentException ex) { setStatus(ex.getMessage(), CLR_RED); }
    }

    private void onDeleteBook() {
        String tv = tfTitle.getText().trim();
        if (tv.isEmpty()) { setStatus("Enter a title to delete.", CLR_RED); return; }
        try {
            library.deleteBookByTitle(tv);
            refreshTable(library.getAllBooks()); refreshStats(); clearFields();
            setStatus("Book deleted: \"" + tv + "\"", CLR_GREEN);
        } catch (BookNotFoundException ex) { setStatus(ex.getMessage(), CLR_RED); }
    }

    private void onSearch() {
        String kw = tfSearch.getText().trim();
        if (kw.isEmpty()) { setStatus("Enter a search keyword.", CLR_RED); return; }
        String type = (String) cbSearchBy.getSelectedItem();
        List<Book> results;
        try {
            results = switch (type) {
                case "Author"       -> library.searchByAuthor(kw);
                case "Publish Year" -> library.searchByPublishYear(Integer.parseInt(kw));
                default             -> library.searchByTitle(kw);
            };
        } catch (NumberFormatException ex) { setStatus("Publish Year must be a number.", CLR_RED); return; }
        refreshTable(results); refreshStats();
        if (results.isEmpty())
            setStatus("No results for \"" + kw + "\" [" + type + "].", CLR_RED);
        else
            setStatus(results.size() + " result(s) found for \"" + kw + "\" [" + type + "].", CLR_BLUE);
    }

    private void onReset() {
        refreshTable(library.getAllBooks()); refreshStats();
        tfSearch.setText("");
        setStatus("Showing all " + library.getTotalBooks() + " book(s).", CLR_BLUE);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UTILITIES
    // ════════════════════════════════════════════════════════════════════════
    private void clearFields() {
        tfTitle.setText(""); tfAuthor.setText(""); tfYear.setText(""); tfIsbn.setText("");
        bookTable.clearSelection();
    }

    private void setStatus(String msg, Color color) {
        lblStatus.setText(msg); lblStatus.setForeground(color);
    }

    private String truncate(String s, int max) {
        if (s == null || s.length() <= max) return s == null ? "—" : s;
        return s.substring(0, max - 1) + "…";
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(FONT_MAIN);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(180, 195, 215), 1, true),
            new EmptyBorder(5, 8, 5, 8)));
        f.setToolTipText(placeholder);
        return f;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(2, 3));
        p.setBackground(CLR_BG);
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(new Color(50, 60, 80));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.addMouseListener(new MouseAdapter() {
            final Color orig = bg;
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(orig.darker()); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(orig); }
        });
        return btn;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  GLOBAL UI DEFAULTS
    // ════════════════════════════════════════════════════════════════════════
    private static void applyUIDefaults() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        UIManager.put("Panel.background",  CLR_BG);
        UIManager.put("Label.font",        FONT_MAIN);
        UIManager.put("TextField.font",    FONT_MAIN);
        UIManager.put("ComboBox.font",     FONT_MAIN);
        UIManager.put("Button.font",       FONT_BOLD);
        UIManager.put("Table.font",        FONT_MAIN);
        UIManager.put("TableHeader.font",  FONT_BOLD);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
    }
}
