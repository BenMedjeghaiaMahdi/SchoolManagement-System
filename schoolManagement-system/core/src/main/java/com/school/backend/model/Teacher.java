package com.school.backend.model;

import java.time.LocalDate;
import java.util.List;

public class Teacher {

    private int id;
    private String nationalId;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate dob;               // Date of Birth
    private String pob;                  // Place of Birth
    private String family_situation;
    private LocalDate workStartDate;
    private String photoPath;
    private List<Integer> subjectIds;    // IDs of subjects the teacher teaches

    public Teacher() {}

    public Teacher(int id, String nationalId, String firstName, String lastName, String phone,
                   LocalDate dob, String pob, String family_situation, LocalDate workStartDate,
                   String photoPath, List<Integer> subjectIds) {
        this.id = id;
        this.nationalId = nationalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.dob = dob;
        this.pob = pob;
        this.family_situation = family_situation;
        this.workStartDate = workStartDate;
        this.photoPath = photoPath;
        this.subjectIds = subjectIds;
    }

    // ==================== Getters & Setters ====================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getPob() { return pob; }
    public void setPob(String pob) { this.pob = pob; }

    public String getFamilySituation() { return family_situation; }
    public void setFamilySituation(String familySituation) { this.family_situation = family_situation; }

    public LocalDate getWorkStartDate() { return workStartDate; }
    public void setWorkStartDate(LocalDate workStartDate) { this.workStartDate = workStartDate; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public List<Integer> getSubjectIds() { return subjectIds; }
    public void setSubjectIds(List<Integer> subjectIds) { this.subjectIds = subjectIds; }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + nationalId + ")";
    }
}
