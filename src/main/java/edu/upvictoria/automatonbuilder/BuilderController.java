package edu.upvictoria.automatonbuilder;

import edu.upvictoria.automatonbuilder.Figuras.CircleCenter;
import edu.upvictoria.automatonbuilder.Figuras.Figure;
import edu.upvictoria.automatonbuilder.Figuras.State;
import edu.upvictoria.automatonbuilder.Figuras.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BuilderController {
    // variables del controlador
    private Scene scene;
    private final List<Figure> figures = new ArrayList<>();
    private Double initialX = null;
    private Double initialY = null;
    private State selectedState = null;
    private StateController stateController = null;
    private TransitionController transitionController = null;

    // elementos de la gui
    @FXML private Canvas canvas;
    @FXML private ToolBar toolBar;
    @FXML private TextField cadenaInput;
    @FXML private Label labelAviso;
    @FXML private TextArea textResultado;

    @FXML
    public void initialize() {
        toolBar.setCursor(Cursor.DEFAULT);
    }

    /************************************
     **** FUNCIONES DEL BUILDER MAIN ****
     ************************************/
    @FXML
    protected void execAutomaton(){
        labelAviso.setText("");
        String cadenaEntrada = cadenaInput.getText().toLowerCase();

        if(!cadenaEntrada.matches("^[ab]+$")){
            labelAviso.setText("Cadena Inválida");
            return;
        }

        boolean validStart = false;
        State estadoInicial = null;
        for(Figure figure : figures){
            if(figure instanceof State){
                if(validStart && ((State) figure).isStart()){
                    labelAviso.setText("Hay más de un estado inicial");
                    return;
                }

                if(((State) figure).isStart()){
                    validStart = true;
                    estadoInicial = (State) figure;
                }
            }
        }

        if (!validStart) {
            labelAviso.setText("Inicio no definido");
            return;
        }

        String[] letras = cadenaEntrada.split("");
        List<List<State>> rutas = new ArrayList<>();

        // Ejecutar la evaluación del autómata
        boolean resultado = evaluaCadena(estadoInicial, letras, 0, new ArrayList<>(), rutas);

        // Imprimir las rutas y el estado final (aceptado o rechazado)
        StringBuilder resultadoFinal = new StringBuilder();
        for (List<State> ruta : rutas) {
            resultadoFinal.append("Ruta: ");
            for (State estado : ruta) {
                resultadoFinal.append(estado.getSymbol()).append(" -> ");
            }
            resultadoFinal.setLength(resultadoFinal.length() - 4); // Eliminar el último " -> "
            resultadoFinal.append(" | Resultado: ")
                    .append(ruta.getLast().isAccepted() ? "Aceptado" : "Rechazado")
                    .append("\n");
        }

        // Mostrar el resultado en la interfaz
        textResultado.setText(resultadoFinal.toString());
    }

    /**
     * Método recursivo para evaluar la cadena en un AFND.
     *
     * @param estadoActual El estado actual en el que estamos evaluando.
     * @param cadena       La cadena dividida en caracteres.
     * @param indice       El índice del carácter actual a evaluar.
     * @param rutaActual   La ruta actual recorrida.
     * @param rutas        Todas las rutas exploradas.
     * @return True si al menos una ruta acepta la cadena, False en caso contrario.
     */
    private boolean evaluaCadena(State estadoActual, String[] cadena, int indice, List<State> rutaActual, List<List<State>> rutas) {
        // Agregar el estado actual a la ruta
        rutaActual.add(estadoActual);
        // Caso base: si hemos consumido toda la cadena
        if (indice == cadena.length) {
            rutas.add(new ArrayList<>(rutaActual)); // Guardar la ruta recorrida
            return estadoActual.isAccepted(); // Verificar si estamos en un estado de aceptación
        }

        boolean resultado = false;
        String letra = cadena[indice]; // Obtener la letra actual de la cadena

        // Evaluar las transiciones desde el estado actual
        List<Transition> transicionesAPosibles = new ArrayList<>();
        List<Transition> transicionesBPosibles = new ArrayList<>();

        for (Transition transicion : estadoActual.getOwnTransitionList()) {
            // Verificar si la transición acepta la letra actual
            if (letra.equals("a") && transicion.isEntradaA()){
                transicionesAPosibles.add(transicion);
            }

            if (letra.equals("b") && transicion.isEntradaB()) {
                transicionesBPosibles.add(transicion);
            }
        }

        // Si hay más de una transición, es una bifurcación
        if (transicionesAPosibles.size() > 1) {
            // Para cada bifurcación, crear una nueva ruta
            for (Transition transicion : transicionesAPosibles) {
                List<State> nuevaRuta = new ArrayList<>(rutaActual);
                if (evaluaCadena(transicion.getEstadoFinal(), cadena, indice + 1, nuevaRuta, rutas)) {
                    resultado = true;
                }
            }
        }

        if (transicionesBPosibles.size() > 1) {
            // Para cada bifurcación, crear una nueva ruta
            for (Transition transicion : transicionesBPosibles) {
                List<State> nuevaRuta = new ArrayList<>(rutaActual);
                if (evaluaCadena(transicion.getEstadoFinal(), cadena, indice + 1, nuevaRuta, rutas)) {
                    resultado = true;
                }
            }
        }

        if (transicionesAPosibles.size() == 1) {
            // Si hay solo una transición, seguirla normalmente
            Transition transicion = transicionesAPosibles.getFirst();
            List<State> nuevaRuta = new ArrayList<>(rutaActual);
            if (evaluaCadena(transicion.getEstadoFinal(), cadena, indice + 1, nuevaRuta, rutas)) {
                resultado = true;
            }
        }

        if (transicionesBPosibles.size() == 1) {
            // Si hay solo una transición, seguirla normalmente
            Transition transicion = transicionesBPosibles.getFirst();
            List<State> nuevaRuta = new ArrayList<>(rutaActual);
            if (evaluaCadena(transicion.getEstadoFinal(), cadena, indice + 1, nuevaRuta, rutas)) {
                resultado = true;
            }
        }

        return resultado;
    }

    @FXML
    protected void setMovingShapesStatus() {
        removeHandlers();
        scene = canvas.getScene();
        canvas.setOnMouseEntered(me -> scene.setCursor(Cursor.OPEN_HAND));
        canvas.setOnMouseExited(me -> scene.setCursor(Cursor.DEFAULT));

        canvas.setOnMouseDragged(this::moveShape);
        canvas.setOnMouseReleased(this::endMoveShape);
    }

    private void moveShape(MouseEvent mouseEvent) {
        canvas.setCursor(Cursor.CLOSED_HAND);
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        Figure figura = getFigureAt(x, y);

        if (!(figura instanceof State estado)) {
            return;
        }

        estado.move(x, y);
        drawShapes();
    }

    private void endMoveShape(MouseEvent mouseEvent) {
        canvas.setCursor(Cursor.OPEN_HAND);
        setMovingShapesStatus();
    }

    @FXML
    private void setOpenFigureMenuStatus() {
        removeHandlers();
        canvas.setOnMouseEntered(me -> scene.setCursor(Cursor.HAND));
        canvas.setOnMouseExited(me -> scene.setCursor(Cursor.DEFAULT));
        canvas.setOnMouseClicked(this::openFigureMenu);
    }

    private void openFigureMenu(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        Figure figure = getFigureAt(x, y);

        if (figure == null) {
            return;
        }

        if (figure instanceof State estado) {
            openStateMenu(estado);
        }

        if (figure instanceof Transition transicion) {
            openTransitionMenu(transicion);
        }
    }

    private void openStateMenu(State estado) {
        // checamos si ya esta abierto, si lo está entonces traemos la ventana al plano principal
        if (stateController != null) {
            stateController.requestFocus();
            return;
        }

        // si no pues lo abrimos en una ventana nueva y lo agregamos a los menus abiertos
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menuEstado.fxml"));
            Stage stage = new Stage();
            StateController stateController = new StateController(estado, stage, this);
            fxmlLoader.setController(stateController);
            Scene scene = new Scene(fxmlLoader.load());
            this.stateController = stateController;

            // Eliminar el controlador de la lista cuando la ventana se cierra
            stage.setOnHidden(event -> this.stateController = null);
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
                if (event.getCode() == KeyCode.ENTER) {
                    stateController.btnGuardar.fire();
                }
            });

            stage.setTitle("Estado");
            stage.setScene(scene);
            stage.setMinWidth(346);
            stage.setMinHeight(126);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openTransitionMenu(Transition transicion) {
        if (transitionController != null) {
            transitionController.requestFocus();
            return;
        }

        // si no pues lo abrimos en una ventana nueva y lo agregamos a los menus abiertos
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menuTransicion.fxml"));
            Stage stage = new Stage();
            TransitionController transitionController = new TransitionController(transicion, stage, this);
            fxmlLoader.setController(transitionController);
            Scene scene = new Scene(fxmlLoader.load());

            this.transitionController = transitionController;
            // Eliminar el controlador de la lista cuando la ventana se cierra
            stage.setOnHidden(event -> this.transitionController = null);
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
                if (event.getCode() == KeyCode.ENTER) {
                    transitionController.btnGuardar.fire();
                }
            });

            stage.setTitle("Transicion");
            stage.setScene(scene);
            stage.setMinWidth(225);
            stage.setMinHeight(167);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void setDeleteFigureStatus() {
        removeHandlers();
        scene = canvas.getScene();
        canvas.setOnMouseEntered(me -> scene.setCursor(Cursor.HAND));
        canvas.setOnMouseExited(me -> scene.setCursor(Cursor.DEFAULT));
        canvas.setOnMouseClicked(this::eraseFigure);
    }

    private void eraseFigure(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        Figure figure = getFigureAt(x, y);

        if(figure == null) {
            return;
        }

        // si es un estado tmb hay que borrar todas las aristas que van hacia este nodo
        if (figure instanceof State estado) {
            List<Transition> nodoTransitionList = estado.getTransitionList();
            for (Transition transition : nodoTransitionList) {
                figures.remove(transition);
            }
        }

        figures.remove(figure);
        drawShapes();
        setDeleteFigureStatus();
    }

    @FXML
    protected void setDrawStateStatus() {
        removeHandlers();
        scene = canvas.getScene();
        canvas.setOnMouseEntered(me -> scene.setCursor(Cursor.CROSSHAIR));
        canvas.setOnMouseExited(me -> scene.setCursor(Cursor.DEFAULT));

        canvas.setOnMouseClicked(this::drawState);
    }

    private void drawState(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        CircleCenter circleCenter = new CircleCenter(x, y);
        State estado = new State(circleCenter);
        figures.add(estado);
        drawShapes();

        openStateMenu(estado);
    }

    @FXML
    protected void setDrawTransitionStatus() {
        removeHandlers();
        scene = canvas.getScene();
        canvas.setOnMouseEntered(me -> scene.setCursor(Cursor.CROSSHAIR));
        canvas.setOnMouseExited(me -> scene.setCursor(Cursor.DEFAULT));
        canvas.setOnMouseDragged(this::drawTransition);
        canvas.setOnMouseReleased(this::endDrawTransition);
    }

    private void drawTransition(MouseEvent mouseEvent) {
        if (initialX == null && initialY == null && selectedState == null) {
            initialX = mouseEvent.getX();
            initialY = mouseEvent.getY();

            Figure figInicial = getFigureAt(initialX, initialY);
            if (!(figInicial instanceof State)) {
                initialX = null;
                initialY = null;
                selectedState = null;
                return;
            }
            selectedState = (State) figInicial;
        }

        drawShapes();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Transition.drawDraft(gc, initialX, initialY, mouseEvent.getX(), mouseEvent.getY());
    }

    private void endDrawTransition(MouseEvent mouseEvent) {
        //recuperamos la figura en la que se dejó de mantener presionado el clic izquirdo
        Figure fig2 = getFigureAt(mouseEvent.getX(), mouseEvent.getY());

        // si esa figura no es un nodo entonces deja de dibujar el borrador de arista
        if (!(fig2 instanceof State estado2) || selectedState == null) {
            initialX = null;
            initialY = null;
            selectedState = null;
            setDrawTransitionStatus();
            drawShapes();
            return;
        }

        //creamos la arista y por cada figura en la lista:
        // 1. si la fig recuperada de la lista es una arista, tenemos que checar si entre los dos nodos elegidos
        //     ya existe una arista que los una, si sí, cancelamos el dibujado completamente
        // 2. si la fig recuperada es un nodo, checamos si es el nodo de inicio o final de la arista, si lo son
        //     entonces añadimos la arista a su lista de aristas propia
        Transition transition = new Transition(selectedState, estado2);
        for (Figure figure : figures) {
            if (figure instanceof Transition transicionTemp && transicionTemp.doesExist(selectedState, estado2)) {
                initialX = null;
                initialY = null;
                selectedState = null;
                setDrawTransitionStatus();
                drawShapes();
                return;
            }

            if (figure == selectedState || figure == estado2) {
                ((State) figure).addToTransitionList(transition);
            }
        }

        // añadimos la arista a la lista y la dibujamos, posteriormente reiniciamos el estado de dibujo
        figures.add(transition);
        drawShapes();

        initialX = null;
        initialY = null;
        selectedState = null;
        setDrawTransitionStatus();
        openTransitionMenu(transition);
    }

    /************************************
     ************** UTILES **************
     ************************************/

    /**
     * Remueve todos los handlers del canvas
     */
    private void removeHandlers() {
        canvas.setOnMouseClicked(null);
        canvas.setOnMouseDragged(null);
        canvas.setOnMouseReleased(null);
        canvas.setOnMousePressed(null);
    }

    /**
     * Redibuja todas las figuras en el canvas para actualizar sus posiciones
     * y actualiza el label inferior
     */
    public void drawShapes() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Figure figure : figures) {
            if (figure instanceof Transition) {
                figure.draw(gc);
            }
        }

        for (Figure figure : figures) {
            if (figure instanceof State) {
                figure.draw(gc);
            }
        }
    }

    /**
     * Obtiene la figura que contenga las coordenadas del evento
     * @param x Coordenada x del click
     * @param y Coordenada y del click
     * @return Una figura
     */

    private Figure getFigureAt(double x, double y) {
        for (Figure figure : figures) {
            if (figure.contains(x, y)) {
                return figure;
            }
        }

        return null;
    }
}
