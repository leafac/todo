package edu.jhu.cs.pl.to_do.models;

public class Item {
    private int id = 0;
    private String description = "";

    public Item(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public Item() {
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
