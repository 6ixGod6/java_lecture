package com.example.lesalonproject;

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


import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class AccountInfoController {

    @FXML
    private Label accountIdLabel;
    @FXML
    private Label aliasLabel;
    @FXML
    private Label currencyLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label navLabel;
    @FXML
    private Label unrealizedPLLabel;
    @FXML
    private Label marginRateLabel;
    @FXML
    private Label openTradesLabel;
    @FXML
    private ProgressIndicator progressIndicator;

    public void initialize() {
        fetchAccountData();
    }

    private void fetchAccountData() {
        // Display the progress indicator while loading
        progressIndicator.setVisible(true);

        // Background task to fetch account data
        Task<AccountSummary> task = new Task<>() {
            @Override
            protected AccountSummary call() throws Exception {
                Context ctx = new Context("https://api-fxpractice.oanda.com", Config.TOKEN);
                return ctx.account.summary(new AccountID(Config.ACCOUNTID)).getAccount();
            }
        };

        // When the task is successful
        task.setOnSucceeded(workerStateEvent -> {
            progressIndicator.setVisible(false); // Hide the progress indicator
            AccountSummary summary = task.getValue();
            updateAccountInfo(summary); // Update the UI with the fetched data
        });

        // When the task fails
        task.setOnFailed(workerStateEvent -> {
            progressIndicator.setVisible(false); // Hide the progress indicator
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Account Information");
            alert.setContentText(task.getException().getMessage());
            alert.showAndWait();
        });

        // Run the task in a background thread
        new Thread(task).start();
    }

    private void updateAccountInfo(AccountSummary summary) {
        accountIdLabel.setText(summary.getId().toString());
        aliasLabel.setText(summary.getAlias());
        currencyLabel.setText(summary.getCurrency().toString());
        balanceLabel.setText(summary.getBalance().toString());
        navLabel.setText(summary.getNAV().toString());
        unrealizedPLLabel.setText(summary.getUnrealizedPL().toString());
        marginRateLabel.setText(summary.getMarginRate().toString());
        openTradesLabel.setText(String.valueOf(summary.getOpenTradeCount()));
    }
}
