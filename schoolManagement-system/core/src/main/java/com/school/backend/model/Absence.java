package com.school.backend.model;

import java.time.LocalDate;

public class Absence {

    private int id;
    private int personId;
    private LocalDate date;
    private boolean isExplanation = false;
    private LocalDate explanationDate;

    public Absence() {}

    public Absence(int id, int personId, LocalDate date, boolean isExplanation, LocalDate explanationDate) {
        this.id = id;
        this.personId = personId;
        this.date = date;
        this.isExplanation = isExplanation;
        this.explanationDate = explanationDate;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isExplanation() { return isExplanation; }
    public void setExplanation(boolean explanation) { this.isExplanation = explanation; }

    public LocalDate getExplanationDate() { return explanationDate; }
    public void setExplanationDate(LocalDate explanationDate) { this.explanationDate = explanationDate; }
}
