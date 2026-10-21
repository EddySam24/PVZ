package com.pvz.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    /** Reloj manual para simular el paso del tiempo sin esperar. */
    static class MutableClock extends Clock {
        private Instant now = Instant.parse("2026-10-07T12:00:00Z");
        void advance(long seconds) { now = now.plusSeconds(seconds); }
        void advanceMillis(long ms) { now = now.plusMillis(ms); }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }

    private MutableClock clock;
    private GameService game;

    @BeforeEach
    void setUp() {
        clock = new MutableClock();
        game = new GameService(clock);
    }

    // ---------- HU-01 ----------
    @Test
    void hu01_boardHasExactly15Cells() {
        GameSnapshot s = game.snapshot();
        assertEquals(3, s.rows());
        assertEquals(5, s.cols());
        assertEquals(15, s.rows() * s.cols());
    }

    @Test
    void hu01_rowColumnMappingIsStable() {
        game.select(PlantType.SUNFLOWER);
        assertTrue(game.place(1, 1).ok());
        clock.advance(20);
        assertTrue(game.place(3, 5).ok());
        var plants = game.snapshot().plants();
        assertTrue(plants.stream().anyMatch(p -> p.row() == 1 && p.col() == 1));
        assertTrue(plants.stream().anyMatch(p -> p.row() == 3 && p.col() == 5));
    }

    @Test
    void hu01_outOfRangeCellRejected() {
        game.select(PlantType.SUNFLOWER);
        assertFalse(game.place(4, 1).ok());
        assertFalse(game.place(1, 6).ok());
        assertFalse(game.place(0, 0).ok());
        assertEquals(50, game.snapshot().suns());
    }

    // ---------- HU-02 ----------
    @Test
    void hu02_newGameStartsWith50Suns() {
        assertEquals(50, game.snapshot().suns());
    }

    @Test
    void hu02_newGameResetsBalanceAndBoard() {
        game.select(PlantType.SUNFLOWER);
        game.place(2, 2);
        clock.advance(30);
        game.newGame();
        GameSnapshot s = game.snapshot();
        assertEquals(50, s.suns());
        assertTrue(s.plants().isEmpty());
        clock.advance(30); // el girasol anterior no debe seguir generando
        assertEquals(50, game.snapshot().suns());
    }

    // ---------- HU-03 ----------
    @Test
    void hu03_inventoryShowsCosts() {
        var inv = game.snapshot().inventory();
        assertEquals(50, inv.stream().filter(i -> i.id().equals("SUNFLOWER")).findFirst().orElseThrow().cost());
        assertEquals(100, inv.stream().filter(i -> i.id().equals("PEASHOOTER")).findFirst().orElseThrow().cost());
    }

    @Test
    void hu03_selectionChangesPendingPurchase() {
        game.select(PlantType.SUNFLOWER);
        assertEquals("SUNFLOWER", game.snapshot().selectedPlant());
        game.select(PlantType.PEASHOOTER);
        assertEquals("PEASHOOTER", game.snapshot().selectedPlant());
    }

    @Test
    void hu03_placeWithoutSelectionRejected() {
        ActionResult r = game.place(1, 1);
        assertFalse(r.ok());
        assertEquals(50, r.state().suns());
    }

    // ---------- HU-04 ----------
    @Test
    void hu04_sunflowerCosts50AndGenerates25Every10Seconds() {
        game.select(PlantType.SUNFLOWER);
        assertTrue(game.place(2, 3).ok());
        assertEquals(0, game.snapshot().suns());
        clock.advanceMillis(9_900);
        assertEquals(0, game.snapshot().suns());
        clock.advanceMillis(100);          // t = 10 s
        assertEquals(25, game.snapshot().suns());
        clock.advance(10);                 // t = 20 s
        assertEquals(50, game.snapshot().suns());
    }

    @Test
    void hu04_occupiedCellRejectedAndNotCharged() {
        game.select(PlantType.SUNFLOWER);
        game.place(1, 1);
        clock.advance(20);                 // saldo 50
        ActionResult r = game.place(1, 1);
        assertFalse(r.ok());
        assertEquals("Celda ocupada.", r.message());
        assertEquals(50, r.state().suns());
        assertEquals(1, r.state().plants().size());
    }

    // ---------- BUG-01 ----------
    @Test
    void bug01_peashooterWith50SunsIsRejectedAndBalanceStays50() {
        game.select(PlantType.PEASHOOTER);
        ActionResult r = game.place(2, 3);
        assertFalse(r.ok());
        assertEquals("Soles insuficientes", r.message());
        assertEquals(50, r.state().suns());
        assertTrue(r.state().plants().isEmpty());
    }

    @Test
    void bug01_exactBalanceOf50BuysSunflower() {
        game.select(PlantType.SUNFLOWER);
        ActionResult r = game.place(1, 2);
        assertTrue(r.ok());
        assertEquals(0, r.state().suns());
    }

    @Test
    void bug01_balanceNeverNegative() {
        game.select(PlantType.SUNFLOWER);
        game.place(1, 1);                  // saldo 0
        ActionResult r = game.place(1, 2); // sin saldo
        assertFalse(r.ok());
        assertEquals("Soles insuficientes", r.message());
        assertEquals(0, r.state().suns());
    }

    // ---------- Alcance pendiente ----------
    @Test
    void pending_peashooterNotPlaceableEvenWithFunds() {
        game.select(PlantType.SUNFLOWER);
        game.place(1, 1);
        clock.advance(Duration.ofSeconds(40).toSeconds()); // saldo 100
        game.select(PlantType.PEASHOOTER);
        ActionResult r = game.place(2, 2);
        assertFalse(r.ok());
        assertEquals(100, r.state().suns()); // no se cobra
    }
}
