package com.pvz.game;

import java.time.Instant;

/** Planta colocada en el tablero. Fila y columna empiezan en 1. */
public class Plant {
    private final PlantType type;
    private final int row;
    private final int col;
    private Instant nextSunAt; // solo girasoles

    public Plant(PlantType type, int row, int col, Instant nextSunAt) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.nextSunAt = nextSunAt;
    }

    public PlantType type() { return type; }
    public int row() { return row; }
    public int col() { return col; }
    public Instant nextSunAt() { return nextSunAt; }
    public void nextSunAt(Instant value) { this.nextSunAt = value; }
}
