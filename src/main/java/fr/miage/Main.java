package fr.miage;

import fr.miage.app.CommandExecutor;
import fr.miage.app.Result;
import fr.miage.service.OfferService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        OfferService offerService = new OfferService();
        CommandExecutor executor = new CommandExecutor(offerService);

        // Mode fichier si args[0] est fourni, sinon mode interactif
        if (args.length >= 1) {
            runFile(args[0], executor);
        } else {
            runInteractive(executor);
        }
    }

    private static void runInteractive(CommandExecutor executor) throws IOException {
        System.out.println("OK: Formation Manager started. Type EXIT to quit.");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("> ");
                String line = br.readLine();
                if (line == null) break; // EOF (Ctrl+D)
                if (line.isBlank()) continue;

                // EXIT : on quitte proprement
                if (line.trim().equalsIgnoreCase("EXIT")) {
                    System.out.println("OK: Bye.");
                    break;
                }

                Result result = executor.executeLine(line);
                System.out.println(result.message());
            }
        }
    }

    private static void runFile(String path, CommandExecutor executor) throws IOException {
        System.out.println("OK: Running commands from file: " + path);

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                // On affiche la commande pour le debug / traçabilité
                System.out.println("> " + trimmed);

                if (trimmed.equalsIgnoreCase("EXIT")) {
                    System.out.println("OK: Bye.");
                    break;
                }

                Result result = executor.executeLine(trimmed);
                System.out.println(result.message());
            }
        }
    }
}