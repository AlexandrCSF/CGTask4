module com.cgvsu {
    requires javafx.controls;
    requires javafx.fxml;
    requires vecmath;
    requires java.desktop;
    requires junit;
    requires org.junit.jupiter.api;


    opens com.cgvsu to javafx.fxml;
    exports com.cgvsu;
    exports com.cgvsu.render_engine;
    opens com.cgvsu.render_engine to javafx.fxml;
}