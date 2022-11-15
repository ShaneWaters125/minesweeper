module com.example.minesweepergui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.minesweepergui to javafx.fxml;
    exports com.example.minesweepergui;
}