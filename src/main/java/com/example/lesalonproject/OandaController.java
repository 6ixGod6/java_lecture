import com.example.lesalonproject.Config;
import com.oanda.v20.order.*;
import com.oanda.v20.position.PositionCloseRequest;
import com.oanda.v20.position.PositionCloseResponse;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.primitives.InstrumentName;
import com.oanda.v20.trade.TradeCloseRequest;
import com.oanda.v20.trade.TradeCloseResponse;
import com.oanda.v20.trade.TradeSpecifier;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.util.List;
import javafx.stage.Stage;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.oanda.v20.Context;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountSummary;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;

import com.oanda.v20.position.PositionGetResponse;

import com.oanda.v20.pricing.PricingGetResponse;



import java.util.List;



public void accountInformationAction(ActionEvent actionEvent) {
    // Crie um contexto com a URL e Token fornecidos
    Context ctx = new Context("https://api-fxpractice.oanda.com", Config.TOKEN);

    // Janela de progresso
    Stage progressStage = new Stage();
    ProgressIndicator progressIndicator = new ProgressIndicator();
    Label progressLabel = new Label("Loading account information...");
    GridPane progressPane = new GridPane();
    progressPane.setHgap(10);
    progressPane.setVgap(10);
    progressPane.add(progressIndicator, 0, 0);
    progressPane.add(progressLabel, 0, 1);
    Scene progressScene = new Scene(progressPane, 300, 200);
    progressStage.setScene(progressScene);
    progressStage.setTitle("Please Wait");
    progressStage.show();

    // Tarefa para buscar dados em segundo plano
    Task<AccountSummary> task = new Task<>() {
        @Override
        protected AccountSummary call() throws Exception {
            // Solicita um resumo da conta usando o AccountID
            return ctx.account.summary(new AccountID(Config.ACCOUNTID)).getAccount();
        }
    };

    // Quando a tarefa for concluída com sucesso
    task.setOnSucceeded(workerStateEvent -> {
        progressStage.close(); // Fecha o indicador de progresso
        AccountSummary summary = task.getValue();
        // Cria uma nova janela para exibir as informações
        Stage infoStage = new Stage();
        GridPane infoPane = new GridPane();
        infoPane.setHgap(10);
        infoPane.setVgap(10);

        // Adiciona as informações formatadas
        infoPane.add(new Label("Account ID:"), 0, 0);
        infoPane.add(new Label(summary.getId().toString()), 1, 0);

        infoPane.add(new Label("Alias:"), 0, 1);
        infoPane.add(new Label(summary.getAlias()), 1, 1);

        infoPane.add(new Label("Currency:"), 0, 2);
        infoPane.add(new Label(summary.getCurrency().toString()), 1, 2);

        infoPane.add(new Label("Balance:"), 0, 3);
        infoPane.add(new Label(summary.getBalance().toString()), 1, 3);

        infoPane.add(new Label("NAV:"), 0, 4);
        infoPane.add(new Label(summary.getNAV().toString()), 1, 4);

        infoPane.add(new Label("Unrealized P/L:"), 0, 5);
        infoPane.add(new Label(summary.getUnrealizedPL().toString()), 1, 5);

        infoPane.add(new Label("Margin Rate:"), 0, 6);
        infoPane.add(new Label(summary.getMarginRate().toString()), 1, 6);

        infoPane.add(new Label("Open Trades:"), 0, 7);
        infoPane.add(new Label(String.valueOf(summary.getOpenTradeCount())), 1, 7);

        // Cria a cena da janela de informações
        Scene infoScene = new Scene(infoPane, 400, 300);
        infoStage.setScene(infoScene);
        infoStage.setTitle("Account Information");
        infoStage.show();
    });

    task.setOnFailed(workerStateEvent -> {
        progressStage.close(); // Fecha o indicador de progresso
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to Load Account Information");
        alert.setContentText(task.getException().getMessage());
        alert.showAndWait();
    });

    // Executa a tarefa em uma nova thread
    new Thread(task).start();
}
