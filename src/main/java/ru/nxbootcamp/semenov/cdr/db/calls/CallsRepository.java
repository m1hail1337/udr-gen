package ru.nxbootcamp.semenov.cdr.db.calls;

import ru.nxbootcamp.semenov.cdr.call.Call;
import ru.nxbootcamp.semenov.cdr.db.JdbcRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;


public class CallsRepository extends JdbcRepository {

    private boolean logsEnabled = false;

    public CallsRepository() {
        initRepository();
    }

    @Override
    protected void initRepository() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, username, password)) {
            String createCallEnum = "CREATE TYPE IF NOT EXISTS CALL_TYPE AS ENUM ('INCOMING', 'OUTGOING')";
            String createCallsTable = """
                    CREATE TABLE IF NOT EXISTS Calls (
                        id SERIAL PRIMARY KEY,
                        type CALL_TYPE,
                        phone VARCHAR(11) REFERENCES Contacts(phone) ON DELETE CASCADE,
                        start TIMESTAMP,
                        finish TIMESTAMP
                    );
                    """;
            String deleteExistingData = """
                    DELETE FROM Calls;
                    ALTER TABLE Calls ALTER COLUMN id RESTART WITH 1
                    """;
            Statement statement = connection.createStatement();
            statement.execute(createCallEnum);
            statement.execute(createCallsTable);
            statement.execute(deleteExistingData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(List<Call> calls) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, username, password)) {
            String sql = "INSERT INTO Calls(type, phone, start, finish) VALUES (?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql);
            for (Call call : calls) {
                statement.setString(1, call.type().name());
                statement.setString(2, call.msisdn());
                statement.setTimestamp(3, Timestamp.valueOf(call.start()));
                statement.setTimestamp(4, Timestamp.valueOf(call.finish()));
                statement.addBatch();
            }
            int[] insertedRows = statement.executeBatch();

            if (logsEnabled) {
                System.out.println("[INFO] Calls (" + insertedRows.length + ")" + " for "
                        + calls.get(0).start().getMonth() + " saved");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLogsEnabled(boolean logsEnabled) {
        this.logsEnabled = logsEnabled;
    }
}
