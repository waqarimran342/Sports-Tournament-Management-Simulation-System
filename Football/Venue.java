package Football;

import java.io.Serializable;

public class Venue implements Serializable {
    private String name;
    private String city;
    private int capacity;

    public Venue(String name, String city, int capacity) {
        this.name = name;
        this.city = city;
        this.capacity = capacity;
    }

    public String getName() { return name; }
    public String getCity() { return city; }
    public int getCapacity() { return capacity; }

    @Override
    public String toString() {
        return String.format("%s (%s) - Capacity: %,d", name, city, capacity);
    }
}