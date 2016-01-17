/**
 * Created by Maxwell on 03/01/2016.
 */
public class MainClass {

    DBClass dbc;
    LoginForm lf;

    public static Boolean runApp = false;

    public MainClass() {
        lf = new LoginForm(dbc, this);
        lf.setVisible(true);
    }

    public void runApplication() {
        dbc = lf.getDBClassInstance();

        MainForm mf = new MainForm(dbc);
    }
}
