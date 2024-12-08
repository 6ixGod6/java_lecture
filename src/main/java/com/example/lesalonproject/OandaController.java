// package com.example.lesalonproject;

// import java.io.ObjectInputFilter.Config;

// import javax.naming.Context;

// import javafx.event.ActionEvent;
// import javafx.scene.Scene;
// import javafx.scene.control.Label;
// import javafx.scene.control.ProgressIndicator;
// import javafx.scene.layout.GridPane;
// import javafx.stage.Stage;

// public class OandaController {

//     public void accountInformationAction(ActionEvent actionEvent) {
//         // Crie um contexto com a URL e Token fornecidos
//         Context ctx = new Context("https://api-fxpractice.oanda.com", Config.TOKEN);

//         // Janela de progresso
//         Stage progressStage = new Stage();
//         ProgressIndicator progressIndicator = new ProgressIndicator();
//         Label progressLabel = new Label("Loading account information...");
//         GridPane progressPane = new GridPane();
//         progressPane.setHgap(10);
//         progressPane.setVgap(10);
//         progressPane.add(progressIndicator, 0, 0);
//         progressPane.add(progressLabel, 0, 1);
//         Scene progressScene = new Scene(progressPane, 300, 200);
//         progressStage.setScene(progressScene);
//         progressStage.setTitle("Please Wait");
//         progressStage.show();
    
// }
