package com.example.lesalonproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;

import com.example.lesalonproject.service.MNBArfolyamServiceSoap;
import com.example.lesalonproject.service.MNBArfolyamServiceSoapImpl;

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


    public void accountInformationAction(){
        loadView("accountInfo.fxml");
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

    private void loadView(String fxmlFile){
        try{
            Node view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            contentPane.getChildren().setAll(view);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
