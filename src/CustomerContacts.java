import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

/**
 * Created by Android on 18/03/2016.
 */
public class CustomerContacts {
    DBClass dbc;

    JDialog dlg;
    JTable contactsTable;
    JTable addressTable;
    ContactsTableModel contactsTableModel;
    AddressTableModel addressTableModel;
    JPanel panelContacts;
    JPanel panelAddress;
    JLabel labelContacts;
    JLabel labelAddress;

    int horizTop = 0;
    int vertTop = 0;
    int column = -1;
    int row = -1;
    int customerID;

    private final static String TITLE = "Customer contacts";

    public CustomerContacts(DBClass dbc, int customerID) {
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

        labelContacts = new JLabel("Contacts");
        labelContacts.setBounds(5, 5, 100, 20);
        dlg.add(labelContacts);

        Dimension dimContacts = new Dimension(620, 150);
        contactsTableModel = new ContactsTableModel(dbc, customerID);
        contactsTable = new JTable(contactsTableModel);
        contactsTable.setPreferredScrollableViewportSize(dimContacts);
        contactsTable.setFillsViewportHeight(true);
        contactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsTable.setCellSelectionEnabled(false);
        contactsTable.setColumnSelectionAllowed(false);
        contactsTable.setRowSelectionAllowed(true);
        //contactsTable.getSelectionModel().addListSelectionListener(new RowListener());

        initColumnSizes(contactsTable);

        panelContacts = new JPanel();
        panelContacts.setBounds(5, 25, (int) dimContacts.getWidth() + 5, (int) dimContacts.getHeight() + 5);

        JScrollPane scrollContacts = new JScrollPane(contactsTable);
        panelContacts.add(scrollContacts);
        dlg.add(panelContacts);

        labelAddress = new JLabel("Address:");
        labelAddress.setBounds(5, 205, 100, 20);
        dlg.add(labelAddress);

        Dimension dimAddress = new Dimension(620, 150);
        addressTableModel = new AddressTableModel(dbc, customerID);
        addressTable = new JTable(addressTableModel);
        addressTable.setPreferredScrollableViewportSize(dimAddress);
        addressTable.setFillsViewportHeight(true);
        addressTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addressTable.setCellSelectionEnabled(false);
        addressTable.setColumnSelectionAllowed(false);
        addressTable.setRowSelectionAllowed(true);
        //addressTable.getSelectionModel().addListSelectionListener(new RowListener());

        initColumnSizes(addressTable);

        panelAddress = new JPanel();
        panelAddress.setBounds(5, 225, (int) dimAddress.getWidth() + 5, (int) dimAddress.getHeight() + 5);

        JScrollPane scrollAddress = new JScrollPane(addressTable);
        panelAddress.add(scrollAddress);
        dlg.add(panelAddress);

        dlg.setVisible(true);
    }


    private void initColumnSizes(JTable table) {
        TableColumn column = null;
        column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(30);
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(120);
        column = table.getColumnModel().getColumn(2);
        column.setPreferredWidth(70);
    }


    class ContactsTableModel extends AbstractTableModel {
        DBClass dbc;
        ResultSet rs = null;

        Vector rows;
        Vector allRows;
        Vector<String> headers;
        int colCount;
        int allCols;

        ContactsTableModel(DBClass dbc, int customerID) {
            this.dbc = dbc;
            rows = new Vector();
            allRows = new Vector();
            headers = new Vector();

            headers.addElement("Number");
            headers.addElement("Type");
            headers.addElement("Contact");

            colCount = headers.size();

            rs = dbc.getContacts(customerID);
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
                    record.addElement(rs.getInt(1));
                    record.addElement(rs.getString(2));
                    record.addElement(rs.getString(3));

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

    class AddressTableModel extends AbstractTableModel {
        DBClass dbc;
        ResultSet rs = null;

        Vector rows;
        Vector allRows;
        Vector<String> headers;
        int colCount;
        int allCols;

        AddressTableModel(DBClass dbc, int customerID) {
            this.dbc = dbc;
            rows = new Vector();
            allRows = new Vector();
            headers = new Vector();

            headers.addElement("Number");
            headers.addElement("Type");
            headers.addElement("Contact");

            colCount = headers.size();

            rs = dbc.getAddress(customerID);
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
                    record.addElement(rs.getInt(1));
                    record.addElement(rs.getString(2));
                    record.addElement(rs.getString(3));
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