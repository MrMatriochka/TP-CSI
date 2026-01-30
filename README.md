# Offre de formation — Prototype (XP)

Prototype Java (console) pour modéliser une offre de formation universitaire (diplômes, UE, enseignants, affectations).

## Prérequis
- **Java 17+**
- **Maven 3.8+** 
- **Graphviz**

## Équipe
- **DENAT Valentin**
- **GONDY Dylan**
- **WAUTOT ALAN**

## Lancer
Depuis IntelliJ : lancer `Main`.

## Build & tests (Maven)
```bash
mvn test
mvn package
```
## Lancer l’application

Mode interactif (Ecrire les commande dans la console): 
```bash
java -cp target/classes fr.miage.Main
```
Mode fichier (exécution d’un script de commandes):

- Créer ou modifier un fichier script.txt (une commande par ligne / **exemple dans le fichier script.txt a la racine**), puis :
```bash
java -cp target/classes fr.miage.Main script.txt
```
