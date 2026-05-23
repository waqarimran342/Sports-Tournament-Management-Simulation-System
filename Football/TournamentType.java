package Football;

public enum TournamentType {
    ROUND_ROBIN("Round Robin"),
    DOUBLE_ROUND_ROBIN("Double Round Robin"),
    KNOCKOUT("Knockout");

    private final String displayName;

    TournamentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}