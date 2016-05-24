/**
 * Created by Maxwell on 03/01/2016.
 */
public class MainClass {

    public static Boolean runApp = false;
    DBClass dbc;
    LoginForm lf;

    public MainClass() {
        lf = new LoginForm(dbc, this);
        lf.setVisible(true);
    }

    public void runApplication() {
        dbc = lf.getDBClassInstance();

        MainForm mf = new MainForm(dbc);
    }
}
