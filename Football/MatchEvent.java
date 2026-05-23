package Football;

import java.io.Serializable;

public class MatchEvent implements Serializable {
    private int minute;
    private EventType type;
    private Player player;
    private Player assistPlayer; // optional
    private String description;

    public enum EventType {
        GOAL, OWN_GOAL, YELLOW_CARD, RED_CARD, SAVE, PENALTY_MISS, SUBSTITUTION
    }

    public MatchEvent(int minute, EventType type, Player player, String description) {
        this.minute = minute;
        this.type = type;
        this.player = player;
        this.description = description;
    }

    public MatchEvent(int minute, EventType type, Player player, Player assistPlayer, String description) {
        this(minute, type, player, description);
        this.assistPlayer = assistPlayer;
    }

    // Getters
    public int getMinute() { return minute; }
    public EventType getType() { return type; }
    public Player getPlayer() { return player; }
    public Player getAssistPlayer() { return assistPlayer; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("[%d'] %s", minute, description);
    }
}