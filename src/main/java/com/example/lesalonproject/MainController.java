package com.example.lesalonproject;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputFilter.Config;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;



import com.example.lesalonproject.service.MNBArfolyamServiceSoap;
import com.example.lesalonproject.service.MNBArfolyamServiceSoapImpl;
import com.oanda.v20.Context;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountSummary;

import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.trade.TradeCloseRequest;
import com.oanda.v20.trade.TradeCloseResponse;
import com.oanda.v20.trade.TradeSpecifier;
import com.example.lesalonproject.model.MarketPosition;
import com.example.lesalonproject.model.Position;
import com.example.lesalonproject.model.PositionData;
import com.example.lesalonproject.model.PriceData;

import com.example.lesalonproject.Configuration;


public class MainController {

     
    @FXML
    private StackPane contentPane;

    private String downloadSoapData(String startDate, String endDate, String selectedCurrency) throws Exception {
        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();

        return service.getExchangeRates(startDate, endDate, selectedCurrency);
    }

     private void saveDataToFile(String data) {
        // Get the current project directory
        String projectDirectory = System.getProperty("user.dir");

        // Create the file path inside the project directory
        File file = new File(projectDirectory + File.separator + "bank.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            // Write the data to the file (overwrites the file if it already exists)
            writer.write(data);
        } catch (IOException e) {
            // Show error if unable to save the file
            showAlert("Error", "Failed to save the file: " + e.getMessage());
        }
    }

    public void openDownloadForm(ActionEvent actionEvent) {
        // Create a new window
        Stage downloadStage = new Stage();
        downloadStage.setTitle("Download SOAP Data");


        Label startDateLabel = new Label("Start Date:");
        DatePicker startDatePicker = new DatePicker();

        Label endDateLabel = new Label("End Date:");
        DatePicker endDatePicker = new DatePicker();

        Label currencyLabel = new Label("Currency:");
        ComboBox<String> currencyComboBox = new ComboBox<>();
        currencyComboBox.getItems().addAll("USD", "EUR", "HUF");


        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);


        Button downloadButton = new Button("Download Data");
        downloadButton.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String selectedCurrency = currencyComboBox.getValue();

