package Football;

import java.util.*;

public class ScheduleGenerator {

    public static void generateSchedule(Tournament tournament) {
        List<Team> teams = tournament.getTeams();
        List<Venue> venues = tournament.getVenues();
        TournamentType type = tournament.getType();

        if (teams.size() < 2) {
            System.out.println("Not enough teams!");
            return;
        }

        Random rand = new Random();
        Date baseDate = new Date(); // Start from today
        long oneDay = 24 * 60 * 60 * 1000L;

        if (type == TournamentType.ROUND_ROBIN || type == TournamentType.DOUBLE_ROUND_ROBIN) {
            generateRoundRobinSchedule(tournament, teams, venues, baseDate, oneDay, rand, type == TournamentType.DOUBLE_ROUND_ROBIN);
        } else if (type == TournamentType.KNOCKOUT) {
            generateKnockoutSchedule(tournament, teams, venues, baseDate, rand);
        }
    }

    // ROUND ROBIN (Classic Algorithm with Rotation - Circular LinkedList Logic)
    private static void generateRoundRobinSchedule(Tournament tournament, List<Team> teams,
                                                   List<Venue> venues, Date baseDate, long oneDay,
                                                   Random rand, boolean doubleRR) {
        int n = teams.size();
        List<Team> rotated = new ArrayList<>(teams);
        rotated.add(teams.get(0)); // Fix one team, rotate others

        List<Match> matches = new ArrayList<>();
        int matchDay = 1;

        for (int round = 1; round < n; round++) {
            for (int i = 0; i < n / 2; i++) {
                Team home = rotated.get(i);
                Team away = rotated.get(n - 1 - i);

                if (home != away) {
                    Venue venue = venues.get(rand.nextInt(venues.size()));
                    Date date = new Date(baseDate.getTime() + (matchDay - 1) * oneDay);

                    // No two matches for same team on same day → already satisfied by algorithm
                    matches.add(new Match(home, away, venue, date));

                    if (doubleRR) {
                        // Return leg
                        Date returnDate = new Date(baseDate.getTime() + (matchDay + n - 1) * oneDay);
                        matches.add(new Match(away, home, venue, returnDate));
                    }
                }
            }

            // Rotate (Circular LinkedList style)
            Team temp = rotated.remove(1);
            rotated.add(rotated.size() - 1, temp);

            matchDay++;
        }

        tournament.getScheduledMatches().addAll(matches);
        System.out.println("Round Robin Schedule Generated: " + matches.size() + " matches");
    }

    private static void generateKnockoutSchedule(
            Tournament tournament,
            List<Team> teams,
            List<Venue> venues,
            Date baseDate,
            Random rand) {

        if (teams.size() < 2) return;

        Collections.shuffle(teams, rand);

        List<Match> allMatches = new ArrayList<>();
        Queue<Match> currentRound = new LinkedList<>();

        // ===== FIRST ROUND (Quarter / Round of 16) =====
        for (int i = 0; i < teams.size(); i += 2) {
            Venue venue = venues.get(rand.nextInt(venues.size()));
            Date date = new Date(baseDate.getTime() + i * 86400000L);

            Match match = new Match(teams.get(i), teams.get(i + 1), venue, date);
            match.setMatchName("Match  " + (i / 2 + 1));

            allMatches.add(match);
            currentRound.offer(match);
        }

        int round = 1;

        // ===== NEXT ROUNDS =====
        while (currentRound.size() > 1) {
            Queue<Match> nextRound = new LinkedList<>();
            int matchNo = 1;

            while (!currentRound.isEmpty()) {
                Match m1 = currentRound.poll();
                Match m2 = currentRound.poll();

                Venue venue = venues.get(rand.nextInt(venues.size()));
                Date date = new Date(baseDate.getTime() + (round + 5) * 86400000L);

                Match nextMatch = new Match(null, null, venue, date);

                if (round == 1)
                    nextMatch.setMatchName("Semi Final " + matchNo++);
                else
                    nextMatch.setMatchName("Final");

                m1.setWinnerGoesTo(nextMatch);
                m2.setWinnerGoesTo(nextMatch);

                allMatches.add(nextMatch);
                nextRound.offer(nextMatch);
            }

            currentRound = nextRound;
            round++;
        }

        tournament.getScheduledMatches().addAll(allMatches);
    }


    // Extension in Football.Match class needed for knockout winner propagation
    // Add this field in Football.Match.java:
    // private Football.Match winnerGoesTo;
    // public void setWinnerGoesTo(Football.Match next) { this.winnerGoesTo = next; }
    // public Football.Match getWinnerGoesTo() { return winnerGoesTo; }
    // And in finishMatch(): if(winnerGoesTo != null) winnerGoesTo.setTeam(winningTeam);
}