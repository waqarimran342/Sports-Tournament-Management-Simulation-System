package Football;

import Animation.ConsoleFX;

import java.io.File;
import java.util.Scanner;

public class MainF {

    private static Tournament tournament = null;
    private static final Scanner sc = new Scanner(System.in);


    public static void Footballstart() {
        ConsoleFX.progressBar("Initializing Football Engine", 25, 40);
        ConsoleFX.loadingDots("Loading teams & venues", 3, 350);
        printTitle();
        while (true) {
            showStartMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> createNewTournament();
                case "2" -> loadTournament();
                case "3" -> {
                    System.out.println("\nThanks for using ⚽ Football Football.Tournament Manager!\n");
                    return; // EXIT FOOTBALL APP
                }
                default -> error("Invalid choice! Please enter 1, 2, or 3.");
            }

            if (tournament != null) showGameMenu();
        }
    }


    // =================== UI TEMPLATES ===================
    private static void printTitle() {
        System.out.println("""
                ╔════════════════════════════════════════════════════════╗
                ║        ⚽ SPORTS TOURNAMENT ORGANIZER & SIMULATION       ║
                ╚════════════════════════════════════════════════════════╝
                """);
    }

    private static void showStartMenu() {
        System.out.println("""
                ──────────────────────────────────────────────
                [1] Create New Football.Tournament
                [2] Load Saved Football.Tournament
                [3] Exit
                ──────────────────────────────────────────────
                """);
        System.out.print("➤ Select Option: ");
    }

    private static void showGameMenu() {
        while (true) {
            printHeader("TOURNAMENT DASHBOARD - " + tournament.getName().toUpperCase());
            System.out.println("Teams: " + tournament.getTeams().size() +
                    "   |   Format: " + tournament.getType().getDisplayName());
            System.out.println("─────────────────────────────────────────────");
            System.out.println("""
                    [1] Play Next Match
                    [2] View Schedule
                    [3] Points Table
                    [4] Top Scorers
                    [5] Golden Glove
                    [6] Man of the Football.Tournament
                    [7] Save Football.Tournament
                    [8] Back to Start Menu
                    [9] Exit
                    """);
            System.out.print("➤ Choose Option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> playNextMatch();
                case "2" -> printSchedule();
                case "3" -> tournament.printPointsTable();
                case "4" -> tournament.printTopScorers(10);
                case "5" -> tournament.printGoldenGlove(5);
                case "6" -> tournament.printManOfTheTournamentCandidates();
                case "7" -> saveTournament();
                case "8" -> { success("Returning to start menu..."); return; }
                case "9" -> { success("Thanks for playing! Goodbye!"); System.exit(0); }
                default -> error("Invalid option!");
            }
        }
    }

    private static void printHeader(String title) {
        String line = "═".repeat(title.length() + 6);
        System.out.println("\n╔" + line + "╗");
        System.out.println("║   " + title + "   ║");
        System.out.println("╚" + line + "╝");
    }

    private static void success(String msg) {
        System.out.println("✔ SUCCESS: " + msg);
    }

    private static void error(String msg) {
        System.out.println("✖ ERROR: " + msg);
    }

    private static String prompt(String msg) {
        System.out.print("➤ " + msg + ": ");
        return sc.nextLine();
    }

    // =================== CREATE TOURNAMENT ===================
    private static void createNewTournament() {
        String name = prompt("Enter Football.Tournament Name");
        if (name.isEmpty()) { error("Football.Tournament name cannot be empty!"); return; }

        System.out.println("\nFootball.Tournament Type:");
        System.out.println("[1] Round Robin  [2] Double Round Robin  [3] Knockout");
        String typeInput = prompt("Choose (1-3)");
        TournamentType type = switch (typeInput) {
            case "1" -> TournamentType.ROUND_ROBIN;
            case "2" -> TournamentType.DOUBLE_ROUND_ROBIN;
            case "3" -> TournamentType.KNOCKOUT;
            default -> {
                error("Invalid choice, defaulting to Round Robin.");
                yield TournamentType.ROUND_ROBIN;
            }
        };

        tournament = new Tournament(name, Sport.FOOTBALL, type);
        success("Football.Tournament '" + name + "' created successfully!");

        addVenues();
        createTeams();
        ScheduleGenerator.generateSchedule(tournament);
        success("Schedule generated! Ready to play!");
    }

    private static void addVenues() {
        printHeader("ADD VENUES");
        while (true) {
            String vName = prompt("Football.Venue Name (type 'done' to finish)");
            if (vName.equalsIgnoreCase("done")) break;
            String city = prompt("City");
            int cap = Integer.parseInt(prompt("Capacity"));
            tournament.addVenue(new Venue(vName, city, cap));
            success("Football.Venue '" + vName + "' added!");
        }
    }

    private static void createTeams() {
        printHeader("CREATE TEAMS");
        while (true) {
            String tName = prompt("Football.Team Name (type 'done' to finish)");
            if (tName.equalsIgnoreCase("done")) break;

            String coach = prompt("Coach Name");
            Team team = new Team(tName, coach.isEmpty() ? "Coach" : coach);

            // AB GALAT INPUT PE DOBARA PUCHHEGA — FORCE 11 NAHI KAREGA!
            int n = 0;
            while (n < 1 || n > 22) {
                String input = prompt("Number of players (1-22)");
                try {
                    n = Integer.parseInt(input);
                    if (n < 1 || n > 22) {
                        error("Please enter a number between 1 and 22!");
                    }
                } catch (NumberFormatException e) {
                    error("Invalid input! '" + input + "' is not a number. Try again.");
                }
            }

            for (int i = 0; i < n; i++) {
                System.out.println("\nFootball.Player " + (i + 1) + " of " + n + ":");
                String pName = prompt("   Name");
                while (pName.trim().isEmpty()) {
                    error("Name cannot be empty!");
                    pName = prompt("   Name");
                }

                int age = 0;
                while (age < 15 || age > 45) {
                    String ageInput = prompt("   Age (15-45)");
                    try {
                        age = Integer.parseInt(ageInput);
                        if (age < 15 || age > 45) error("Age must be between 15 and 45!");
                    } catch (Exception e) {
                        error("Invalid age! Enter a number.");
                        age = 0;
                    }
                }

                Position p = null;
                while (p == null) {
                    String pos = prompt("   Football.Position (gk/df/mf/fw)").toLowerCase().trim();
                    p = switch (pos) {
                        case "gk", "1", "goalkeeper" -> Position.GOALKEEPER;
                        case "df", "2", "defender" -> Position.DEFENDER;
                        case "mf", "3", "midfielder" -> Position.MIDFIELDER;
                        case "fw", "4", "forward", "striker" -> Position.FORWARD;
                        default -> {
                            error("Invalid position! Use gk/df/mf/fw or 1-4");
                            yield null;
                        }
                    };
                }

                team.addPlayer(new Player(pName, age, p));
            }

            if (!team.getPlayers().isEmpty()) {
                team.setCaptain(team.getPlayers().get(0));
                team.setFormation(Formation.F_4_3_3);
            }

            tournament.addTeam(team);
            success("Football.Team '" + tName + "' added with " + n + " player(s)!");
        }

        // Minimum 2 teams check
        if (tournament.getTeams().size() < 2) {
            error("You need at least 2 teams to start a tournament!");
            createTeams(); // Doobara mangega
        }
    }

    // =================== LOAD & SAVE ===================
    private static void loadTournament() {
        String input = prompt("Enter tournament name or part of filename");
        if (input.isEmpty()) input = "Football.Tournament";

        String finalInput = input;
        File[] files = new File(".").listFiles((dir, name) ->
                name.toLowerCase().contains(finalInput.toLowerCase()) && name.endsWith(".dat"));

        if (files == null || files.length == 0) {
            error("No saved tournament found!");
            listAllSaved();
            return;
        }

        File selected = files.length == 1 ? files[0] : chooseFile(files);
        if (selected != null) {
            Tournament loaded = Tournament.loadTournament(selected.getName());
            if (loaded != null) {
                tournament = loaded;
                success("LOADED: " + tournament.getName());
                System.out.println("Teams: " + tournament.getTeams().size() +
                        " | Matches Played: " + tournament.getAllMatches().size());
            }
        }
    }

    private static void listAllSaved() {
        File[] files = new File(".").listFiles((d, n) -> n.startsWith("Tournament_") && n.endsWith(".dat"));
        if (files == null || files.length == 0) { error("No saved tournaments found."); return; }
        System.out.println("Available saves:");
        for (File f : files) System.out.println("  • " + f.getName());
    }

    private static File chooseFile(File[] files) {
        System.out.println("Multiple tournaments found:");
        for (int i = 0; i < files.length; i++) System.out.println((i + 1) + ". " + files[i].getName());
        int idx = Integer.parseInt(prompt("Choose one")) - 1;
        if (idx >= 0 && idx < files.length) return files[idx];
        error("Invalid selection."); return null;
    }

    private static void saveTournament() {
        String safeName = tournament.getName().replaceAll("[^a-zA-Z0-9]", "_");
        String filename = "Tournament_" + safeName + "_" + java.time.LocalDate.now() + ".dat";
        tournament.saveTournament(filename);
        success("Football.Tournament saved as: " + filename);
    }

    // =================== MATCHES ===================
    private static void playNextMatch() {
        if (tournament.getScheduledMatches().isEmpty()) {
            error("No more matches!");
            return;
        }

        var match = tournament.getScheduledMatches().remove(0);
        printMatchBanner(match);

        String mode = prompt("1. Auto Simulate   2. Manual Scoring");
        if (mode.equals("1")) MatchSimulator.autoSimulate(match, sc);
        else MatchSimulator.manualScoring(match, sc);

        tournament.getAllMatches().add(match);
    }

    private static void printMatchBanner(Match match) {
        String t1 = match.getCurrentTeam1().getName().toUpperCase();
        String t2 = match.getCurrentTeam2().getName().toUpperCase();
        String stadium = match.getVenue().getName();

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║               MATCH DAY               ║");
        System.out.println("╟────────────────────────────────────────╢");
        System.out.println("║ " + t1 + "  ⚔  " + t2 + "                 ║");
        System.out.println("║ Stadium: " + stadium + "                 ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void printSchedule() {
        printHeader("REMAINING SCHEDULE");

        if (tournament.getScheduledMatches().isEmpty()) {
            success("All matches completed! Football.Tournament finished!");
            return;
        }

        for (var m : tournament.getScheduledMatches()) {
            String team1Name = (m.getCurrentTeam1() != null) ? m.getCurrentTeam1().getName() : "Winner of previous match";
            String team2Name = (m.getCurrentTeam2() != null) ? m.getCurrentTeam2().getName() : "Winner of previous match";

            String venueName = m.getVenue() != null ? m.getVenue().getName() : "TBD";

            // Knockout ke liye special display
            if (m.getMatchName() != null && !m.getMatchName().isEmpty()) {
                System.out.println("• " + m.getMatchName() + ": " + team1Name + " vs " + team2Name + " @ " + venueName);
            } else {
                System.out.println("• " + team1Name + " vs " + team2Name + " @ " + venueName);
            }
        }
    }
}
