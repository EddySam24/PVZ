package com.pvz.game;

import java.util.List;

/** Vista inmutable del estado de la partida que consume el frontend. */
public record GameSnapshot(
        int suns,
        int rows,
        int cols,
        String selectedPlant,
        List<InventoryItem> inventory,
        List<PlantView> plants) {

    public record InventoryItem(String id, String name, int cost, boolean affordable, boolean selected) {}

    /** secondsToNextSun es null salvo en girasoles. */
    public record PlantView(int row, int col, String type, String name, Long secondsToNextSun) {}
}
