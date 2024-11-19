module edu.upvictoria.automatonbuilder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens edu.upvictoria.automatonbuilder to javafx.fxml;
    exports edu.upvictoria.automatonbuilder;
}