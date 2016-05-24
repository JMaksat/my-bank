import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * Created by Maxwell on 10/01/2016.
 */
public class NewAccount {

    private final static String TITLE = "Adding new account";
    DBClass dbc;
    JDialog dlg;
    JLabel labelAccount;
    JLabel labelType;
    JLabel labelDateOpen;
    JTextField textAccount;
    JComboBox comboType;
    JButton buttonOk;
    JButton buttonCancel;
    UtilDateModel model;
    Properties p;
    JDatePanelImpl datePanel;
    JDatePickerImpl datePicker;
    int customerID;
    int editLength = 130;
    int editOffset = 120;
    int labelLength = 110;

    public NewAccount(DBClass dbc, int customerID) {
        this.dbc = dbc;
        this.customerID = customerID;

        dlg = new JDialog();
        dlg.setTitle(TITLE);
        dlg.setLayout(null);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setSize(270, 220);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(null);
        dlg.setModal(true);

        labelAccount = new JLabel("Account number");
        labelAccount.setEnabled(true);
        labelAccount.setBounds(20, 20, labelLength, 20);
        dlg.add(labelAccount);

        labelType = new JLabel("Account type");
        labelType.setEnabled(true);
        labelType.setBounds(20, 50, labelLength, 20);
        dlg.add(labelType);

        labelDateOpen = new JLabel("Opening date");
        labelDateOpen.setEnabled(true);
        labelDateOpen.setBounds(20, 80, labelLength, 20);
        dlg.add(labelDateOpen);

        textAccount = new JTextField();
        textAccount.setEnabled(true);
        textAccount.setBounds(editOffset, 20, editLength, 22);
        dlg.add(textAccount);

        comboType = new JComboBox(dbc.getAccountTypes());
        comboType.setEnabled(true);
        comboType.setBounds(editOffset, 50, editLength, 22);
        comboType.setSelectedItem(new Item(16, "PERSONAL"));
        dlg.add(comboType);

        model = new UtilDateModel();
        p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setBounds(editOffset, 80, editLength, 26);
        dlg.add(datePicker);

        buttonOk = new JButton("Save");
        buttonOk.setEnabled(true);
        buttonOk.setBounds(40, 150, 80, 22);
        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkFields()) {
                    insertData();
                    formDispose();
                }
            }
        });
        dlg.add(buttonOk);

        buttonCancel = new JButton("Cancel");
        buttonCancel.setEnabled(true);
        buttonCancel.setBounds(140, 150, 80, 22);
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formDispose();
            }
        });
        dlg.add(buttonCancel);

        dlg.setVisible(true);
    }

    private void insertData() {
        Item item = (Item) comboType.getSelectedItem();

        dbc.insertAccountInfo(textAccount.getText(),
                customerID,
                new GregorianCalendar(datePicker.getModel().getYear(), datePicker.getModel().getMonth(), datePicker.getModel().getDay()),
                item.getId());
    }

    private void formDispose() {
        dlg.dispose();
    }

    private Boolean checkFields() {
        if (textAccount.getText().trim().length() == 0) {
            return false;
        }
        return true;
    }
}
