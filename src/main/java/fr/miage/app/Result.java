package fr.miage.app;

//On standardise les messages de sorties
public record Result(boolean ok, String message) {
    public static Result ok(String msg) { return new Result(true, "OK: " + msg); }
    public static Result err(String msg) { return new Result(false, "ERR: " + msg); }
}