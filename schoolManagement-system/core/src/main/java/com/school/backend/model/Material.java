package com.school.backend.model;

public class Material {
    private int materialId;
    private String name;
    private int quantity;

    public Material() {}

    public Material(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() { return materialId; }
    public void setId(int materialId) { this.materialId = materialId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}