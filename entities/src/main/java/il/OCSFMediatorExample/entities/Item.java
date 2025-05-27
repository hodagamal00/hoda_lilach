package il.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Item implements Serializable {
    private int id;
    private String name;
    private String category;
    private double price;
    private String description;
    private String imageUrl;

    public Item(int id, String name, String category, double price, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return String.format("Item ID %d: %s (%s) - %.2f₪", id, name, category, price);
    }
}
