package fr.miage.app;

public record Result(boolean ok, String message) {
    public static Result ok(String msg) { return new Result(true, "OK: " + msg); }
    public static Result err(String msg) { return new Result(false, msg); }
}