import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.GregorianCalendar;
import java.util.Properties;

public class NewCustomer {
    private final static String TITLE = "Adding new customer";
    DBClass dbc;
    JDialog dlg;
    JLabel labelLastname;
    JLabel labelFirstname;
    JLabel labelMiddlename;
    JLabel labelBirthdate;
    JLabel labelPesonalID;
    JLabel labelResident;
    JTextField textLastname;
    JTextField textFirstname;
    JTextField textMiddlename;
    UtilDateModel model;
    Properties p;
    JDatePanelImpl datePanel;
    JDatePickerImpl datePicker;
    JTextField textPersonalID;
    JCheckBox checkResident;
    JButton buttonOk;
    JButton buttonCancel;
    int editLength = 130;
    int editOffset = 120;
    int labelLength = 110;

    public NewCustomer(DBClass dbc) {
        this.dbc = dbc;

        dlg = new JDialog();
        dlg.setTitle(TITLE);
        dlg.setLayout(null);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setSize(270, 280);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(null);
        dlg.setModal(true);

        labelLastname = new JLabel("Last name");
        labelLastname.setEnabled(true);
        labelLastname.setBounds(20, 20, labelLength, 20);
        dlg.add(labelLastname);

        labelFirstname = new JLabel("First name");
        labelFirstname.setEnabled(true);
        labelFirstname.setBounds(20, 50, labelLength, 20);
        dlg.add(labelFirstname);

        labelMiddlename = new JLabel("Middle name");
        labelMiddlename.setEnabled(true);
        labelMiddlename.setBounds(20, 80, labelLength, 20);
        dlg.add(labelMiddlename);

        labelBirthdate = new JLabel("Day of birth");
        labelBirthdate.setEnabled(true);
        labelBirthdate.setBounds(20, 110, labelLength, 20);
        dlg.add(labelBirthdate);

        labelPesonalID = new JLabel("Personal ID");
        labelPesonalID.setEnabled(true);
        labelPesonalID.setBounds(20, 140, labelLength, 20);
        dlg.add(labelPesonalID);

        labelResident = new JLabel("Resident");
        labelResident.setEnabled(true);
        labelResident.setBounds(20, 170, labelLength, 20);
        dlg.add(labelResident);

        textLastname = new JTextField();
        textLastname.setEnabled(true);
        textLastname.setBounds(editOffset, 20, editLength, 22);
        dlg.add(textLastname);

        textFirstname = new JTextField();
        textFirstname.setEnabled(true);
        textFirstname.setBounds(editOffset, 50, editLength, 22);
        dlg.add(textFirstname);

        textMiddlename = new JTextField();
        textMiddlename.setEnabled(true);
        textMiddlename.setBounds(editOffset, 80, editLength, 22);
        dlg.add(textMiddlename);

        model = new UtilDateModel();
        p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setBounds(editOffset, 107, editLength, 26);
        dlg.add(datePicker);

        textPersonalID = new JTextField();
        textPersonalID.setEnabled(true);
        textPersonalID.setBounds(editOffset, 140, editLength, 22);
        dlg.add(textPersonalID);

        checkResident = new JCheckBox();
        checkResident.setEnabled(true);
        checkResident.setBounds(editOffset, 170, editLength, 22);
        dlg.add(checkResident);

        buttonOk = new JButton("Save");
        buttonOk.setEnabled(true);
        buttonOk.setBounds(40, 210, 80, 22);
        buttonOk.addActionListener(event -> {
            if (checkFields()) {
                insertData();
                formDispose();
            }
        });
        dlg.add(buttonOk);

        buttonCancel = new JButton("Cancel");
        buttonCancel.setEnabled(true);
        buttonCancel.setBounds(140, 210, 80, 22);
        buttonCancel.addActionListener(event -> formDispose());
        dlg.add(buttonCancel);

        dlg.setVisible(true);
    }

    private void insertData() {
        dbc.insertCustomerInfo(textLastname.getText(),
                textFirstname.getText(),
                textMiddlename.getText(),
                new GregorianCalendar(datePicker.getModel().getYear(), datePicker.getModel().getMonth(), datePicker.getModel().getDay()),
                textPersonalID.getText(),
                checkResident.isSelected());
    }

    private void formDispose() {
        dlg.dispose();
    }

    private Boolean checkFields() {
        if ((textLastname.getText().trim().length() == 0) ||
                (textFirstname.getText().trim().length() == 0) ||
                (textMiddlename.getText().trim().length() == 0)) {
            return false;
        }
        return true;
    }
}
