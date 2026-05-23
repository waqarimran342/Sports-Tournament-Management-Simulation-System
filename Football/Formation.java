package Football;

public enum Formation {
    F_4_3_3("4-3-3", new int[]{4, 3, 3}),
    F_4_4_2("4-4-2", new int[]{4, 4, 2}),
    F_3_5_2("3-5-2", new int[]{3, 5, 2}),
    F_5_3_2("5-3-2", new int[]{5, 3, 2}),
    F_4_2_3_1("4-2-3-1", new int[]{4, 2, 3, 1});

    private final String name;
    private final int[] distribution; // Def, Mid, Fwd

    Formation(String name, int[] distribution) {
        this.name = name;
        this.distribution = distribution;
    }

    public String getName() { return name; }
    public int[] getDistribution() { return distribution; }
}