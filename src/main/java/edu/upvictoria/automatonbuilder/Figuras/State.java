package edu.upvictoria.automatonbuilder.Figuras;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class State implements Figure {
    private String symbol = "";
    private final CircleCenter mCenter;
    private final static double mRadius = 30 ;
    private final static double acceptedStrokeRadius = 34;
    private boolean isStart = false;
    private boolean isAccepted = false;
    private final List<Transition> transitionList = new ArrayList<>();

    public State(CircleCenter center) {
        this.mCenter = center;
    }

    /*****************************************
     ********** GETTERS Y SETTERS ************
     *****************************************
     */
    public CircleCenter getmCenter() {
        return mCenter;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public void addToTransitionList(Transition transition) {
        transitionList.add(transition);
    }

    public List<Transition> getTransitionList() {
        return transitionList;
    }

    public List<Transition> getOwnTransitionList() {
        List<Transition> transitionList = new ArrayList<>();
        for (Transition transition : this.transitionList) {
            if (transition.getEstadoInicio() == this){
                transitionList.add(transition);
            }
        }

        return transitionList;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /************************************************
     ********** FUNCIONES DE LA INTERFAZ ************
     ***********************************************/
    @Override
    public void draw(GraphicsContext gc) {
        if (isStart) {
            paintInitial(gc);
        }

        gc.setStroke(Color.BLACK);
        gc.strokeOval(mCenter.getX() - mRadius, mCenter.getY() - mRadius, mRadius * 2, mRadius * 2);
        gc.setFill(Color.WHITE);
        gc.fillOval(mCenter.getX() - mRadius, mCenter.getY() - mRadius, mRadius * 2, mRadius * 2);

        if(isAccepted){
            gc.setStroke(Color.BLACK);
            gc.strokeOval(mCenter.getX() - acceptedStrokeRadius, mCenter.getY() - acceptedStrokeRadius, acceptedStrokeRadius * 2, acceptedStrokeRadius* 2);
        }

        Text labelNombre = new Text(symbol);
        double textWidth = labelNombre.getLayoutBounds().getWidth();
        double textX = mCenter.getX() - (textWidth / 2);
        double textY = mCenter.getY() + 5;
        gc.setFill(Color.BLACK);
        gc.fillText(symbol, textX, textY);
    }

    private void paintInitial(GraphicsContext gc){
        double triangleSize = 20; // Tamaño del triángulo
        double x1 = mCenter.getX() - mRadius - triangleSize; // Coordenada X del vértice izquierdo
        double y1 = mCenter.getY(); // Coordenada Y del vértice izquierdo

        double x2 = x1 + triangleSize; // Coordenada X del vértice superior
        double y2 = y1 - triangleSize / 2; // Coordenada Y del vértice superior

        double x3 = x1 + triangleSize; // Coordenada X del vértice inferior
        double y3 = y1 + triangleSize / 2; // Coordenada Y del vértice inferior

        gc.setFill(Color.BLACK); // Color del triángulo
        gc.fillPolygon(new double[]{x1, x2, x3}, new double[]{y1, y2, y3}, 3);
    }

    @Override
    public void move(double deltaX, double deltaY){
        this.mCenter.setmX(deltaX);
        this.mCenter.setmY(deltaY);

        for (Transition transition : transitionList) {
            transition.changeCoords(this, deltaX, deltaY);
        }
    }

    @Override
    public boolean contains(double x, double y) {
        double minX = mCenter.getX() - mRadius;
        double minY = mCenter.getY() - mRadius;
        double maxX = mCenter.getX() + mRadius;
        double maxY = mCenter.getY() + mRadius;

        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
}
