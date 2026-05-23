package Football;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.*;

public class Tournament implements Serializable {
    private static final long serialVersionUID = 20251212L;

    private String name;
    private Sport sport;
    private TournamentType type;
    private List<Team> teams;
    private Map<String, Team> teamMap;
    private List<Match> allMatches;
    private List<Match> scheduledMatches;
    private List<Venue> venues;

    private transient PriorityQueue<Player> topScorers;
    private transient PriorityQueue<Player> topSaves;
    private transient PriorityQueue<Player> momCandidates;

    public Tournament(String name, Sport sport, TournamentType type) {
        this.name = name;
        this.sport = sport;
        this.type = type;
        this.teams = new ArrayList<>();
        this.teamMap = new HashMap<>();
        this.allMatches = new ArrayList<>();
        this.scheduledMatches = new ArrayList<>();
        this.venues = new ArrayList<>();
        initializeHeaps();
    }

    private void initializeHeaps() {
        topScorers = new PriorityQueue<>((p1, p2) -> {
            int g = Integer.compare(p2.getGoals(), p1.getGoals());
            if (g != 0) return g;
            int a = Integer.compare(p2.getAssists(), p1.getAssists());
            if (a != 0) return a;
            return p1.getName().compareTo(p2.getName());
        });

        topSaves = new PriorityQueue<>((p1, p2) ->
                Integer.compare(p2.getSaves(), p1.getSaves()));

        momCandidates = new PriorityQueue<>((p1, p2) ->
                Double.compare(p2.getRating(), p1.getRating()));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initializeHeaps();
        for (Team team : teams) {
            for (Player p : team.getPlayers()) {
                topScorers.offer(p);
                topSaves.offer(p);
                momCandidates.offer(p);
            }
        }
    }

    public void addTeam(Team team) {
        teams.add(team);
        teamMap.put(team.getName(), team);
        team.getPlayers().forEach(p -> {
            topScorers.offer(p);
            topSaves.offer(p);
            momCandidates.offer(p);
        });
    }

    public void addVenue(Venue venue) { venues.add(venue); }

    public void printPointsTable() {
        System.out.println("\n=== POINTS TABLE ===");
        List<Team> sorted = new ArrayList<>(teams);
        sorted.sort((t1, t2) -> {
            int p1 = t1.getPoints(), p2 = t2.getPoints();
            if (p1 != p2) return Integer.compare(p2, p1);
            int gd1 = t1.getGoalDifference(), gd2 = t2.getGoalDifference();
            if (gd1 != gd2) return Integer.compare(gd2, gd1);
            return t1.getName().compareTo(t2.getName());
        });

        System.out.printf("%-20s P   W  D  L  GF  GA  GD  Pts%n", "Football.Team");
        for (Team t : sorted) {
            System.out.printf("%-20s %2d  %2d %2d %2d  %2d  %2d  %3d  %3d%n",
                    t.getName(), t.getMatchesPlayed(), t.getWins(), t.getDraws(),
                    t.getLosses(), t.getGoalsScored(), t.getGoalsConceded(),
                    t.getGoalDifference(), t.getPoints());
        }
    }

    public void printTopScorers(int limit) {
        System.out.println("\n=== TOP SCORERS ===");
        List<Player> list = new ArrayList<>(topScorers);
        for (int i = 0; i < Math.min(limit, list.size()); i++) {
            Player p = list.get(i);
            System.out.printf("%d. %s (%s) - %d goals, %d assists%n",
                    i+1, p.getName(), p.getPosition().getShortForm(), p.getGoals(), p.getAssists());
        }
    }

    public void printGoldenGlove(int limit) {
        System.out.println("\n=== GOLDEN GLOVE (Goalkeepers Only) ===");
        List<Player> gks = new ArrayList<>(topSaves);
        gks.removeIf(p -> p.getPosition() != Position.GOALKEEPER);
        int count = 0;
        for (Player p : gks) {
            if (count >= limit) break;
            System.out.printf("%d. %s - %d saves%n", ++count, p.getName(), p.getSaves());
        }
        if (count == 0) System.out.println("No saves recorded yet.");
    }

