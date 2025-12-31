package com.school.backend.model;

public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String dob;
    private String pob;
    private String currentClass;
    private String photoPath;

    public Student() {}

    public Student(String firstName, String lastName, String currentClass) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentClass = currentClass;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getPob() { return pob; }
    public void setPob(String pob) { this.pob = pob; }
    public String getCurrentClass() { return currentClass; }
    public void setCurrentClass(String currentClass) { this.currentClass = currentClass; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}