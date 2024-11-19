package edu.upvictoria.automatonbuilder;

import edu.upvictoria.automatonbuilder.Figuras.State;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class StateController {
    private final State estado;
    private final Stage stage;
    private final BuilderController controller;

    @FXML private CheckBox checkBoxInicio;
    @FXML private CheckBox checkBoxAceptado;
    @FXML private TextField nombreEstado;
    @FXML public Button btnGuardar;

    public StateController(State estado, Stage stage, BuilderController controller) {
        this.estado = estado;
        this.stage = stage;
        this.controller = controller;
    }

    @FXML
    private void initialize() {
        nombreEstado.setText(estado.getSymbol());
        if(estado.isAccepted()){
            checkBoxAceptado.setSelected(true);
        }

        if(estado.isStart()){
            checkBoxInicio.setSelected(true);
        }

        stage.setAlwaysOnTop(true);
    }

    @FXML
    private void guardarDatos(){
        estado.setSymbol(nombreEstado.getText());
        estado.setAccepted(checkBoxAceptado.isSelected());
        estado.setStart(checkBoxInicio.isSelected());
        controller.drawShapes();
    }

    public void requestFocus() {
        stage.toFront();
        stage.requestFocus();
    }
}
