package Football;

import java.io.Serializable;
import java.util.*;

public class Team implements Serializable {
    private String name;
    private String coachName;

    private Formation formation;
    private List<Player> players;
    private Map<Position, List<Player>> playersByPosition;

    // Stats
    private int matchesPlayed;
    private int wins, draws, losses;
    private int goalsScored, goalsConceded;

    public Team(String name, String coachName) {
        this.name = name;
        this.coachName = coachName;
        this.players = new ArrayList<>();
        this.playersByPosition = new HashMap<>();
        this.formation = Formation.F_4_3_3; // default
        for (Position pos : Position.values()) {
            playersByPosition.put(pos, new ArrayList<>());
        }
    }

    public void addPlayer(Player player) {
        if (players.size() >= 11) {
            System.out.println("Team is full! Cannot add more than 11 players.");
            return;
        }
        players.add(player);
        player.setTeam(this);
        playersByPosition.get(player.getPosition()).add(player);

        // Ensure at least one goalkeeper after at least 4 players
        if (players.size() >= 4) {
            boolean hasGoalkeeper = false;
            for (Player p : players) {
                if (p.getPosition() == Position.GOALKEEPER) {
                    hasGoalkeeper = true;
                    break;
                }
            }
            if (!hasGoalkeeper) {
                Player lastPlayer = players.get(players.size() - 1);
                playersByPosition.get(lastPlayer.getPosition()).remove(lastPlayer);
                lastPlayer.setPosition(Position.GOALKEEPER);
                playersByPosition.get(Position.GOALKEEPER).add(lastPlayer);
                System.out.println("No goalkeeper found. Made last added player a GOALKEEPER.");
            }
        }
    }

// You can add a helper method like this to check if the team has the minimum players
public boolean isPlayable() {
    return players.size() >= 4;
}



    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    // Stats methods
    public void addWin() { wins++; matchesPlayed++; }
    public void addDraw() { draws++; matchesPlayed++; }
    public void addLoss() { losses++; matchesPlayed++; }
    public void addGoalsScored(int goals) { this.goalsScored += goals; }
    public void addGoalsConceded(int goals) { this.goalsConceded += goals; }

    public int getPoints() { return wins * 3 + draws; }
    public int getGoalDifference() { return goalsScored - goalsConceded; }

    // Getters
    public String getName() { return name; }
    
    public Formation getFormation() { return formation; }
    public List<Player> getPlayers() { return players; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoalsScored() { return goalsScored; }
    public int getGoalsConceded() { return goalsConceded; }
    public int getMatchesPlayed() { return matchesPlayed; }


    @Override
    public String toString() {
        return String.format("%-20s | P: %d | W: %d D: %d L: %d | GF: %d GA: %d | Pts: %d",
                name, matchesPlayed, wins, draws, losses, goalsScored, goalsConceded, getPoints());
    }

    public String getCoachName() {
        return coachName;
    }

    public Object getPlayed() {
        return matchesPlayed;
    }

    public void setCaptain(Player player) {
    }
}