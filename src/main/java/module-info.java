module edu.upvictoria.automatonbuilder {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.upvictoria.automatonbuilder to javafx.fxml;
    exports edu.upvictoria.automatonbuilder;
}