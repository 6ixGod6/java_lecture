module com.example.lesalonproject {
    requires javafx.controls;
    requires javafx.fxml;

    //requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires jakarta.jws;
    requires jakarta.xml.ws;
    requires jakarta.xml.bind; 
    requires org.controlsfx.controls;
    
     // Jakarta JAXB API
    //requires com.sun.xml.bind; // JAXB Runtime

//    requires org.xerial.sqlitejdbc;
//    requires org.slf4j;


    opens com.example.lesalonproject to javafx.fxml;
    exports com.example.lesalonproject;
}