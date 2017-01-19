import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Maxwell on 03/01/2016.
 */
public class LoginForm extends JFrame {
    private final static String TITLE = "Log in";
    DBClass dbc;
    MainClass mc;
    private JPanel logFrm;
    private JLabel labelUser;
    private JLabel labelPass;
    private JTextField editUser;
    private JPasswordField editPass;
    private JButton buttonOk;
    private JButton buttonCancel;

    public LoginForm(DBClass dbc, MainClass mc) {
        super(TITLE);

        this.dbc = dbc;
        this.mc = mc;

        logFrm = new JPanel();
        logFrm.setLayout(null);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(210, 150);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        labelUser = new JLabel("User ID");
        labelUser.setBounds(20, 20, 150, 20);
        labelUser.setEnabled(true);
        logFrm.add(labelUser);

        labelPass = new JLabel("Password");
        labelPass.setBounds(20, 50, 100, 20);
        labelPass.setEnabled(true);
        logFrm.add(labelPass);

        editUser = new JTextField(32);
        editUser.setBounds(90, 20, 100, 22);
        editUser.setEnabled(true);
        logFrm.add(editUser);

        editPass = new JPasswordField(32);
        editPass.setBounds(90, 50, 100, 22);
        editUser.setEnabled(true);
        logFrm.add(editPass);

        buttonOk = new JButton("OK");
        buttonOk.setBounds(20, 90, 80, 22);
        buttonOk.setVisible(true);
        //buttonOk.requestFocus();
        logFrm.add(buttonOk);

        buttonCancel = new JButton("Cancel");
        buttonCancel.setBounds(110, 90, 80, 22);
        buttonCancel.setVisible(true);
        logFrm.add(buttonCancel);

        getContentPane().add(logFrm);
        getRootPane().setDefaultButton(buttonOk);

        buttonOk.addActionListener(event -> {
                instantiateDBClass();
                runApplication();
                dispose();
            });

        buttonCancel.addActionListener(event -> System.exit(1));
    }

    private void instantiateDBClass() {
        dbc = new DBClass(editUser.getText(), editPass.getText());
    }

    public DBClass getDBClassInstance() {
        return dbc;
    }

    public void runApplication() {
        mc.runApplication();
    }
}