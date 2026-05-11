package ma.enset.campusservices;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        afficherLogin();
        stage.setTitle("CampusServices — ENSET");
        stage.setResizable(false);
        stage.show();
    }

    public static void afficherLogin() throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(MainApp.class.getResource("login.fxml")));
        Scene scene = new Scene(root, 460, 580);
        scene.getStylesheets().add(css());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
    }

    public static void afficherPrincipal() throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(MainApp.class.getResource("main.fxml")));
        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(css());
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
    }

    public static void afficherAdmin() throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(MainApp.class.getResource("admin-main.fxml")));
        Scene scene = new Scene(root, 1150, 720);
        scene.getStylesheets().add(css());
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(620);
    }

    private static String css() {
        return Objects.requireNonNull(
                MainApp.class.getResource("css/style.css")).toExternalForm();
    }

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) { launch(args); }
}
