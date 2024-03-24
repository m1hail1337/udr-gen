package ru.nxbootcamp.semenov.cdr.db.contacts;

import ru.nxbootcamp.semenov.cdr.db.JdbcRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class ContactsRepository extends JdbcRepository {

    private boolean logsEnabled = false;

    public ContactsRepository() {
        initRepository();
    }

    @Override
    protected void initRepository() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, username, password)) {
            String createContactsTable = """
                    CREATE TABLE IF NOT EXISTS Contacts (
                        phone VARCHAR(11) PRIMARY KEY
                    );
                    """;
            String deleteExistingData = "DELETE FROM Contacts";
            Statement statement = connection.createStatement();
            statement.execute(createContactsTable);
            statement.execute(deleteExistingData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertContacts(Set<String> contacts) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, username, password)) {
            String sql = "INSERT INTO Contacts VALUES ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            for (String phoneNumber : contacts) {
                statement.setString(1, phoneNumber);
                statement.addBatch();
            }
            int[] insertedRows = statement.executeBatch();
            if (logsEnabled) {
                System.out.println("[INFO] Inserted into 'Contacts' " + insertedRows.length + " phone numbers:");
                System.out.println("---------------");
                System.out.println("|    Phone    |");
                System.out.println("---------------");
                for (String contact : contacts) {
                    System.out.println("| " + contact + " |");
                }
                System.out.println("---------------");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLogsEnabled(boolean logsEnabled) {
        this.logsEnabled = logsEnabled;
    }
}
