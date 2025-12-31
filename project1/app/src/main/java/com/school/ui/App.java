package com.school.ui;

import com.school.backend.model.*;
import com.school.backend.service.SchoolService;

import java.time.LocalDate;
import java.util.Arrays;

public class App {

    public static void main(String[] args) {

        SchoolService service = SchoolService.getInstance();
        System.out.println("Starting School Management System Test...\n");

        // ==========================================
        // TEST 1: Authentication
        // ==========================================
        System.out.println("--- 1. Testing Login ---");
        if (service.login("admin@school.com", "admin123")) {
            User user = service.getCurrentUser();
            System.out.println("Login Successful!");
            System.out.println("User: " + user.getEmail() + " | Role: " + user.getGrade());
        } else {
            System.err.println("Login Failed! Exiting.");
            return;
        }

        try {
            // ==========================================
            // TEST 2: Teacher Management
            // ==========================================
            System.out.println("\n--- 2. Testing Teacher Module ---");

            Teacher t = new Teacher();
            t.setNationalId("DZ-" + System.currentTimeMillis());
            t.setFirstName("Ahmed");
            t.setLastName("Benali");
            t.setPhone("0550123456");
            t.setDob(LocalDate.of(1985, 5, 20));
            t.setPob("Ouargla");

            service.addTeacher(t);
            t.setId(1);
            System.out.println("New Teacher Added.");
            service.addSubject(new Subject(1,"Mathematics"));
            service.addSubject(new Subject(2,"Physics"));
            t.setSubjectIds(Arrays.asList(1, 2));
            for (Integer subjectId : t.getSubjectIds()) {
                service.assignSubjectToTeacher(t.getId(), subjectId, 5); // 5 years taught
            }

            service.getAllTeachers().forEach(teacher ->
                    System.out.println(" -> " + teacher.getFirstName() + " " + teacher.getLastName() +
                            " | ID: " + teacher.getId())
            );

            // ==========================================
            //  TEST 3: Room Management
           // ==========================================
            System.out.println("\n--- 3. Testing Room Module ---");

            Room room = new Room("Lab-01", "IT");
            service.addRoom(room);
            System.out.println("Room Added.");

            service.getAllRooms().forEach(r ->
                    System.out.println(" -> Room: " + r.getRoomNumber() + " | Type: " + r.getType())
            );

           // ==========================================
           //  TEST 4: Material + Material Log
           //==========================================
            System.out.println("\n--- 4. Testing Material & Logs ---");

            Material mat = new Material("Projecteur Data Show", 5);
            service.addMaterial(mat);
            System.out.println("Material Added.");

            service.getAllMaterials().forEach(m ->
                    System.out.println(" -> Item: " + m.getName() + " | Qty: " + m.getQuantity())
            );
            Material finalMat = mat;
            mat = service.getAllMaterials().stream()
                    .filter(m -> m.getName().equals(finalMat.getName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Material not found in DB"));


            System.out.println("\nUpdating material quantity (should create log)...");
            service.updateMaterialQuantity(mat.getId(), 8);

            service.getAllMaterials().forEach(m ->
                    System.out.println(" -> UPDATED Item: " + m.getName() + " | Qty: " + m.getQuantity())
            );

            // ==========================================
            // TEST 5: Logout & Security
            // ==========================================
            System.out.println("\n--- 5. Testing Security ---");
            service.logout();
            System.out.println("User Logged Out.");

            try {
                service.getAllStudents();
            } catch (SecurityException e) {
                System.out.println("[SUCCESS] Access Denied: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
