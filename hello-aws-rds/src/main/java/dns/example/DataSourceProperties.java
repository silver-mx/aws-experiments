package dns.example;

public class DataSourceProperties {

    private final String host;
    private final int port;
    private final String db;
    private final String username;
    private final String password;

    public DataSourceProperties() {
        this.host = System.getenv("RDS_HOSTNAME");
        this.db = System.getenv("RDS_DB_NAME");
        this.port = Integer.parseInt(System.getenv("RDS_PORT"));
        this.username = System.getenv("RDS_USER");
        this.password = System.getenv("RDS_PASSWORD");
    }

    public String asPostgresqlJdbcUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s", host, port, db);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDb() {
        return db;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
