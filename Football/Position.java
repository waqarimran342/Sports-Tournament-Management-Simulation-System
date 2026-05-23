package Football;

public enum Position {
    GOALKEEPER("GK"),
    DEFENDER("DF"),
    MIDFIELDER("MF"),
    FORWARD("FW");

    private final String shortForm;

    Position(String shortForm) {
        this.shortForm = shortForm;
    }

    public String getShortForm() {
        return shortForm;
    }

    // NEW METHOD: Case-insensitive match
    public boolean matches(String input) {
        if (input == null) return false;
        return shortForm.equalsIgnoreCase(input.trim());
    }

    // Optional: Get Football.Position from string (case-insensitive)
    public static Position fromString(String text) {
        if (text == null) return null;
        for (Position p : values()) {
            if (p.shortForm.equalsIgnoreCase(text.trim())) {
                return p;
            }
        }
        return null;
    }
}