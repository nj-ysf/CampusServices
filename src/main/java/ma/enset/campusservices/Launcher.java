package ma.enset.campusservices;

/**
 * Point d'entrée séparé de MainApp.
 * Nécessaire pour que le JAR exécutable fonctionne sans
 * que JavaFX soit installé séparément sur la machine.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