            if (startDate == null || endDate == null || selectedCurrency == null) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }


            if (startDate.isAfter(endDate)) {
                showAlert("Error -_-", "Start date cannot be after the end date.");
                return;
            }


            Thread downloadThread = new Thread(() -> {
                try {
                    // Simulate progress updates
                    for (int i = 0; i <= 100; i++) {
                        double progress = i / 100.0;
                        Thread.sleep(10); // Simulate progress delay
                        final int currentProgress = i;


                        javafx.application.Platform.runLater(() -> progressBar.setProgress(progress));
                    }


                    String exchangeRates = downloadSoapData(startDate.toString(), endDate.toString(), selectedCurrency);


                    saveDataToFile(exchangeRates);


                    javafx.application.Platform.runLater(() ->
                            showAlert("Download Successful", "All data has been downloaded to the file bank.txt.")
                    );

                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                            showAlert("Error", "An error occurred during the download: " + ex.getMessage())
                    );
                }
            });

            downloadThread.start();
        });


        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(
                startDateLabel, startDatePicker,
                endDateLabel, endDatePicker,
                currencyLabel, currencyComboBox,
                progressBar, downloadButton
        );


        Scene scene = new Scene(vbox, 300, 350);
        downloadStage.setScene(scene);
        downloadStage.show();
    }

    private void downloadFilteredData(String startDate, String endDate, String currency, boolean includeMetaData, String dataOption, ProgressBar progressBar) {
        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();

        try {
            // Simulate data download progress
            for (int i = 0; i <= 100; i += 10) {
                final int progress = i;
                Thread.sleep(50); // Simulate processing delay
                progressBar.setProgress(progress / 100.0);
            }

            // Call SOAP service to get filtered data
            String exchangeRates = service.getExchangeRates(startDate, endDate, currency);

            // Add metadata or modify data if required
            if (includeMetaData) {
                exchangeRates += "\nMetadata: Data generated with option " + dataOption;
            }

            // Save data to Bank.txt
            saveDataToFile(exchangeRates);

            showAlert("Download Successful", "Filtered data has been saved to Bank.txt.");
        } catch (Exception e) {
            showAlert("Error", "An error occurred during the download: " + e.getMessage());
        }
    }
    // Method to graph the data

    private LineChart<String, Number> generateGraph(String startDate, String endDate, String currency, boolean includeMetaData) {
        // Simulated data fetching and processing
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();

        Random random = new Random();
        for (int i = 1; i <= 10; i++) {
            data.add(new XYChart.Data<>("Day " + i, random.nextDouble() * 100)); // Simulated exchange rate
        }

        // Graph series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Exchange Rate for " + currency + " (" + startDate + " to " + endDate + ")");
        series.setData(data);

        // Include metadata, if selected
        if (includeMetaData) {
            series.setName(series.getName() + " - Metadata Included");
        }

        // Line chart setup
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Exchange Rate");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.getData().add(series);
        lineChart.setTitle("Currency Exchange Rates");

        return lineChart;
    }

    public void downloadFilteredSoapData(javafx.event.ActionEvent actionEvent){

        Stage filterStage = new Stage();
        filterStage.setTitle("Download Filtered SOAP Data");

        // Create input components
        Label startDateLabel = new Label("Start Date:");
        DatePicker startDatePicker = new DatePicker();

        Label endDateLabel = new Label("End Date:");
        DatePicker endDatePicker = new DatePicker();

        Label currencyLabel = new Label("Currency:");
        ComboBox<String> currencyComboBox = new ComboBox<>();
        currencyComboBox.getItems().addAll("USD", "EUR", "HUF");

        Label optionsLabel = new Label("Additional Options:");
        CheckBox includeMetaDataCheckbox = new CheckBox("Include Metadata");
        RadioButton detailedDataRadio = new RadioButton("Detailed Data");
        RadioButton summaryDataRadio = new RadioButton("Summary Data");


        ToggleGroup dataOptionsGroup = new ToggleGroup();
        detailedDataRadio.setToggleGroup(dataOptionsGroup);
        summaryDataRadio.setToggleGroup(dataOptionsGroup);


        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setVisible(false);


        Button downloadButton = new Button("Download Data");
        downloadButton.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String selectedCurrency = currencyComboBox.getValue();
            boolean includeMetaData = includeMetaDataCheckbox.isSelected();
            RadioButton selectedDataOption = (RadioButton) dataOptionsGroup.getSelectedToggle();

            if (startDate == null || endDate == null || selectedCurrency == null || selectedDataOption == null) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            if (((java.time.LocalDate) startDate).isAfter(endDate)) {
                showAlert("Error", "Start date cannot be after end date.");
                return;
            }

            String dataOption = selectedDataOption.getText();


            progressBar.setVisible(true);
            downloadFilteredData(startDate.toString(), endDate.toString(), selectedCurrency, includeMetaData, dataOption, progressBar);
        });

        // Arrange components in a layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(
                startDateLabel, startDatePicker,
                endDateLabel, endDatePicker,
                currencyLabel, currencyComboBox,
                optionsLabel, includeMetaDataCheckbox, detailedDataRadio, summaryDataRadio,
                progressBar, downloadButton
        );

        // Set the scene and show the stage
        Scene scene = new Scene(vbox, 350, 400);
        filterStage.setScene(scene);
        filterStage.show();
    }


    public void graphSoapData(ActionEvent actionEvent) {
        {

            Stage graphStage = new Stage();
            graphStage.setTitle("Graph SOAP Data");


            Label startDateLabel = new Label("Start Date:");
            DatePicker startDatePicker = new DatePicker();

            Label endDateLabel = new Label("End Date:");
            DatePicker endDatePicker = new DatePicker();

            Label currencyLabel = new Label("Currency:");
            ComboBox<String> currencyComboBox = new ComboBox<>();
            currencyComboBox.getItems().addAll("USD", "EUR", "HUF");

            CheckBox includeMetaDataCheckBox = new CheckBox("Include Metadata");

            Button generateGraphButton = new Button("Generate Graph");


            VBox graphContainer = new VBox(10);

            generateGraphButton.setOnAction(e -> {
                String startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : "";
                String endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : "";
                String selectedCurrency = currencyComboBox.getValue();

                if (startDate.isEmpty() || endDate.isEmpty() || selectedCurrency == null) {
                    showAlert("Error -_-", "Please fill in all fields");
                    return;
                }


                graphContainer.getChildren().clear();


                LineChart<String, Number> lineChart = generateGraph(startDate, endDate, selectedCurrency, includeMetaDataCheckBox.isSelected());
                graphContainer.getChildren().add(lineChart);
            });


            VBox vbox = new VBox(10);
            vbox.setPadding(new javafx.geometry.Insets(10));
            vbox.getChildren().addAll(
                    startDateLabel, startDatePicker,
                    endDateLabel, endDatePicker,
                    currencyLabel, currencyComboBox,
                    includeMetaDataCheckBox,
                    generateGraphButton,
                    graphContainer
            );

            Scene scene = new Scene(vbox, 800, 600);
            graphStage.setScene(scene);
            graphStage.show();
        }
    }

    
