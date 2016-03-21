import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * Created by Maxwell on 02/01/2016.
 */
public class DBClass {
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static String db_url;
    private static String user_name;
    private static String user_pass;

    private Connection conn = null;
    private CallableStatement stmt = null;

    Vector cache;
    int colCount;
    Vector<String> headers;

    DBClass(String user_name, String user_pass) {
        this.user_name = user_name;
        this.user_pass = user_pass;

        db_url = getDatabaseURL();

        try {
            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(db_url, user_name, user_pass);
            conn.setAutoCommit(false);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't connect to database!");
            System.exit(1);
        }
    }


    private String getDatabaseURL() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            return prop.getProperty("database");

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't read 'database' parameter from config.properties!");
            System.exit(1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }


    public void insertCustomerInfo(String firstName,
                                   String lastName,
                                   String middleName,
                                   Calendar birthDate,
                                   String personalID,
                                   Boolean isResident) {
        try {
            String sql = "{? = call bank.insert_customer_info(?,?,?,?,?,?)}";

            long javaTime = birthDate.getTimeInMillis();
            java.sql.Date dateSQL = new java.sql.Date(javaTime);

            conn.setSavepoint();

            stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, middleName);
            stmt.setDate(5, dateSQL);
            stmt.setString(6, personalID);
            stmt.setBoolean(7, isResident);
            stmt.execute();

            if (stmt.getBoolean(1)) {
                conn.commit();
            } else {
                conn.rollback();
            }
            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't insert customer info!");
        }
    }


    public void updateCustomerInfo(int customerID,
                                   String firstName,
                                   String lastName,
                                   String middleName,
                                   java.sql.Date birthDate,
                                   String personalID,
                                   Boolean isResident) {
        try {
            String sql = "{? = call bank.update_customer_info(?,?,?,?,?,?,?)}";

            conn.setSavepoint();

            stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, customerID);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, middleName);
            stmt.setDate(6, birthDate);
            stmt.setString(7, personalID);
            stmt.setBoolean(8, isResident);
            stmt.execute();

            if (stmt.getBoolean(1)) {
                conn.commit();
            } else {
                conn.rollback();
            }
            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't update customer info!");
        }
    }


    public void updateAccountInfo(int accountID,
                                  boolean accountSuspended) {
        try {
            String sql = "{? = call bank.update_account_info(?,?)}";

            conn.setSavepoint();

            stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, accountID);
            stmt.setBoolean(3, accountSuspended);
            stmt.execute();

            if (stmt.getBoolean(1)) {
                conn.commit();
            } else {
                conn.rollback();
            }
            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't update account info!");
        }
    }


    public void unblockCustomer(int customerID,
                                Boolean isActive) {
        try {
            String sql = "{? = call bank.change_customer_activity(?,?)}";

            conn.setSavepoint();

            stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, customerID);
            stmt.setBoolean(3, isActive);
            stmt.execute();

            if (stmt.getBoolean(1)) {
                conn.commit();
            } else {
                conn.rollback();
            }
            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't block/unblock customer info!");
        }
    }


    public ResultSet getCustomerInfo(String pattern) {
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);

            CallableStatement stmt = conn.prepareCall("{ ? = call bank.get_customer_info(?) }");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.setString(2, pattern);
            stmt.execute();

            rs = (ResultSet) stmt.getObject(1);

            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't obtain customer data!");
        } catch (Exception e) {
            cache = new Vector();
            e.printStackTrace();
        }

        return rs;
    }


    public ResultSet getCustomerAccounts(int customerID) {
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);

            CallableStatement stmt = conn.prepareCall("{ ? = call bank.get_customer_accounts(?) }");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.setInt(2, customerID);
            stmt.execute();

            rs = (ResultSet) stmt.getObject(1);

            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't obtain accounts list!");
        } catch (Exception e) {
            cache = new Vector();
            e.printStackTrace();
        }

        return rs;
    }


    public ResultSet getAccountTransactions(int accountID) {
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);

            CallableStatement stmt = conn.prepareCall("{ ? = call bank.get_account_transactions(?) }");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.setInt(2, accountID);
            stmt.execute();

            rs = (ResultSet) stmt.getObject(1);

            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't obtain transaction list!");
        } catch (Exception e) {
            cache = new Vector();
            e.printStackTrace();
        }

        return rs;
    }


    public Vector getAccountTypes() {
        ResultSet rs = null;
        Vector vc = new Vector();

        try {
            conn.setAutoCommit(false);

            CallableStatement stmt = conn.prepareCall("{ ? = call bank.get_account_types() }");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.execute();

            rs = (ResultSet) stmt.getObject(1);

            stmt.close();


            while (rs.next()) {
                vc.addElement(new Item(rs.getInt(1), rs.getString(2)));
            }

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't obtain account types!");
        } catch (Exception e) {
            cache = new Vector();
            e.printStackTrace();
        }

        return vc;
    }


    public Vector getCustomerAccountList(int customerID) {
        ResultSet rs = null;
        Vector vc = new Vector();

        try {
            conn.setAutoCommit(false);

            CallableStatement stmt = conn.prepareCall("{ ? = call bank.get_account_list(?) }");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.setInt(2, customerID);
            stmt.execute();

            rs = (ResultSet) stmt.getObject(1);

            stmt.close();

            while (rs.next()) {
                vc.addElement(new Item(rs.getInt(1), rs.getString(2)));
            }

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't obtain accounts list!");
        } catch (Exception e) {
            cache = new Vector();
            e.printStackTrace();
        }

        return vc;
    }


    public Vector getAllAccountList() {
        ResultSet rs = null;
        Vector vc = new Vector();

        try {
            conn.setAutoCommit(false);

            CallableStatement stmt = conn.prepareCall("{ ? = call bank.get_all_accounts() }");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.execute();

            rs = (ResultSet) stmt.getObject(1);

            stmt.close();

            while (rs.next()) {
                vc.addElement(new Item(rs.getInt(1), rs.getString(2)));
            }

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't obtain accounts list!");
        } catch (Exception e) {
            cache = new Vector();
            e.printStackTrace();
        }

        return vc;
    }


    public Vector getTransactionTypes() {
        ResultSet rs = null;
        Vector vc = new Vector();

        try {
            conn.setAutoCommit(false);

            CallableStatement stmt = conn.prepareCall("{ ? = call bank.get_transaction_types() }");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.execute();

            rs = (ResultSet) stmt.getObject(1);

            stmt.close();

            while (rs.next()) {
                vc.addElement(new Item(rs.getInt(1), rs.getString(2)));
            }

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't obtain transaction types!");
        } catch (Exception e) {
            cache = new Vector();
            e.printStackTrace();
        }

        return vc;
    }


    public void insertAccountInfo(String accountNumber,
                                  int customerID,
                                  Calendar dateOpen,
                                  int typeAccount) {
        try {
            String sql = "{? = call bank.insert_account_info(?,?,?,?)}";

            long javaTime = dateOpen.getTimeInMillis();
            java.sql.Date dateSQL = new java.sql.Date(javaTime);

            conn.setSavepoint();

            stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setString(2, accountNumber);
            stmt.setInt(3, customerID);
            stmt.setDate(4, dateSQL);
            stmt.setInt(5, typeAccount);
            stmt.execute();

            if (stmt.getBoolean(1)) {
                conn.commit();
            } else {
                conn.rollback();
            }
            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't insert account info!");
        }
    }


    public ResultSet getContacts(int customerID) {
        ResultSet rs = null;

        try {
            conn.setAutoCommit(false);

            CallableStatement stmt = conn.prepareCall("{ ? = call bank.get_contacts(?) }");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.setInt(2, customerID);
            stmt.execute();

            rs = (ResultSet) stmt.getObject(1);

            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't obtain contacts list!");
        } catch (Exception e) {
            cache = new Vector();
            e.printStackTrace();
        }

        return rs;
    }


    public ResultSet getAddress(int customerID) {
        ResultSet rs = null;

        try {
            conn.setAutoCommit(false);

            CallableStatement stmt = conn.prepareCall("{ ? = call bank.get_address(?) }");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.setInt(2, customerID);
            stmt.execute();

            rs = (ResultSet) stmt.getObject(1);

            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't obtain adress list!");
        } catch (Exception e) {
            cache = new Vector();
            e.printStackTrace();
        }

        return rs;
    }


    public void insertTransactionInfo(int operationType,
                                      boolean isReversed,
                                      float transactionSum,
                                      int accountDebit,
                                      int accountCredit) {
        try {
            String sql = "{? = call bank.insert_transaction_info(?,?,?,?,?)}";

            conn.setSavepoint();

            stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, operationType);
            stmt.setBoolean(3, isReversed);
            stmt.setFloat(4, transactionSum);
            stmt.setInt(5, accountDebit);
            stmt.setInt(6, accountCredit);
            stmt.execute();
            System.out.println(stmt.getInt(1));
            if (stmt.getInt(1) >= 0) {
                conn.commit();
            } else {
                conn.rollback();
                if (stmt.getInt(1) == -2) {
                    JOptionPane.showMessageDialog(null, "Insufficient funds on debit account!");
                }
            }
            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't insert transaction info!");
        }
    }


    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't close database connection!");
        }
    }

}
