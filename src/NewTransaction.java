import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewTransaction {
    private final static String TITLE = "Adding new transaction";
    DBClass dbc;
    JDialog dlg;
    JLabel labelType;
    JLabel labelSum;
    JLabel labelAccountDebit;
    JLabel labelAccountCredit;
    JComboBox comboType;
    JTextField textSum;
    JComboBox comboAccountDebit;
    JComboBox comboAccountCredit;
    JButton buttonOk;
    JButton buttonCancel;
    int editLength = 160;
    int editOffset = 120;
    int labelLength = 110;

    public NewTransaction(DBClass dbc) {
        this.dbc = dbc;

        dlg = new JDialog();
        dlg.setTitle(TITLE);
        dlg.setLayout(null);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setSize(300, 220);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(null);
        dlg.setModal(true);

        labelType = new JLabel("Transaction type");
        labelType.setEnabled(true);
        labelType.setBounds(20, 20, labelLength, 20);
        dlg.add(labelType);

        labelSum = new JLabel("Amount");
        labelSum.setEnabled(true);
        labelSum.setBounds(20, 50, labelLength, 20);
        dlg.add(labelSum);

        labelAccountDebit = new JLabel("Account debit");
        labelAccountDebit.setEnabled(true);
        labelAccountDebit.setBounds(20, 80, labelLength, 20);
        dlg.add(labelAccountDebit);

        labelAccountCredit = new JLabel("Account credit");
        labelAccountCredit.setEnabled(true);
        labelAccountCredit.setBounds(20, 110, labelLength, 20);
        dlg.add(labelAccountCredit);

        comboType = new JComboBox(dbc.getTransactionTypes());
        comboType.setEnabled(true);
        comboType.setBounds(editOffset, 20, editLength, 22);
        comboType.setSelectedItem(comboType.getItemAt(0));
        comboType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((comboType.getSelectedIndex() != -1)) {

                    if (comboType.getSelectedItem().toString().equals("REFILL")) {
                        for (int i = 0; i < comboAccountDebit.getItemCount(); i++) {
                            if (comboAccountDebit.getItemAt(i).toString().endsWith("15")) {
                                comboAccountDebit.setSelectedIndex(i);
                            }
                        }
                        comboAccountDebit.setEnabled(false);
                        comboAccountCredit.setEnabled(true);

                    } else if (comboType.getSelectedItem().toString().equals("WITHDRAWAL")) {
                        for (int i = 0; i < comboAccountCredit.getItemCount(); i++) {
                            if (comboAccountCredit.getItemAt(i).toString().endsWith("15")) {
                                comboAccountCredit.setSelectedIndex(i);
                            }
                        }
                        comboAccountDebit.setEnabled(true);
                        comboAccountCredit.setEnabled(false);

                    } else {
                        comboAccountDebit.setEnabled(true);
                        comboAccountCredit.setEnabled(true);
                    }
                }
            }
        });
        dlg.add(comboType);

        textSum = new JTextField();
        textSum.setEnabled(true);
        textSum.setBounds(editOffset, 50, editLength, 22);
        dlg.add(textSum);

        comboAccountDebit = new JComboBox(dbc.getAllAccountList());
        comboAccountDebit.setEnabled(true);
        comboAccountDebit.setBounds(editOffset, 80, editLength, 22);
        comboAccountDebit.setSelectedItem(comboType.getItemAt(0));
        dlg.add(comboAccountDebit);

        comboAccountCredit = new JComboBox(dbc.getAllAccountList());
        comboAccountCredit.setEnabled(true);
        comboAccountCredit.setBounds(editOffset, 110, editLength, 22);
        comboAccountCredit.setSelectedItem(comboType.getItemAt(0));
        dlg.add(comboAccountCredit);

        buttonOk = new JButton("Save");
        buttonOk.setEnabled(true);
        buttonOk.setBounds(55, 150, 80, 22);
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
        buttonCancel.setBounds(155, 150, 80, 22);
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
        Item itemType = (Item) comboType.getSelectedItem();
        Item itemDebit = (Item) comboAccountDebit.getSelectedItem();
        Item itemCredit = (Item) comboAccountCredit.getSelectedItem();

        dbc.insertTransactionInfo(itemType.getId(),
                false,
                new Float(textSum.getText()),
                itemDebit.getId(),
                itemCredit.getId());
    }

    private void formDispose() {
        dlg.dispose();
    }

    private Boolean checkFields() {
        if ((textSum.getText().trim().length() == 0) || (!textSum.getText().matches("[-+]?\\d*\\.?\\d+"))) {
            return false;
        }

        return true;
    }
}
