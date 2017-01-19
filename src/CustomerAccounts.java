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

/**
 * Created by Maxwell on 08/01/2016.
 */
public class CustomerAccounts implements TableModelListener {
    private final static String TITLE = "Customer accounts";
    DBClass dbc;
    JDialog dlg;
    JTable accountTable;
    AccountTableModel accountTableModel;
    JPanel panel;
    JButton buttonAddAccount;
    JButton buttonTransactions;
    int horizTop = 0;
    int vertTop = 0;
    int column = -1;
    int row = -1;
    int customerID;

    public CustomerAccounts(DBClass dbc, int customerID) {
        this.dbc = dbc;
        this.customerID = customerID;

        dlg = new JDialog();
        dlg.setLayout(null);
        dlg.setTitle(TITLE);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setSize(640, 480);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(null);
        dlg.setModal(true);

        Dimension dim = new Dimension(620, 400);
        accountTableModel = new AccountTableModel(dbc, customerID);
        accountTable = new JTable(accountTableModel);
        accountTable.setPreferredScrollableViewportSize(dim);
        accountTable.setFillsViewportHeight(true);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.setCellSelectionEnabled(false);
        accountTable.setColumnSelectionAllowed(false);
        accountTable.setRowSelectionAllowed(true);
        accountTable.getSelectionModel().addListSelectionListener(new RowListener());

        initColumnSizes();

        panel = new JPanel();
        panel.setBounds(5, 5, (int) dim.getWidth() + 5, (int) dim.getHeight() + 5);


        JScrollPane scrollPane = new JScrollPane(accountTable);
        panel.add(scrollPane);
        dlg.add(panel);

        accountTableModel.addTableModelListener(this);

        buttonAddAccount = new JButton("Add account");
        buttonAddAccount.setEnabled(true);
        buttonAddAccount.setBounds(horizTop + 370, vertTop + 420, 120, 22);
        buttonAddAccount.addActionListener(event -> addAccount());
        dlg.add(buttonAddAccount);

        buttonTransactions = new JButton("Transactions");
        buttonTransactions.setEnabled(true);
        buttonTransactions.setBounds(horizTop + 500, vertTop + 420, 120, 22);
        buttonTransactions.addActionListener(event -> transactionList(accountTableModel));
        dlg.add(buttonTransactions);

        dlg.setVisible(true);
    }


    private void addAccount() {
        NewAccount na = new NewAccount(dbc, customerID);
        accountTableModel = new AccountTableModel(dbc, customerID);
        accountTable.setModel(accountTableModel);
        initColumnSizes();
    }

    public void tableChanged(TableModelEvent e) {
        row = e.getFirstRow();
        column = e.getColumn();
        AccountTableModel model = (AccountTableModel) e.getSource();
        //columnName = model.getColumnName(column);
        //Object data = model.getValueAt(row, column);

        dbc.updateAccountInfo((int) model.getValueAt(row, 0),
                (boolean) model.getValueAt(row, 4));
    }

    private void initColumnSizes() {
        TableColumn column = null;
        column = accountTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(30);
        column = accountTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(120);
        column = accountTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(70);
        column = accountTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(70);
        column = accountTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(60);
        column = accountTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(80);
    }

    private void transactionList(AccountTableModel model) {
        if (accountTable.isRowSelected(row)) {
            AccountTransactions at = new AccountTransactions(dbc, (int) model.getValueAt(row, 0));
        }
    }

    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            column = accountTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
            row = accountTable.getSelectionModel().getLeadSelectionIndex();
            //AccountTableModel model = (AccountTableModel) accountTable.getModel();
            //String columnName = model.getColumnName(column);
            //Object data = model.getValueAt(row, column);
        }
    }

    class AccountTableModel extends AbstractTableModel {
        DBClass dbc;
        ResultSet rs = null;

        Vector rows;
        Vector allRows;
        Vector<String> headers;
        int colCount;
        int allCols;

        AccountTableModel(DBClass dbc, int customerID) {
            this.dbc = dbc;
            rows = new Vector();
            allRows = new Vector();
            headers = new Vector();

            headers.addElement("Number");
            headers.addElement("Account");
            headers.addElement("Opened");
            headers.addElement("Type");
            headers.addElement("Suspended");
            headers.addElement("Rest");

            colCount = headers.size();

            rs = dbc.getCustomerAccounts(customerID);
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
                    record.addElement(rs.getDate(4));
                    record.addElement(rs.getString(9));
                    record.addElement(rs.getBoolean(10));
                    record.addElement(rs.getDouble(11));

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
            return ((Vector) rows.get(rowIndex)).get(columnIndex);
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
            ((Vector) rows.get(row)).set(col, value);
            fireTableCellUpdated(row, col);
        }

    }
}
