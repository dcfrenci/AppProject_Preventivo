package com.preventivoapp.appproject_preventivo;

import com.preventivoapp.appproject_preventivo.controller.quoteMainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuoteMainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(QuoteMainApplication.class.getResource("quoteMain-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Quote Maker");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            quoteMainController quoteMainController = fxmlLoader.getController();
            quoteMainController.handleSaveData();
            Platform.exit();
            System.exit(0);
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}
