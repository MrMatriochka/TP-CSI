# Sprint I0 — Summary

## Objectif du sprint
Setup projet + cadrage : suivi Sheets, backlog, scénarios de tests, UML/architecture, OCL v0, base CLI.

## Prévu (plan)
- T-000 → T-012 (Sprint initial : I0)

## Réalisé (Done)
- T-000 : Google Sheets (onglets + colonnes + formats)
- T-001 : Repo + README + .gitignore + 1er commit
- T-002 : Maven + JUnit5 + smoke test OK
- T-003 : CLI loop + EXIT propre
- T-004 : Standard erreurs `ERR:`
- T-005 : Mode fichier (args[0]) + mode interactif
- T-006 : NameValidator + tests JUnit
- T-011 : Planning I0..I4 dans Sheets
- T-012 : Onglet Sprints (formules vélocité “Done only”)

## WIP / Reporté (non comptabilisé dans la vélocité)
- T-007 (UML v0) : WIP → report Sprint I1 (doc plus longue que prévu)
- T-008 (OCL v0) : WIP → report Sprint I1 (alignement UML↔OCL + corrections)
- T-009 (Scénarios TS) : WIP → report Sprint I1 (harmonisation + vérif “commandes du sujet”)

## Vélocité
- Minutes planifiées : 325
- Minutes DONE : 205
- vélocité : 63% ou 0.63
- Règle : seules les tâches “Done” comptent ; WIP = 0 pour la vélocité.

## Analyse des écarts (estimations)
- Sous-estimation : tâches de documentation (UML/OCL/TS) plus longues que prévu.
- Sur-estimation : certaines tâches de setup ont été plus rapides.
- Action : ré-estimer et éventuellement découper les tâches doc en sous-tâches plus petites.

## Décisions / conventions
- Sortie standard : `OK:` / `ERR:`
- Tests d’acceptation au format mini-script 
- OCL v0 aligné UML v0 ; à mettre à jour à chaque évolution du modèle
- `Sprint initial` ajouté dans Tasks pour tracer les reports

## Risques / blocages
- Dépendance Graphviz (TRACE GRAPH) selon l’environnement.
- Nécessité de rester strict sur les commandes du sujet.

## Rétrospective
### Ce qui a bien marché
- Cadrage complet dès Sprint 0 (traçabilité exigences → tests → tâches → planning).

### À améliorer
- Mieux estimer la doc/modélisation et limiter le WIP en fin de Sprint.
- Standardiser davantage les messages pour faciliter les tests.

### Actions (Sprint I1)
- Terminer T-007/008/009 (Done) ou les découper/ré-estimer.
- Démarrer l’incrément fonctionnel : CREATE/SELECT/CREATE UE + contraintes + DISPLAY GRAPH.
