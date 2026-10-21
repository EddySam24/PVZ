package com.pvz.game;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Logica del corte minimo (HU-01 a HU-04).
 * La generacion de soles se calcula contra el reloj en cada consulta/accion,
 * por lo que no depende de hilos y es totalmente verificable con un Clock controlado.
 */
@Service
public class GameService {

    public static final int ROWS = 3;
    public static final int COLS = 5;
    public static final int INITIAL_SUNS = 50;
    public static final int SUN_PER_TICK = 25;
    public static final Duration SUN_INTERVAL = Duration.ofSeconds(10);

    private final Clock clock;
    private int suns;
    private PlantType selected;
    private Plant[][] board;

    public GameService(Clock clock) {
        this.clock = clock;
        reset();
    }

    private void reset() {
        suns = INITIAL_SUNS;
        selected = null;
        board = new Plant[ROWS][COLS];
    }

    public synchronized GameSnapshot snapshot() {
        refreshSuns();
        return buildSnapshot();
    }

    public synchronized ActionResult newGame() {
        reset();
        return new ActionResult(true, "Nueva partida: " + INITIAL_SUNS + " soles.", buildSnapshot());
    }

    /** HU-03: seleccionar una planta cambia la compra pendiente. */
    public synchronized ActionResult select(PlantType type) {
        refreshSuns();
        selected = type;
        return new ActionResult(true,
                type.displayName() + " seleccionado (costo " + type.cost() + ").", buildSnapshot());
    }

    /** HU-04 + BUG-01: compra validada, saldo nunca negativo, celda ocupada no cobra. */
    public synchronized ActionResult place(int row, int col) {
        refreshSuns();
        if (row < 1 || row > ROWS || col < 1 || col > COLS) {
            return fail("Celda inexistente: fila " + row + ", columna " + col + ".");
        }
        if (selected == null) {
            return fail("Selecciona una planta del inventario primero.");
        }
        if (board[row - 1][col - 1] != null) {
            return fail("Celda ocupada.");
        }
        if (suns < selected.cost()) {
            return fail("Soles insuficientes");
        }
        if (selected == PlantType.PEASHOOTER) {
            // Los disparos (HU-05) estan pendientes en este corte: no se cobra ni se coloca.
            return fail("Lanzaguisantes pendiente (HU-05): aun no se puede colocar.");
        }
        suns -= selected.cost();
        board[row - 1][col - 1] = new Plant(selected, row, col, clock.instant().plus(SUN_INTERVAL));
        return new ActionResult(true,
                selected.displayName() + " plantado en fila " + row + ", columna " + col + ".", buildSnapshot());
    }

    private void refreshSuns() {
        Instant now = clock.instant();
        for (Plant[] line : board) {
            for (Plant p : line) {
                if (p != null && p.type() == PlantType.SUNFLOWER) {
                    while (!now.isBefore(p.nextSunAt())) {
                        suns += SUN_PER_TICK;
                        p.nextSunAt(p.nextSunAt().plus(SUN_INTERVAL));
                    }
                }
            }
        }
    }

    private ActionResult fail(String message) {
        return new ActionResult(false, message, buildSnapshot());
    }

    private GameSnapshot buildSnapshot() {
        Instant now = clock.instant();
        List<GameSnapshot.InventoryItem> inventory = Arrays.stream(PlantType.values())
                .map(t -> new GameSnapshot.InventoryItem(
                        t.name(), t.displayName(), t.cost(), suns >= t.cost(), t == selected))
                .toList();
        List<GameSnapshot.PlantView> plants = new ArrayList<>();
        for (Plant[] line : board) {
            for (Plant p : line) {
                if (p != null) {
                    Long secs = p.type() == PlantType.SUNFLOWER
                            ? Math.max(0, (Duration.between(now, p.nextSunAt()).toMillis() + 999) / 1000) : null;
                    plants.add(new GameSnapshot.PlantView(
                            p.row(), p.col(), p.type().name(), p.type().displayName(), secs));
                }
            }
        }
        return new GameSnapshot(suns, ROWS, COLS, selected == null ? null : selected.name(), inventory, plants);
    }
}
