package openbrfacerecogapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private volatile static Stage stage;
    private static Scene mainForm;

    private static void startUpConfigs() {

    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        stage = primaryStage;
        Parent telaA = FXMLLoader.load(getClass().getResource("/fxml/MainPage.fxml"));
        mainForm = new Scene(telaA);
        mainForm.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(mainForm);
        primaryStage.setFullScreen(false);
        primaryStage.setMaximized(false);
        primaryStage.setTitle("WebCam Capture App");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    @Override
    public void stop() {

        CameraCaptureController.isCapture = true;

    }
}
