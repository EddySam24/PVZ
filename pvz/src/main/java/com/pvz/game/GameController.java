package com.pvz.game;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {

    public record SelectRequest(PlantType plant) {}
    public record PlaceRequest(int row, int col) {}
    public record PendingItem(String id, String title, int sp, String status) {}

    private final GameService game;

    public GameController(GameService game) {
        this.game = game;
    }

    @GetMapping("/game")
    public GameSnapshot state() { return game.snapshot(); }

    @PostMapping("/game/new")
    public ActionResult newGame() { return game.newGame(); }

    @PostMapping("/game/select")
    public ActionResult select(@RequestBody SelectRequest req) { return game.select(req.plant()); }

    @PostMapping("/game/place")
    public ActionResult place(@RequestBody PlaceRequest req) { return game.place(req.row(), req.col()); }

    /** Alcance pendiente transparente: estado real, nada se cuenta como aceptado. */
    @GetMapping("/pending")
    public List<PendingItem> pending() {
        return List.of(
                new PendingItem("HU-05", "Disparos del lanzaguisantes", 8, "PENDIENTE"),
                new PendingItem("HU-06", "Zombies avanzando por su fila", 8, "PENDIENTE"),
                new PendingItem("HU-07", "Ataque de zombies a plantas", 6, "PENDIENTE"),
                new PendingItem("HU-08", "Tres oleadas (5, 8 y 12 zombies)", 8, "PENDIENTE"));
    }
}
