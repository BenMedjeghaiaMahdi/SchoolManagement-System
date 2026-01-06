package com.school.backend.model;

public class Material {
    private int id;
    private String name;
    private String model;
    private int quantity;
    private String category;
    private String status;
    private String photoPath;

    public Material() {}

    public Material(String name, int quantity, String model, String status, String category) {
        this.name = name;
        this.quantity = quantity;
        this.model = model;
        this.status = status;
        this.category = category;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}
