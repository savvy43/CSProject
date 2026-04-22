package com.hotel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Button login = new Button("login");
        Button signup = new Button("sign up");

        GridPane gridPane  =new GridPane();
        gridPane.add(signup, 0, 0);
        gridPane.add(login, 0, 1);


        Scene scene = new Scene(gridPane, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Hotel of NewUU");
        stage.show();


    }
    public static void main(String[] args){

    }
}