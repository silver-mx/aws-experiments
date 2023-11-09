package dns.example;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListSubscribersTest {

    @Test
    void shouldListAllSubscribers() throws SQLException {
        // Remember to set the environment variables
        var subscribers = new ListSubscribers();
        assertEquals(2, subscribers.handleRequest(null, null).size());
    }
}