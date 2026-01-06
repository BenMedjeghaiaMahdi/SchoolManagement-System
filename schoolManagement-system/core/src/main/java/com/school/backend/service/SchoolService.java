package com.school.backend.service;

import com.school.backend.DatabaseConfig;
import com.school.backend.dao.*;
import com.school.backend.model.*;
import org.jdbi.v3.core.Jdbi;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;

/**
 * Central service for school management operations.
 * Refactored to include validation, better error handling, and logging.
 */
public class SchoolService {

    private static final Logger LOGGER = Logger.getLogger(SchoolService.class.getName());
    private static SchoolService instance;

    private final Jdbi jdbi;
    private User currentUser;

    // ================= CONSTANTS =================
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String UPLOAD_BASE_DIR = "uploads/";
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_MODEL_LENGTH = 100;

    private SchoolService() {
        this.jdbi = DatabaseConfig.getJdbi();
    }

    public static SchoolService getInstance() {
        if (instance == null) {
            instance = new SchoolService();
        }
        return instance;
    }

    // ================= AUTHENTICATION =================

    public boolean login(String email, String password) {
        try {
            Optional<User> user = jdbi.onDemand(UserDao.class).login(email, password);
            if (user.isPresent()) {
                this.currentUser = user.get();
                LOGGER.info("User logged in: " + currentUser.getId());
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Login failed", e);
            throw new RuntimeException("Authentication failed", e);
        }
    }

    public void logout() {
        if (currentUser != null) {
            LOGGER.info("User logged out: " + currentUser.getId());
        }
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // ================= MATERIALS =================

    public List<Material> getAllMaterials() {
        checkReadPermission("MATERIALS");
        try {
            LOGGER.fine("Fetching all materials");
            return jdbi.withExtension(MaterialDao.class, MaterialDao::listAll);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch materials", e);
            throw new RuntimeException("Failed to fetch materials", e);
        }
    }

    /**
     * Retrieves a specific material by ID.
     */
    public Material getMaterial(int materialId) {
        checkReadPermission("MATERIALS");
        try {
            LOGGER.fine("Loading material ID: " + materialId);
            Material material = jdbi.onDemand(MaterialDao.class).findById(materialId);

            if (material == null) {
                throw new IllegalArgumentException("Material not found: " + materialId);
            }
            return material;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch material", e);
            throw new RuntimeException("Failed to load material", e);
        }
    }

    // In SchoolService.java, update these methods:

    public Material addMaterial(Material material) {
        checkWritePermission();
        validateMaterial(material);

        try {
            LOGGER.info("Adding new material: " + material.getName());
            MaterialDao dao = jdbi.onDemand(MaterialDao.class);
            int id = dao.insert(material);
            material.setId(id);

            // Log the creation with ADD action
            MaterialLog log = new MaterialLog();
            log.setMaterialId(id);
            log.setAction("ADD");  // ✅ CHANGED from "CREATE" to "ADD"
            log.setQuantityChange(material.getQuantity());
            log.setQuantityBefore(0);
            log.setQuantityAfter(material.getQuantity());
            log.setUserId(currentUser.getId());

            jdbi.onDemand(MaterialLogDao.class).insert(log);

            LOGGER.info("Material created with ID: " + id);
            return material;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to add material", e);
            throw new RuntimeException("Failed to add material", e);
        }
    }

    /**
     * Updates an existing material.
     */
    public void updateMaterial(Material material) {
        checkWritePermission();
        validateMaterial(material);

        try {
            LOGGER.info("Updating material ID: " + material.getId());
            Material existing = getMaterial(material.getId());

            jdbi.onDemand(MaterialDao.class).update(material);

            // Log the update
            MaterialLog log = new MaterialLog();
            log.setMaterialId(material.getId());
            log.setAction("UPDATE");  // ✅ Already correct
            log.setQuantityChange(material.getQuantity() - existing.getQuantity());
            log.setQuantityBefore(existing.getQuantity());
            log.setQuantityAfter(material.getQuantity());
            log.setUserId(currentUser.getId());

            jdbi.onDemand(MaterialLogDao.class).insert(log);
            LOGGER.info("Material updated: " + material.getId());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to update material", e);
            throw new RuntimeException("Failed to update material", e);
        }
    }

    /**
     * Deletes a material from inventory.
     */
    public void deleteMaterial(int materialId) {
        checkWritePermission();

        try {
            LOGGER.info("Deleting material ID: " + materialId);
            Material material = getMaterial(materialId);  // Get BEFORE deleting

            // Log the deletion BEFORE deleting from database
            MaterialLog log = new MaterialLog();
            log.setMaterialId(materialId);
            log.setAction("REMOVE");  // ✅ CHANGED from "DELETE" to "REMOVE"
            log.setQuantityChange(-material.getQuantity());
            log.setQuantityBefore(material.getQuantity());
            log.setQuantityAfter(0);
            log.setUserId(currentUser.getId());

            jdbi.onDemand(MaterialLogDao.class).insert(log);

            // NOW delete the material
            jdbi.onDemand(MaterialDao.class).deleteById(materialId);

            LOGGER.info("Material deleted: " + materialId);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to delete material", e);
            throw new RuntimeException("Failed to delete material", e);
        }
    }

    /**
     * Updates material quantity and logs the change.
     */
    public void updateMaterialQuantity(int materialId, int newQty) {
        checkWritePermission();

        try {
            LOGGER.info("Updating quantity for material ID: " + materialId + " to: " + newQty);

            MaterialDao materialDao = jdbi.onDemand(MaterialDao.class);
            int before = materialDao.getQuantity(materialId);
            materialDao.updateQuantity(materialId, newQty);

            MaterialLog log = new MaterialLog();
            log.setMaterialId(materialId);
            log.setAction("UPDATE");  // ✅ Already correct
            log.setQuantityChange(newQty - before);
            log.setQuantityBefore(before);
            log.setQuantityAfter(newQty);
            log.setUserId(currentUser.getId());

            jdbi.onDemand(MaterialLogDao.class).insert(log);

            LOGGER.info("Quantity updated successfully");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to update quantity", e);
            throw new RuntimeException("Failed to update material quantity", e);
        }
    }

    /**
     * Retrieves change history for a material.
     */
    public List<MaterialLog> getMaterialHistory(int materialId) {
        try {
            return jdbi.onDemand(MaterialLogDao.class).findByMaterial(materialId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch material history", e);
            throw new RuntimeException("Failed to load history", e);
        }
    }


    // ================= IMAGE OPERATIONS =================

    public void updateCurrentUserPhoto(File imageFile) {
        if (currentUser == null)
            throw new SecurityException("Not logged in");

        validateImageFile(imageFile);
        String prefix = roleToPrefix(currentUser.getRole());

        String path = saveImage(imageFile, prefix, currentUser.getId(), "users");
        jdbi.onDemand(UserDao.class).updatePhotoPath(currentUser.getId(), path);
        currentUser.setPhotoPath(path);
    }

    public void updateMaterialPhoto(int materialId, File imageFile) {
        checkWritePermission();
        validateImageFile(imageFile);

        try {
            LOGGER.info("Updating photo for material ID: " + materialId);

            Material material = getMaterial(materialId);
            if (material.getPhotoPath() != null && !material.getPhotoPath().isEmpty()) {
                deleteImageFile(material.getPhotoPath());
            }

            String path = saveImage(imageFile, "MT", materialId, "materials");
            jdbi.onDemand(MaterialDao.class).updatePhotoPath(materialId, path);

            LOGGER.info("Photo updated successfully: " + path);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to update photo", e);
            throw new RuntimeException("Failed to update photo", e);
        }
    }

    public void updateTeacherPhoto(int teacherId, File imageFile) {
        checkWritePermission();
        validateImageFile(imageFile);

        String path = saveImage(imageFile, "TE", teacherId, "teachers");
        jdbi.onDemand(TeacherDao.class).updatePhotoPath(teacherId, path);
    }

    public void updateStudentPhoto(int studentId, File imageFile) {
        checkWritePermission();
        validateImageFile(imageFile);

        String path = saveImage(imageFile, "ST", studentId, "students");
        jdbi.onDemand(StudentDao.class).updatePhotoPath(studentId, path);
    }

    public void addTeacherWithPhoto(Teacher teacher, File imageFile) {
        checkWritePermission();
        validateImageFile(imageFile);

        jdbi.onDemand(TeacherDao.class).insert(teacher);

        int teacherId = jdbi.withHandle(handle ->
                handle.createQuery("SELECT last_insert_rowid()")
                        .mapTo(int.class)
                        .one()
        );

        if (imageFile != null) {
            String path = saveImage(imageFile, "TE", teacherId, "teachers");
            jdbi.onDemand(TeacherDao.class).updatePhotoPath(teacherId, path);
            teacher.setPhotoPath(path);
        }

        teacher.setId(teacherId);
    }

    // ================= IMAGE HELPERS =================

    private String saveImage(File imageFile, String prefix, int id, String folder) {
        try {
            String uploadDir = UPLOAD_BASE_DIR + folder + "/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IOException("Failed to create directory: " + uploadDir);
                }
            }

            String extension = imageFile.getName().substring(imageFile.getName().lastIndexOf("."));
            String fileName = prefix + "-" + id + extension;
            Path destination = Paths.get(uploadDir + fileName);

            Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            LOGGER.info("Image saved: " + destination);
            return destination.toString();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save image", e);
            throw new RuntimeException("Failed to save image", e);
        }
    }

    private void validateImageFile(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Image file does not exist");
        }

        String lower = file.getName().toLowerCase();
        if (!lower.matches(".*\\.(png|jpg|jpeg|gif)$")) {
            throw new IllegalArgumentException(
                    "Invalid image format. Supported: PNG, JPG, JPEG, GIF"
            );
        }

        if (file.length() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Image too large (max 5MB). Current size: " +
                            (file.length() / 1024 / 1024) + "MB"
            );
        }
    }

    private void deleteImageFile(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        try {
            Path path = Paths.get(imagePath);
            if (Files.exists(path)) {
                Files.delete(path);
                LOGGER.info("Image deleted: " + imagePath);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to delete image: " + imagePath, e);
        }
    }

    // ================= VALIDATION =================

    private void validateMaterial(Material material) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }

        String name = material.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Material name cannot be empty");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Material name too long (max " + MAX_NAME_LENGTH + " characters)"
            );
        }

        String model = material.getModel();
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Material model cannot be empty");
        }
        if (model.length() > MAX_MODEL_LENGTH) {
            throw new IllegalArgumentException(
                    "Material model too long (max " + MAX_MODEL_LENGTH + " characters)"
            );
        }

        String category = material.getCategory();
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Material category cannot be empty");
        }

        String status = material.getStatus();
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Material status cannot be empty");
        }

        if (material.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    // ================= TEACHERS =================

    public List<Teacher> getAllTeachers() {
        checkReadPermission("TEACHERS");
        return jdbi.onDemand(TeacherDao.class).listAll();
    }

    public List<Teacher> getTeachersBySubject(String subjectName) {
        checkReadPermission("TEACHERS");
        return jdbi.onDemand(TeacherDao.class).findBySubject(subjectName);
    }

    public void addTeacher(Teacher teacher) {
        checkWritePermission();
        jdbi.onDemand(TeacherDao.class).insert(teacher);
    }

    public void deleteTeacher(int id) {
        checkWritePermission();
        jdbi.onDemand(TeacherDao.class).deleteById(id);
    }

    // ================= SUBJECTS =================

    public List<Subject> getAllSubjects() {
        checkReadPermission("TEACHERS");
        return jdbi.onDemand(SubjectDao.class).listAll();
    }

    public void addSubject(Subject subject) {
        checkWritePermission();
        jdbi.onDemand(SubjectDao.class).insert(subject);
    }

    public void assignSubjectToTeacher(int teacherId, int subjectId, int yearsTaught) {
        checkWritePermission();
        jdbi.onDemand(TeacherSubjectDao.class).assignSubject(teacherId, subjectId, yearsTaught);
    }

    // ================= STUDENTS =================

    public List<Student> getAllStudents() {
        checkReadPermission("STUDENTS");
        return jdbi.onDemand(StudentDao.class).listAll();
    }

    public void addStudent(Student student) {
        checkWritePermission();
        jdbi.onDemand(StudentDao.class).insert(student);
    }

    public Double getStudentAverage(int studentId, int year) {
        checkReadPermission("STUDENTS");
        return jdbi.onDemand(StudentDao.class).getGradeForYear(studentId, year);
    }

    // ================= ROOMS =================

    public List<Room> getAllRooms() {
        checkReadPermission("ROOMS");
        return jdbi.onDemand(RoomDao.class).listAll();
    }

    public void addRoom(Room room) {
        checkWritePermission();
        jdbi.onDemand(RoomDao.class).insert(room);
    }

    public void deleteRoom(int id) {
        checkWritePermission();
        jdbi.onDemand(RoomDao.class).deleteById(id);
    }

    // ================= ABSENCES =================

    public void markAbsence(int personId, String personType, LocalDate date) {
        checkWritePermission();
        Absence absence = new Absence();
        absence.setPersonId(personId);
        absence.setDate(date);
        absence.setExplanation(false);
        jdbi.onDemand(AbsenceDao.class).recordAbsence(absence);
    }

    public int getAbsenceCount(int personId, String personType) {
        checkReadPermission("ATTENDANCE");
        return jdbi.onDemand(AbsenceDao.class).countAbsences(personId, personType);
    }

    public int justifyAbsence(int personId) {
        checkReadPermission("ATTENDANCE");
        return jdbi.onDemand(AbsenceDao.class).justifyAbsence(personId);
    }

    // ================= SECURITY =================

    private void checkWritePermission() {
        if (currentUser == null)
            throw new SecurityException("Not logged in");

        String role = currentUser.getRole();
        if ("MANAGER".equals(role) || "ECONOME".equals(role)) {
            throw new SecurityException("Access Denied: Write operation not allowed");
        }
    }

    private void checkReadPermission(String module) {
        if (currentUser == null)
            throw new SecurityException("Not logged in");

        if ("ECONOME".equals(currentUser.getRole()) && !"MATERIALS".equals(module)) {
            throw new SecurityException("Access Denied: Econome can access materials only");
        }
    }

    private String roleToPrefix(String role) {
        return switch (role) {
            case "ADMIN" -> "AD";
            case "MAGAZINER" -> "MA";
            case "EMPLOYEE" -> "EM";
            default -> "UN";
        };
    }
}