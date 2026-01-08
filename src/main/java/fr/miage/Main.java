package fr.miage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Offre de formation - CLI (type EXIT to quit)");

        BufferedReader reader;
        boolean echoPrompt = true;

        //verification d'un document texte argument
        if (args.length == 1) {
            reader = new BufferedReader(new FileReader(args[0]));
            echoPrompt = false;
        }
        else {
            reader = new BufferedReader(new InputStreamReader(System.in)); //si pas de texte passe en mode input
        }

        String line;

            while (true) {
                if (echoPrompt) System.out.print("> ");

                line = reader.readLine();
                if (line == null) break; // EOF
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equalsIgnoreCase("EXIT")) {
                    System.out.println("Bye.");
                    break;
                }

                System.out.println("ERR: Unknown command");
            }

    }
}