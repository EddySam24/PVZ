package com.pvz.game;

public record ActionResult(boolean ok, String message, GameSnapshot state) {}
