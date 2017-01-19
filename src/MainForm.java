import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Enumeration;
import java.util.Vector;

public class MainForm implements TableModelListener {

    private final static String TITLE = "MyBank - Customer list";
    JFrame mainFrm;
    MainTableModel mainTableModel;
    JTable mainTable;
    JButton buttonAdd;
    JButton buttonExit;
    JButton buttonBlock;
    JButton buttonAccounts;
    JButton buttonTransaction;
    JButton buttonContacts;
    JPanel panel;
    DBClass dbc;
    int horizTop = 0;
    int vertTop = -20;
    int column = -1;
    int row = -1;
    Boolean isBlocked = null;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem menuItem;
    private JMenuItem aboutItem;

    public MainForm(DBClass dbc) {
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        mainFrm = new JFrame();
        mainFrm.setTitle(TITLE);
        mainFrm.setLayout(null);

        mainFrm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrm.setSize(800, 600);
        mainFrm.setResizable(false);
        mainFrm.setLocationRelativeTo(null);
        mainFrm.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            {
                exitPrompt();
            }
        });

        this.dbc = dbc;

        initMenu();

        Dimension dim = new Dimension(780, 500);

        initTable(dim);

        initColumnSizes();

        panel = new JPanel();
        panel.setBounds(5, 5, (int) dim.getWidth() + 5, (int) dim.getHeight() + 5);

        JScrollPane scrollPane = new JScrollPane(mainTable);
        panel.add(scrollPane);
        mainFrm.add(panel);

        initButtons();

        mainFrm.setVisible(true);
    }

    public void tableChanged(TableModelEvent e) {
        row = e.getFirstRow();
        column = e.getColumn();
        MainTableModel model = (MainTableModel) e.getSource();
        //String columnName = model.getColumnName(column);
        //Object data = model.getValueAt(row, column);

        dbc.updateCustomerInfo((int) model.getValueAt(row, 0),
                (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 2),
                (String) model.getValueAt(row, 3),
                (java.sql.Date) model.getValueAt(row, 4),
                (String) model.getValueAt(row, 5),
                (Boolean) model.getValueAt(row, 6));
    }

    private void checkBlocked(MainTableModel model) {
        if (mainTable.isRowSelected(row)) {
            if (((Vector) model.allRows.get(row)).get(8).equals("f")) {
                buttonBlock.setText("Unblock client");
                isBlocked = true;
            } else {
                buttonBlock.setText("Block client");
                isBlocked = false;
            }
        }
    }

    private void unblockCustomer(MainTableModel model) {
        if (mainTable.isRowSelected(row)) {
            if (isBlocked) {
                dbc.unblockCustomer((int) model.getValueAt(row, 0), true);
                ((Vector) model.allRows.get(row)).set(8, "t");
            } else {
                dbc.unblockCustomer((int) model.getValueAt(row, 0), false);
                ((Vector) model.allRows.get(row)).set(8, "f");
            }
            model.fireTableCellUpdated(row, column);
        }
    }

    private void addCustomer() {
        NewCustomer nc = new NewCustomer(dbc);
        mainTableModel = new MainTableModel(dbc);
        mainTable.setModel(mainTableModel);
        initColumnSizes();
    }

    private void addTransaction() {
        NewTransaction na = new NewTransaction(dbc);
        mainTableModel = new MainTableModel(dbc);
        mainTable.setModel(mainTableModel);
        initColumnSizes();
    }

    private void contactList(MainTableModel model) {
        if (mainTable.isRowSelected(row)) {
            CustomerContacts cc = new CustomerContacts(dbc, (int) model.getValueAt(row, 0));
            mainTableModel = new MainTableModel(dbc);
            mainTable.setModel(mainTableModel);
            initColumnSizes();
        }
    }

    private void accountsList(MainTableModel model) {
        if (mainTable.isRowSelected(row)) {
            CustomerAccounts ca = new CustomerAccounts(dbc, (int) model.getValueAt(row, 0));
        }

    }

    private void initMenu() {
        menuBar = new JMenuBar();
        mainFrm.setJMenuBar(menuBar);

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        aboutItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutItem.addActionListener(event -> JOptionPane.showMessageDialog(null, "MyBank is demo application which shows " +
                                "how to work with PostgreSQL, JDBC and SWING JTable.\n \n2016. Maksat E.",
                        TITLE,
                        JOptionPane.INFORMATION_MESSAGE)
        );
        fileMenu.add(aboutItem);
        fileMenu.addSeparator();

        menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
        menuItem.addActionListener(event -> exitPrompt());
        fileMenu.add(menuItem);
    }

    private void initButtons() {
        buttonTransaction = new JButton("Transaction");
        buttonTransaction.setEnabled(true);
        buttonTransaction.setBounds(horizTop + 15, vertTop + 540, 120, 22);
        buttonTransaction.addActionListener(event -> addTransaction());
        mainFrm.add(buttonTransaction);

        buttonContacts = new JButton("Contacts");
        buttonContacts.setEnabled(true);
        buttonContacts.setBounds(horizTop + 145, vertTop + 540, 120, 22);
        buttonContacts.addActionListener(event -> contactList(mainTableModel));
        mainFrm.add(buttonContacts);

        buttonAccounts = new JButton("Accounts");
        buttonAccounts.setEnabled(true);
        buttonAccounts.setBounds(horizTop + 275, vertTop + 540, 120, 22);
        buttonAccounts.addActionListener(event -> accountsList(mainTableModel));
        mainFrm.add(buttonAccounts);

        buttonBlock = new JButton();
        buttonBlock.setText("Block client");
        buttonBlock.setBounds(horizTop + 405, vertTop + 540, 120, 22);
        buttonBlock.setEnabled(true);
        buttonBlock.addActionListener(event -> {
            unblockCustomer(mainTableModel);
            checkBlocked(mainTableModel);
        });
        mainFrm.add(buttonBlock);

        buttonAdd = new JButton("New customer");
        buttonAdd.setBounds(horizTop + 535, vertTop + 540, 120, 22);
        buttonAdd.setEnabled(true);
        buttonAdd.addActionListener(event -> addCustomer());
        mainFrm.add(buttonAdd);

        buttonExit = new JButton("Close");
        buttonExit.setBounds(horizTop + 665, vertTop + 540, 120, 22);
        buttonExit.setEnabled(true);
        buttonExit.addActionListener(event -> exitPrompt());
        mainFrm.add(buttonExit);
    }

    private void initColumnSizes() {
        Vector<TableColumn> vt = new Vector<>();
        vt.addElement(mainTable.getColumnModel().getColumn(0));
        vt.addElement(mainTable.getColumnModel().getColumn(1));
        vt.addElement(mainTable.getColumnModel().getColumn(2));
        vt.addElement(mainTable.getColumnModel().getColumn(3));
        vt.addElement(mainTable.getColumnModel().getColumn(4));
        vt.addElement(mainTable.getColumnModel().getColumn(5));
        Enumeration<TableColumn> en = vt.elements();
        while (en.hasMoreElements()) {
            TableColumn tc = en.nextElement();
            tc.setCellRenderer(new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    if (((Vector) mainTableModel.allRows.get(row)).get(8).equals("f")) {
                        label.setForeground(Color.GRAY);
                    } else {
                        label.setForeground(Color.BLACK);
                    }
                    return label;
                }
            });
        }

        TableColumn column = null;
        column = mainTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);
        column = mainTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(140);
        column = mainTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(140);
        column = mainTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(140);
        column = mainTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(80);
        column = mainTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(150);
        column = mainTable.getColumnModel().getColumn(6);
        column.setPreferredWidth(50);
    }

    private void initTable(Dimension dim) {
        mainTableModel = new MainTableModel(dbc);
        mainTableModel.addTableModelListener(this);
        mainTable = new JTable(mainTableModel);
        mainTable.setPreferredScrollableViewportSize(dim);
        mainTable.setFillsViewportHeight(true);
        mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainTable.setCellSelectionEnabled(false);
        mainTable.setColumnSelectionAllowed(false);
        mainTable.setRowSelectionAllowed(true);
        mainTable.getSelectionModel().addListSelectionListener(new RowListener());
    }

    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            column = mainTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
            row = mainTable.getSelectionModel().getLeadSelectionIndex();

            if ((row > -1) && (column > -1)) {
                MainTableModel model = (MainTableModel) mainTable.getModel();
                //String columnName = model.getColumnName(column);
                //Object data = model.getValueAt(row, column);
                checkBlocked(model);
            }
        }
    }

    class MainTableModel extends AbstractTableModel {
        DBClass dbc;
        ResultSet rs = null;

        Vector rows;
        Vector allRows;
        Vector<String> headers;
        int colCount;
        int allCols;

        MainTableModel(DBClass dbc) {
            this.dbc = dbc;
            rows = new Vector();
            allRows = new Vector();
            headers = new Vector();

            headers.addElement("Number");
            headers.addElement("First name");
            headers.addElement("Last name");
            headers.addElement("Middle name");
            headers.addElement("Birth date");
            headers.addElement("Personal number");
            headers.addElement("Resident");

            colCount = headers.size();

            rs = dbc.getCustomerInfo("%");
            try {
                ResultSetMetaData meta = rs.getMetaData();
                allCols = meta.getColumnCount();

                while (rs.next()) {
                    Vector<String> allRec = new Vector<>();
                    for (int i = 0; i < allCols; i++) {
                        allRec.addElement(rs.getString(i + 1));
                    }

                    allRows.addElement(allRec);

                    Vector record = new Vector();
                    record.addElement(rs.getInt(1));
                    record.addElement(rs.getString(2));
                    record.addElement(rs.getString(3));
                    record.addElement(rs.getString(4));
                    record.addElement(rs.getDate(5));
                    record.addElement(rs.getString(6));
                    record.addElement(rs.getBoolean(7));

                    rows.addElement(record);
                }

                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return colCount;
        }

        public String getColumnName(int i) {
            return headers.elementAt(i);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return ((Vector) rows.get(rowIndex)).get(columnIndex);
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }

        public void setValueAt(Object value, int row, int col) {
            ((Vector) rows.get(row)).set(col, value);
            fireTableCellUpdated(row, col);
        }


    }

    private void exitPrompt() {
        String ObjButtons[] = {"Yes","No"};
        int PromptResult = JOptionPane.showOptionDialog(null,
                "Are you sure you want to exit?",
                TITLE,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                ObjButtons,
                ObjButtons[1]);
        if(PromptResult == JOptionPane.YES_OPTION)
        {
            dbc.closeConnection();
            System.exit(0);
        }
    }
}
