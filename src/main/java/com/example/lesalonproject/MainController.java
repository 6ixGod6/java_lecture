package com.example.lesalonproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class MainController {
    @FXML
    private StackPane contentPane;

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
