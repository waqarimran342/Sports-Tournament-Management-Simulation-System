package Football;

import java.util.Random;
import java.util.Scanner;

public class MatchSimulator {

    private static final String[] goalComments = {
            "What a strike!", "GOAL! Unbelievable!", "The net bulges!", "Clinical finish!",
            "Thunderbolt from %s!", "Curled into the top corner!"
    };
    private static final String[] saveComments = {"Great save by the keeper!", "Denied!", "Superb stop!"};
    private static final String[] cardComments = {"That's a booking!", "Harsh yellow card.", "Straight red!"};

    public static void autoSimulate(Match match, Scanner sc) {
        // 🔒 Knockout safety
        if (match.getCurrentTeam1() == null || match.getCurrentTeam2() == null) {
            System.out.println("Football.Match not ready yet. Waiting for previous results.");
            return;
        }

        Random rand = new Random();
        System.out.println("\n=== AUTO SIMULATION STARTED ===");
        System.out.println(match.getMatchName());
        System.out.println("Press Enter to continue minute by minute, or type 'undo' to undo last event\n");

        for (int minute = 1; minute <= 90; minute += rand.nextInt(8) + 1) {
            if (minute > 90) minute = 90;

            System.out.println("\n--- " + minute + "' ---");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("undo")) {
                match.undoLastEvent();
                continue;
            }

            int eventsThisMinute = rand.nextInt(3);
            for (int i = 0; i < eventsThisMinute; i++) {
                simulateRandomEvent(match, rand, minute);
            }

            System.out.println(match);
            if (match.getManOfTheMatch() != null) {
                System.out.println("Current MOM: " + match.getManOfTheMatch().getName());
            }
        }

        match.finishMatch();
        System.out.println("\nFULL TIME!");
        System.out.println(match);
        if (match.getManOfTheMatch() != null) {
            System.out.println("Man of the Football.Match: " + match.getManOfTheMatch().getName());
        }
        replayHighlights(match);
    }

    private static void simulateRandomEvent(Match match, Random rand, int minute) {
        Team team1 = match.getCurrentTeam1();
        Team team2 = match.getCurrentTeam2();
        if (team1 == null || team2 == null) return;

        Team attackingTeam = rand.nextBoolean() ? team1 : team2;
        Team defendingTeam = attackingTeam == team1 ? team2 : team1;

        Player attacker = getRandomPlayer(attackingTeam, "FW", "MF");
        Player assist = rand.nextBoolean() ? getRandomPlayer(attackingTeam, "MF", "DF") : null;
        Player gk = getRandomPlayer(defendingTeam, "GK");

        if (attacker == null || gk == null) return;

        int eventType = rand.nextInt(100);
        String desc;

        if (eventType < 55) {
            if (rand.nextInt(100) < 25) {
                desc = goalComments[rand.nextInt(goalComments.length)]
                        .replace("%s", attacker.getName());
                match.addEvent(new MatchEvent(minute, MatchEvent.EventType.GOAL, attacker, assist, desc));
                System.out.println(desc);
            } else if (rand.nextInt(100) < 40) {
                desc = saveComments[rand.nextInt(saveComments.length)];
                match.addEvent(new MatchEvent(minute, MatchEvent.EventType.SAVE, gk, desc));
                System.out.println(desc);
            }
        } else if (eventType < 80) {
            Player player = rand.nextBoolean() ? attacker : getRandomPlayer(defendingTeam);
            if (player == null) return;

            MatchEvent.EventType type =
                    rand.nextBoolean() ? MatchEvent.EventType.YELLOW_CARD : MatchEvent.EventType.RED_CARD;

            desc = cardComments[type == MatchEvent.EventType.YELLOW_CARD ? 0 : 2];
            match.addEvent(new MatchEvent(minute, type, player, desc));
            System.out.println(desc);
        }
    }

    private static Player getRandomPlayer(Team team, String... positions) {
        if (team == null || team.getPlayers().isEmpty()) return null;

        java.util.List<Player> pool = new java.util.ArrayList<>(team.getPlayers());

        if (positions != null && positions.length > 0) {
            pool.removeIf(p -> {
                for (String pos : positions) {
                    if (p.getPosition().getShortForm().equalsIgnoreCase(pos))
                        return false;
                }
                return true;
            });
        }

        return pool.isEmpty()
                ? team.getPlayers().get(0)
                : pool.get(new Random().nextInt(pool.size()));
    }

    public static void manualScoring(Match match, Scanner sc) {
        if (match.getCurrentTeam1() == null || match.getCurrentTeam2() == null) {
            System.out.println("Football.Match not ready yet.");
            return;
        }

        System.out.println("\n=== MANUAL SCORING ===");
        System.out.println(match.getMatchName());

        while (true) {
            System.out.println("\nCurrent: " + match);
            System.out.println("1. Goal Team1  2. Goal Team2  3. Undo  4. Finish");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                Player scorer = selectPlayer(match.getCurrentTeam1(), sc);
                Player assist = selectPlayer(match.getCurrentTeam1(), sc, "Assist (or press Enter to skip)");
                match.addEvent(new MatchEvent(0, MatchEvent.EventType.GOAL, scorer, assist, scorer.getName() + " scores!"));
            } else if (choice.equals("2")) {
                Player scorer = selectPlayer(match.getCurrentTeam2(), sc);
                Player assist = selectPlayer(match.getCurrentTeam2(), sc, "Assist (or press Enter to skip)");
                match.addEvent(new MatchEvent(0, MatchEvent.EventType.GOAL, scorer, assist, scorer.getName() + " scores!"));
            } else if (choice.equals("3")) {
                match.undoLastEvent();
            } else if (choice.equals("4")) {
                match.finishMatch();
                System.out.println("Football.Match Finished!");
                break;
            }
        }
    }

    private static Player selectPlayer(Team team, Scanner sc) {
        return selectPlayer(team, sc, "Select player");
    }

    private static Player selectPlayer(Team team, Scanner sc, String prompt) {
        if (team == null) return null;

        System.out.println(prompt + " from " + team.getName() + ":");
        for (int i = 0; i < team.getPlayers().size(); i++) {
            System.out.println((i + 1) + ". " + team.getPlayers().get(i).getName());
        }
        int idx = Integer.parseInt(sc.nextLine()) - 1;
        return team.getPlayers().get(idx);
    }

    private static void replayHighlights(Match match) {
        System.out.println("\n=== HIGHLIGHTS ===");
        match.getAllEvents().stream()
                .filter(e -> e.getType() == MatchEvent.EventType.GOAL)
                .forEach(System.out::println);
    }
}
