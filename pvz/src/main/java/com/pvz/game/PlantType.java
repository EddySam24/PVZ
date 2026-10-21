package com.pvz.game;

public enum PlantType {
    SUNFLOWER("Girasol", 50),
    PEASHOOTER("Lanzaguisantes", 100);

    private final String displayName;
    private final int cost;

    PlantType(String displayName, int cost) {
        this.displayName = displayName;
        this.cost = cost;
    }

    public String displayName() { return displayName; }
    public int cost() { return cost; }
}
