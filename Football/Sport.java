package Football;

public enum Sport {
    FOOTBALL("Football", 11, new String[]{"Goalkeeper", "Defender", "Midfielder", "Forward"});

    private final String name;
    private final int playersPerTeam;
    private final String[] positions;

    Sport(String name, int playersPerTeam, String[] positions) {
        this.name = name;
        this.playersPerTeam = playersPerTeam;
        this.positions = positions;
    }

    public String getName() { return name; }
    public int getPlayersPerTeam() { return playersPerTeam; }
    public String[] getPositions() { return positions; }
}