package com.lumina.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main_view.fxml"));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        
        // Add css
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        primaryStage.setTitle("Lumina Currency");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