//***************************************************************************************************************************************
//***************************************************************************************************************************************

    

    

   


    
    




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    



    public void handleParallels(){
        loadView("parallelProgrammingView.fxml");
    }

    public void handleRelationships(){
        loadView("RelationshipView.fxml");
    }

    public void handleOsDelete(){
        loadView("opsystemDeleteView.fxml");
    }

    public void handleOsUpdate(){
        loadView("opsystemUpdateView.fxml");
    }
    public void handleOsCreate(){
        loadView("opsystemCreateView.fxml");
    }
    public void handleOsRead(){
        loadView("opsystemReadView.fxml");
    }

    public void handleProcessorDelete(){
        loadView("processDeleteView.fxml");
    }

    public void handleProcessorUpdate(){
        loadView("processorUpdateView.fxml");
    }


    public void handleProcessorCreate(){
        loadView("processorCreateView.fxml");
    }

    public void handleProcessorRead(){
        loadView("processorReadView.fxml");
    }

    public void handleRead(){
        loadView("read_view.fxml");
    }

    public void handleRead2(){
        loadView("read2_view.fxml");
    }

    public void handleWrite(){
        loadView("write_view.fxml");
    }

    public void handleChange(){
        loadView("change_view.fxml");
    }

    public void handleDelete(){
        loadView("delete_view.fxml");
    }

    public void accountInformationAction(ActionEvent actionEvent) {
        Context ctx = new Context("https://api-fxpractice.oanda.com", Configuration.TOKEN);


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

        Task<AccountSummary> task = new Task<>() {
            @Override
            protected AccountSummary call() throws Exception {

                return ctx.account.summary(new AccountID(Configuration.ACCOUNTID)).getAccount();
            }
        };

        


        
        task.setOnSucceeded(workerStateEvent -> {
            progressStage.close();
            AccountSummary summary = task.getValue();


            Stage infoStage = new Stage();
            GridPane infoPane = new GridPane();
            infoPane.setHgap(10);
            infoPane.setVgap(10);

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


            Scene infoScene = new Scene(infoPane, 400, 300);
            infoStage.setScene(infoScene);
            infoStage.setTitle("Account Information");
            infoStage.show();
        });


        task.setOnFailed(workerStateEvent -> {
            progressStage.close();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Account Information");
            alert.setContentText(task.getException().getMessage());
            alert.showAndWait();
        });


        new Thread(task).start();
    }

    public void CurrentPriceDeci(ActionEvent actionEvent) {
        Stage priceStage = new Stage();
        priceStage.setTitle("Current prices");


        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Check Current Prices");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


        ComboBox<String> currencyPairDropdown = new ComboBox<>();
        currencyPairDropdown.getItems().addAll("EUR/USD", "USD/JPY", "GBP/USD", "AUD/USD", "USD/CHF");
        currencyPairDropdown.setPromptText("Please Select a currency pair.");
        currencyPairDropdown.setPrefWidth(200);


        Button fetchPriceButton = new Button("Get Current Price");
        fetchPriceButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");


        Label priceLabel = new Label();
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-alignment: center;");


        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);


        mainLayout.getChildren().addAll(titleLabel, currencyPairDropdown, fetchPriceButton, loadingIndicator, priceLabel);


        Scene scene = new Scene(mainLayout, 400, 300);
        priceStage.setScene(scene);
        priceStage.show();


        fetchPriceButton.setOnAction(e -> {
            String selectedPair = currencyPairDropdown.getValue();

            if (selectedPair == null) {
                priceLabel.setText("Please select a currency pair.");
                return;
            }


            loadingIndicator.setVisible(true);
            priceLabel.setText("");


            Context ctx = new Context("https://api-fxpractice.oanda.com", Configuration.TOKEN);

            try {

                PricingGetRequest request = new PricingGetRequest(
                        new com.oanda.v20.account.AccountID(Configuration.ACCOUNTID),
                        List.of(selectedPair.replace("/", "_"))
                );


                PricingGetResponse pricingResponse = ctx.pricing.get(request);


                ClientPrice clientPrice = pricingResponse.getPrices().get(0);
                String bidVal = clientPrice.getBids().get(0).getPrice().toString();
                String askVal = clientPrice.getAsks().get(0).getPrice().toString();

                priceLabel.setText(String.format("Bid: %s | Ask: %s", bidVal, askVal));



                priceLabel.setText(String.format("Bid: %s | Ask: %s", bidVal, askVal));
            } catch (Exception ex) {
                priceLabel.setText("Failed to load the prices -_-.");
                ex.printStackTrace();
            } finally {

                loadingIndicator.setVisible(false);
            }
        });
    }

    public void historicalPricesAction(ActionEvent actionEvent) {
        Stage historicalStage = new Stage();
        historicalStage.setTitle("Historical Prices");



        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));


        Label titleLabel = new Label("Historical Prices");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


        ComboBox<String> currencyPairDropdown = new ComboBox<>();
        currencyPairDropdown.getItems().addAll("EUR/USD", "USD/JPY", "GBP/USD", "AUD/USD", "USD/CHF");
        currencyPairDropdown.setPromptText("Select a currency pair");
        currencyPairDropdown.setPrefWidth(200);


        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");


        Button displayPriceButton = new Button("Get Historical Prices");
        displayPriceButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");


        TableView<PriceData> priceTable = new TableView<>();
        TableColumn<PriceData, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        TableColumn<PriceData, String> bidColumn = new TableColumn<>("Bid");
        bidColumn.setCellValueFactory(cellData -> cellData.getValue().bidProperty());
        TableColumn<PriceData, String> askColumn = new TableColumn<>("Ask");
        askColumn.setCellValueFactory(cellData -> cellData.getValue().askProperty());

        priceTable.getColumns().addAll(dateColumn, bidColumn, askColumn);


        mainLayout.getChildren().addAll(titleLabel, currencyPairDropdown, startDatePicker, endDatePicker, displayPriceButton, priceTable);


        Scene scene = new Scene(mainLayout, 600, 400);
        historicalStage.setScene(scene);
        historicalStage.show();


        displayPriceButton.setOnAction(e -> {
            String selectedPair = currencyPairDropdown.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (selectedPair == null || startDate == null || endDate == null) {

                Error("Please select a currency pair and dates.");
                return;
            }


            Context ctx = new Context("https://api-fxpractice.oanda.com", Configuration.TOKEN);

            try {

                PricingGetRequest request = new PricingGetRequest(
                        new AccountID(Configuration.ACCOUNTID),
                        List.of(selectedPair.replace("/", "_"))
                );


                PricingGetResponse pricingResponse = ctx.pricing.get(request);


                List<PriceData> historicalPrices = new ArrayList<>();
                for (ClientPrice clientPrice : pricingResponse.getPrices()) {
                    // Assuming the historical data is available in the ClientPrice object
                    String bid = clientPrice.getBids().get(0).getPrice().toString();
                    String ask = clientPrice.getAsks().get(0).getPrice().toString();
                    // Populate the list with the historical data
                    historicalPrices.add(new PriceData(clientPrice.getTime().toString(), bid, ask));
                }


                priceTable.getItems().setAll(historicalPrices);
            } catch (Exception ex) {
                Error("Failed to fetch historical prices.");
                ex.printStackTrace();
            }
        });
    }
    private void Error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }    public void positionOpeningAction(ActionEvent actionEvent) {

        Stage positionStage = new Stage();
        positionStage.setTitle("Open Position");


        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));


        Label titleLabel = new Label("Open a Position");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


        ComboBox<String> currencyPairDropdown = new ComboBox<>();
        currencyPairDropdown.getItems().addAll("EUR/USD", "USD/JPY", "GBP/USD", "AUD/USD", "USD/CHF");
        currencyPairDropdown.setPromptText("Please Select a currency pair ");
        currencyPairDropdown.setPrefWidth(200);


        TextField quantityField = new TextField();
        quantityField.setPromptText("Enter quantity");
        quantityField.setPrefWidth(200);


        ComboBox<String> directionDropdown = new ComboBox<>();
        directionDropdown.getItems().addAll(" Buy ", " Sell ");
        directionDropdown.setPromptText("Select direction to do");
        directionDropdown.setPrefWidth(200);


        Button openPositionButton = new Button("Open Position");
        openPositionButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");


        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-alignment: center;");


        mainLayout.getChildren().addAll(titleLabel, currencyPairDropdown, quantityField, directionDropdown, openPositionButton, resultLabel);


        Scene scene = new Scene(mainLayout, 400, 300);
        positionStage.setScene(scene);
        positionStage.show();


        openPositionButton.setOnAction(e -> {
            String selectedCurrPair = currencyPairDropdown.getValue();
            String quantityText = quantityField.getText();
            String direction = directionDropdown.getValue();


            if (selectedCurrPair == null || quantityText.isEmpty() || direction == null) {
                resultLabel.setText("Please fill in all fields.");
                return;
            }


            double quantity;
            try {
                quantity = Double.parseDouble(quantityText);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Invalid quantity.");
                return;
            }

            try {

                String action = (direction.equals("Buy")) ? "Buying" : "Selling";
                resultLabel.setText(String.format("Opening %s position for %s with quantity %.3f", action, selectedCurrPair, quantity));



            } catch (Exception ex) {
                resultLabel.setText("Failed to open position.");
                ex.printStackTrace();
            }
        });
    }



    public void positionCloseAction(ActionEvent actionEvent) {

        Stage closingStage = new Stage();
        closingStage.setTitle("Close Position");


        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Close Position");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


        Label positionIdLabel = new Label("Enter Position ID:");


        TextField positionIdField = new TextField();
        positionIdField.setPromptText("Position ID");


        Button closePositionButton = new Button("Close Position");
        closePositionButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");


        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-alignment: center;");


        mainLayout.getChildren().addAll(titleLabel, positionIdLabel, positionIdField, closePositionButton, resultLabel);


        Scene scene = new Scene(mainLayout, 400, 300);
        closingStage.setScene(scene);
        closingStage.show();


        closePositionButton.setOnAction(e -> {
            String positionId = positionIdField.getText().trim();

            if (positionId.isEmpty()) {
                resultLabel.setText("Please enter a position ID.");
                return;
            }




            Context ctx = new Context("https://api-fxpractice.oanda.com", Configuration.TOKEN);

            try {

                TradeSpecifier tradeSpecifier = new TradeSpecifier(positionId);


                TradeCloseRequest closeRequest = new TradeCloseRequest(Configuration.ACCOUNTID, tradeSpecifier);


                TradeCloseResponse closeResponse = ctx.trade.close(closeRequest);


                if (closeResponse != null) {
                    resultLabel.setText("Position closed successfully!");
                } else {
                    resultLabel.setText("Failed to close position.");
                }
            } catch (Exception ex) {
                resultLabel.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }


    public void openPositionsAction(ActionEvent actionEvent) {

        Stage openedPositionsStage = new Stage();
        openedPositionsStage.setTitle("Opened Positions");


        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));


        Label titleLabel = new Label("Opened Positions");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


        TableView<com.example.lesalonproject.model.Position> positionsTable = new TableView<>();
        positionsTable.setPrefWidth(400);


        TableColumn<Position, String> currencyPairColumn = new TableColumn<>("Currency Pair");
        currencyPairColumn.setCellValueFactory(new PropertyValueFactory<>("currencyPair"));

        TableColumn<Position, String> directionColumn = new TableColumn<>("Direction");
        directionColumn.setCellValueFactory(new PropertyValueFactory<>("direction"));

        TableColumn<Position, Double> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));


        positionsTable.getColumns().addAll(currencyPairColumn, directionColumn, quantityColumn);


        ObservableList<Position> openedPositions = FXCollections.observableArrayList(
                new Position("EUR/USD", "Buy", 1000),
                new Position("GBP/USD", "Sell", 1500),
                new Position("USD/JPY", "Buy", 2000)
        );


        positionsTable.setItems(openedPositions);


        mainLayout.getChildren().addAll(titleLabel, positionsTable);


        Scene scene = new Scene(mainLayout, 500, 350);
        openedPositionsStage.setScene(scene);
        openedPositionsStage.show();
    }



    private void loadView(String fxmlFile){
        try{
            Node view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            contentPane.getChildren().setAll(view);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
