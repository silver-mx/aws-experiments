package dns.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

public class ListSubscribers implements RequestHandler<Void, List<String>> {

    private final AtomicInteger execCounter = new AtomicInteger(0);
    private Connection connection;

    public ListSubscribers() throws SQLException {
        DataSourceProperties dbProperties = new DataSourceProperties();

        // Experiment caching resources for the lambda function
        if (isNull(connection) || !connection.isValid(0)) {
            connection = DriverManager.getConnection(dbProperties.asPostgresqlJdbcUrl(),
                    dbProperties.getUsername(), dbProperties.getPassword());
        }
    }

    @Override
    public List<String> handleRequest(Void unused, Context context) {
        List<String> subscribers = new ArrayList<>();

        try {
            if (!connection.isValid(0)) {
                System.out.println("Unable to connecto to database");
                System.exit(1);
            }

            PreparedStatement selectSubscribers = connection.prepareStatement("SELECT * FROM subscribers");
            ResultSet resultSet = selectSubscribers.executeQuery();

            while (resultSet.next()) {
                var email = resultSet.getString("email");
                System.out.println("Email=" + email);
                subscribers.add(email);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        execCounter.incrementAndGet();
        System.out.println("The function has been called " + execCounter.get() + " times");

        return subscribers;
    }
}
