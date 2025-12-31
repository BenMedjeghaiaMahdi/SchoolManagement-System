package com.school.backend.model;

import java.time.LocalDateTime;

public class MaterialLog {

    private int id;
    private int materialId;
    private String action;
    private int quantityChange;
    private int quantityBefore;
    private int quantityAfter;
    private int userId;
    private LocalDateTime logDate;

    public MaterialLog() {}

    public MaterialLog(
            int materialId,
            String action,
            int quantityChange,
            int quantityBefore,
            int quantityAfter,
            int userId
    ) {
        this.materialId = materialId;
        this.action = action;
        this.quantityChange = quantityChange;
        this.quantityBefore = quantityBefore;
        this.quantityAfter = quantityAfter;
        this.userId = userId;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public int getQuantityChange() { return quantityChange; }
    public void setQuantityChange(int quantityChange) { this.quantityChange = quantityChange; }

    public int getQuantityBefore() { return quantityBefore; }
    public void setQuantityBefore(int quantityBefore) { this.quantityBefore = quantityBefore; }

    public int getQuantityAfter() { return quantityAfter; }
    public void setQuantityAfter(int quantityAfter) { this.quantityAfter = quantityAfter; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDateTime getLogDate() { return logDate; }
    public void setLogDate(LocalDateTime logDate) { this.logDate = logDate; }

}
