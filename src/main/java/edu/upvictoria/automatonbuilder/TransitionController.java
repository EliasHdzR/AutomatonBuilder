package edu.upvictoria.automatonbuilder;

import edu.upvictoria.automatonbuilder.Figuras.Transition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class TransitionController {
    private final Transition transicion;
    private final Stage stage;
    private final BuilderController controller;

    @FXML private RadioButton radioButtonA;
    @FXML private RadioButton radioButtonB;
    @FXML public Button btnGuardar;

    public TransitionController(Transition transicion, Stage stage, BuilderController controller) {
        this.transicion = transicion;
        this.stage = stage;
        this.controller = controller;
    }

    @FXML
    public void initialize() {
        if(transicion.isEntradaA()){
            radioButtonA.setSelected(true);
        }
        if(transicion.isEntradaB()){
            radioButtonB.setSelected(true);
        }
    }

    @FXML
    public void guardarDatos() {
        transicion.setEntradaA(radioButtonA.isSelected());
        transicion.setEntradaB(radioButtonB.isSelected());
        controller.drawShapes();
    }

    public void requestFocus() {
        stage.toFront();
        stage.requestFocus();
    }
}
