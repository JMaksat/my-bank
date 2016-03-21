import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

public class AccountTransactions implements TableModelListener {
    DBClass dbc;

    JDialog dlg;
    JTable transactionTable;
    TransactionTableModel transactionTableModel;
    JPanel panel;
    //JButton buttonReverse;
    // JButton buttonAddTransaction;

    int horizTop = 0;
    int vertTop = 0;
    int column = -1;
    int row = -1;
    int accountID;
    int customerID;

    private final static String TITLE = "Account transactions";

    public AccountTransactions(DBClass dbc, int accountID, int customerID) {
        this.dbc = dbc;
        this.accountID = accountID;
        this.customerID = customerID;

        dlg = new JDialog();
        dlg.setLayout(null);
        dlg.setTitle(TITLE);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setSize(750, 480);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(null);
        dlg.setModal(true);

        Dimension dim = new Dimension(730, 440);
        transactionTableModel = new TransactionTableModel(dbc, accountID);
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setPreferredScrollableViewportSize(dim);
        transactionTable.setFillsViewportHeight(true);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setCellSelectionEnabled(false);
        transactionTable.setColumnSelectionAllowed(false);
        transactionTable.setRowSelectionAllowed(true);
        transactionTable.getSelectionModel().addListSelectionListener(new RowListener());

        initColumnSizes();

        panel = new JPanel();
        panel.setBounds(5, 5, (int) dim.getWidth() + 5, (int) dim.getHeight() + 5);


        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane);
        dlg.add(panel);

        transactionTableModel.addTableModelListener(this);

        dlg.setVisible(true);
    }

    public void tableChanged(TableModelEvent e) {
        row = e.getFirstRow();
        column = e.getColumn();
        TransactionTableModel model = (TransactionTableModel)e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);
    }


    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            column = transactionTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
            row = transactionTable.getSelectionModel().getLeadSelectionIndex();
            TransactionTableModel model = (TransactionTableModel) transactionTable.getModel();
            String columnName = model.getColumnName(column);
            Object data = model.getValueAt(row, column);
        }
    }

    private void initColumnSizes() {
        TableColumn column = null;
        column = transactionTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(55);
        column = transactionTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(90);
        column = transactionTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(55);
        column = transactionTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(80);
        column = transactionTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(80);
        column = transactionTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(70);
        column = transactionTable.getColumnModel().getColumn(6);
        column.setPreferredWidth(150);
        column = transactionTable.getColumnModel().getColumn(7);
        column.setPreferredWidth(150);
    }


    class TransactionTableModel extends AbstractTableModel {
        DBClass dbc;
        ResultSet rs = null;

        Vector rows;
        Vector allRows;
        Vector<String> headers;
        int colCount;
        int allCols;

        TransactionTableModel(DBClass dbc, int accountID) {
            this.dbc = dbc;
            rows = new Vector();
            allRows = new Vector();
            headers = new Vector();

            headers.addElement("Number");
            headers.addElement("Type");
            headers.addElement("Reverse");
            headers.addElement("Amount");
            headers.addElement("Date");
            headers.addElement("Time");
            headers.addElement("Account debit");
            headers.addElement("Account credit");

            colCount = headers.size();

            rs = dbc.getAccountTransactions(accountID);
            try {
                ResultSetMetaData meta = rs.getMetaData();
                allCols = meta.getColumnCount();

                while (rs.next()) {
                    Vector<String> allRec = new Vector<String>();
                    for (int i = 0; i < allCols; i++) {
                        allRec.addElement(rs.getString(i + 1));
                    }

                    allRows.addElement(allRec);

                    Vector record = new Vector();
                    record.addElement(rs.getString(1));
                    record.addElement(rs.getString(2));
                    record.addElement(rs.getBoolean(3));
                    record.addElement(rs.getDouble(4));
                    record.addElement(rs.getDate(5));
                    record.addElement(rs.getString(6));
                    record.addElement(rs.getString(7));
                    record.addElement(rs.getString(8));
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
            return headers.get(i);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return ((Vector)rows.get(rowIndex)).get(columnIndex);
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 4) {
                return true;
            }
            return false;
        }

        public void setValueAt(Object value, int row, int col) {
            ((Vector)rows.get(row)).set(col, value);
            fireTableCellUpdated(row, col);
        }

    }
}