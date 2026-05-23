package Football;

import java.io.Serializable;

public class Player implements Serializable, Comparable<Player> {
    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private Position position;
    private int goals = 0;
    private int assists = 0;
    private int saves = 0;
    private int yellowCards = 0;
    private int redCards = 0;
    private double rating = 5.0;

    public Player(String name, int age, Position position) {
        this.name = name;
        this.age = age;
        this.position = position;
    }

    public void updateRating() {
        rating = 5.0 +
                goals * 1.8 +
                assists * 1.2 +
                saves * 1.0 -
                yellowCards * 0.7 -
                redCards * 3.0;
        if (rating > 10.0) rating = 10.0;
        if (rating < 4.0) rating = 4.0;
    }

    // Stats
    public void addGoal() { goals++; updateRating(); }
    public void addAssist() { assists++; updateRating(); }
    public void addSave() { saves++; updateRating(); }
    public void addYellowCard() { yellowCards++; updateRating(); }
    public void addRedCard() { redCards++; updateRating(); }

    // Getters
    public String getName() { return name; }
    public Position getPosition() { return position; }
    public int getGoals() { return goals; }
    public int getAssists() { return assists; }
    public int getSaves() { return saves; }
    public double getRating() { return rating; }

    @Override public String toString() {
        return String.format("%s [%s] - G:%d A:%d R:%.2f", name, position.getShortForm(), goals, assists, rating);
    }

    @Override
    public int compareTo(Player o) {
        return Double.compare(o.rating, this.rating);
    }


    private Team team;

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}