    public List<Player> getTopScorers() {
        List<Player> list = new ArrayList<>(topScorers); // topScorers is PriorityQueue
        return list.subList(0, Math.min(10, list.size()));
    }

    public void printManOfTheTournamentCandidates() {
        System.out.println("\n=== MAN OF THE TOURNAMENT CANDIDATES ===");
        List<Player> list = new ArrayList<>(momCandidates);
        for (int i = 0; i < Math.min(5, list.size()); i++) {
            Player p = list.get(i);
            System.out.printf("%d. %s - Rating: %.2f%n", i+1, p.getName(), p.getRating());
        }
    }

    public void saveTournament(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
            System.out.println("Football.Tournament saved: " + filename);
        } catch (Exception e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    public static Tournament loadTournament(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Tournament t = (Tournament) ois.readObject();
            System.out.println("Football.Tournament loaded: " + t.getName());
            return t;
        } catch (Exception e) {
            System.out.println("Load failed: " + e.getMessage());
            return null;
        }
    }

    // Getters
    public String getName() { return name; }
    public List<Team> getTeams() { return teams; }
    public List<Match> getScheduledMatches() { return scheduledMatches; }
    public List<Match> getAllMatches() { return allMatches; }
    public List<Venue> getVenues() { return venues; }
    public TournamentType getType() { return type; }






    public void updatePointsTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing rows
        model.setColumnIdentifiers(new String[]{
                "Pos", "Team", "P", "W", "D", "L", "GF", "GA", "GD", "Pts"
        });

        // Sort teams by points, then goal difference, then goals scored
        List<Team> sortedTeams = new ArrayList<>(getTeams());
        sortedTeams.sort((a, b) -> {
            if (b.getPoints() != a.getPoints()) return b.getPoints() - a.getPoints();
            if (b.getGoalDifference() != a.getGoalDifference()) return b.getGoalDifference() - a.getGoalDifference();
            return b.getGoalsScored() - a.getGoalsScored();
        });

        int position = 1;
        for (Team team : sortedTeams) {
            model.addRow(new Object[]{
                    position++,
                    team.getName(),
                    team.getPlayed(),
                    team.getWins(),
                    team.getDraws(),
                    team.getLosses(),
                    team.getGoalsScored(),
                    team.getGoalsConceded(),
                    team.getGoalDifference(),
                    team.getPoints()
            });
        }
    }

    public void updateTopScorersAndAwards(DefaultTableModel model) {
        model.setRowCount(0); // Clear rows
        model.setColumnIdentifiers(new String[]{
                "Rank", "Player", "Team", "Goals", "Assists"
        });

        // Collect all players
        List<Player> allPlayers = new ArrayList<>();
        for (Team team : getTeams()) {
            allPlayers.addAll(team.getPlayers());
        }

        // Sort by goals descending, then assists
        allPlayers.sort((a, b) -> {
            if (b.getGoals() != a.getGoals()) return b.getGoals() - a.getGoals();
            return b.getAssists() - a.getAssists();
        });

        int rank = 1;
        for (Player p : allPlayers) {
            if (p.getGoals() == 0 && p.getAssists() == 0) continue; // Skip players with no contribution
            model.addRow(new Object[]{
                    rank++,
                    p.getName(),
                    p.getTeam().getName(),
                    p.getGoals(),
                    p.getAssists()
            });
        }

        // Optional: Add Golden Glove section (clean sheets or saves)
        if (!allPlayers.isEmpty()) {
            model.addRow(new Object[]{"", "", "", "", ""}); // Spacer
            model.addRow(new Object[]{"#", "Golden Glove Candidates (Most Saves)", "", "", ""});

            allPlayers.sort((a, b) -> b.getSaves() - a.getSaves());
            int gkRank = 1;
            for (Player p : allPlayers) {
                if (p.getPosition() == Position.GOALKEEPER && p.getSaves() > 0) {
                    model.addRow(new Object[]{
                            gkRank+++ ".",
                            p.getName(),
                            p.getTeam().getName(),
                            p.getSaves() + " saves",
                            ""
                    });
                }
                if (gkRank > 5) break; // Top 5 keepers
            }
        }
    }
}