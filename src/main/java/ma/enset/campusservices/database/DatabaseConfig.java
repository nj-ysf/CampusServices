package ma.enset.campusservices.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestionnaire de connexion PostgreSQL — Singleton.
 * Lit la configuration depuis src/main/resources/database.properties
 */
public class DatabaseConfig {

    private static DatabaseConfig instance;
    private Connection connection;

    private String url;
    private String user;
    private String password;

    // ── Constructeur privé (Singleton) ───────────────────────
    private DatabaseConfig() {
        chargerProprietes();
    }

    // ── Instance unique thread-safe ───────────────────────────
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    // ── Chargement du fichier database.properties ─────────────
    private void chargerProprietes() {
        Properties props = new Properties();

        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (in == null) {
                throw new RuntimeException(
                        "Fichier database.properties introuvable dans le classpath. " +
                                "Vérifiez qu'il est bien dans src/main/resources/");
            }
            props.load(in);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossible de lire database.properties : " + e.getMessage(), e);
        }

        String host = props.getProperty("db.host",     "localhost");
        String port = props.getProperty("db.port",     "5432");
        String name = props.getProperty("db.name",     "campus_services");

        this.url      = "jdbc:postgresql://" + host + ":" + port + "/" + name;
        this.user     = props.getProperty("db.user",     "postgres");
        this.password = props.getProperty("db.password", "");
    }

    // ── Retourne la connexion active (reconnecte si fermée) ───
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("[DB] Connexion établie → " + url);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(
                    "[DB] Échec de connexion à la base de données : " + e.getMessage(), e);
        }
    }

    // ── Ferme proprement la connexion ─────────────────────────
    public void fermer() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erreur lors de la fermeture : " + e.getMessage());
        }
    }

    // ── Getters utiles ────────────────────────────────────────
    public String getUrl()  { return url;  }
    public String getUser() { return user; }

    // ── Test de connexion ─────────────────────────────────────
    public static void main(String[] args) {
        DatabaseConfig db = DatabaseConfig.getInstance();
        try {
            Connection conn = db.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion réussie !");
                System.out.println("   URL      : " + db.getUrl());
                System.out.println("   User     : " + db.getUser());
                System.out.println("   Catalog  : " + conn.getCatalog());
            }
        } catch (Exception e) {
            System.err.println("❌ Connexion échouée : " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.fermer();
        }
    }
}