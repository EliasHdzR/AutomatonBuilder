package edu.upvictoria.automatonbuilder.Figuras;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Transition implements Figure {

    private final State estadoInicio;
    private final State estadoFinal;

    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private boolean entradaA = true;
    private boolean entradaB = false;

    public Transition(State estadoInicio, State estadoFinal) {
        this.estadoInicio = estadoInicio;
        this.estadoFinal = estadoFinal;
        this.startX = this.estadoInicio.getmCenter().getX();
        this.startY = this.estadoInicio.getmCenter().getY();
        this.endX = this.estadoFinal.getmCenter().getX();
        this.endY = this.estadoFinal.getmCenter().getY();
    }

    /**
     * Compara dos estados recibidos con los dos estados existentes para checar si estos estados ya
     * están unidos por una arista, así se evita dibujar más de una arista entre dos mismos estados
     *
     * @param estadoInicio un estado
     * @param estadoFinal  otro estado
     * @return true si el estado es igual a this.estado, igual con el estado2
     */
    public boolean doesExist(State estadoInicio, State estadoFinal) {
        return (estadoInicio == this.estadoInicio && estadoFinal == this.estadoFinal) || (estadoFinal == this.estadoInicio && estadoInicio == this.estadoFinal);
    }

    public void changeCoords(State estado, double x, double y) {
        if (estado == estadoInicio) {
            startX = x;
            startY = y;
        }

        if (estado == estadoFinal) {
            endX = x;
            endY = y;
        }
    }

    /*****************************************
     ********** GETTERS Y SETTERS ************
     *****************************************
     */
    public boolean isEntradaA(){
        return entradaA;
    }

    public void setEntradaA(boolean entradaA) {
        this.entradaA = entradaA;
    }

    public boolean isEntradaB(){
        return entradaB;
    }

    public void setEntradaB(boolean entradaB) {
        this.entradaB = entradaB;
    }

    /************************************************
     ********** FUNCIONES DE LA INTERFAZ ************
     ***********************************************/
    public static void drawDraft(GraphicsContext gc, double startX, double startY, double endX, double endY) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        gc.strokeLine(startX, startY, endX, endY);
        drawArrowHead(gc, startX, startY, endX, endY, 0);
    }

    @Override
    public void draw(GraphicsContext gc) {
        String text = entradaA && entradaB ? "a,b" : entradaA ? "a" : "b";
        gc.setStroke(Color.BLACK);
        double midX, midY;

        if (startX == endX && startY == endY) {
            // Dibujar un autolazo (un círculo o una curva)
            double loopRadius = 75;
            gc.setLineWidth(2);
            gc.strokeOval(startX - 5, startY - loopRadius, 15, loopRadius);

            //TODO RENDERIZAR LABEL CORRECTAMENTE
            midX = startY - loopRadius;
            midY = startX - 5;
        } else {
            gc.setLineWidth(2);
            gc.strokeLine(startX, startY, endX, endY);
            drawArrowHead(gc, startX, startY, endX, endY, 30);

            // Calcular el punto medio de la línea
            midX = (startX + endX) / 2;
            midY = (startY + endY) / 2;
        }

        // Medir el tamaño del texto para ajustar el fondo
        Text tempText = new Text(text);
        double textWidth = tempText.getLayoutBounds().getWidth();
        double textHeight = tempText.getLayoutBounds().getHeight();

        // Dibujar el rectángulo amarillo de fondo
        double padding = 8;
        gc.setFill(Color.YELLOW);
        gc.fillRect(midX - textWidth / 2 - padding, midY - textHeight / 2 - padding, textWidth + 2 * padding, textHeight + 2 * padding);

        // Dibujar el texto encima del rectángulo
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 18));
        gc.fillText(text, midX - textWidth / 2, midY + textHeight / 4);
    }

    private static void drawArrowHead(GraphicsContext gc, double startX, double startY, double endX, double endY, double radius) {
        // Calcular el ángulo de la línea
        double angle = Math.atan2(endY - startY, endX - startX);

        // Ajustar el punto final para que la línea termine en la circunferencia
        double adjustedEndX = endX - radius * Math.cos(angle);
        double adjustedEndY = endY - radius * Math.sin(angle);

        // Longitud de las líneas de la cabeza de la flecha
        double arrowLength = 10;
        double arrowWidth = 5;

        // Calcular los puntos de la cabeza de la flecha
        double x1 = adjustedEndX - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = adjustedEndY - arrowLength * Math.sin(angle - Math.PI / 6);

        double x2 = adjustedEndX - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = adjustedEndY - arrowLength * Math.sin(angle + Math.PI / 6);

        // Dibujar las líneas de la cabeza de la flecha
        gc.strokeLine(adjustedEndX, adjustedEndY, x1, y1);
        gc.strokeLine(adjustedEndX, adjustedEndY, x2, y2);
    }

    @Override
    public void move(double deltaX, double deltaY) {
        //jiji no se mueve desde aquí, checa la clase State
    }

    @Override
    public boolean contains(double x, double y) {
        double distance = pointLineDistance(startX, startY, endX, endY, x, y);
        return distance < 5;
    }

    // Magia
    private double pointLineDistance(double x1, double y1, double x2, double y2, double px, double py) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) {
            param = dot / len_sq;
        }

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
