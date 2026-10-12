// [Core Java] Package declaration: Groups this class under the entity layer
package com.keerthi.registration.entity;

// [Hibernate / JPA] Importing Jakarta Persistence annotations for Object-Relational Mapping (ORM)
import jakarta.persistence.*;

// [Core Java] Importing java.time.LocalDateTime to store server timestamps
import java.time.LocalDateTime;

/**
 * User Entity Class
 * 
 * TECHNOLOGY: [Core Java Class] + [Hibernate / Jakarta Persistence (JPA)] + [MySQL Table Mapping]
 * USE: Represents the database model/table 'users' in MySQL.
 * WHY: Hibernate (JPA provider) reads this class and automatically creates/updates
 *      the corresponding MySQL table schema and maps rows directly to Java objects.
 */
@Entity // [Hibernate / JPA]: Marks this Java class as a JPA persistent entity (database table)
@Table(name = "users") // [Hibernate / JPA]: Specifies the exact name of the table in MySQL database
public class User {

    /**
     * Primary Key: id
     * 
     * TECHNOLOGY: [Hibernate / JPA @Id] + [MySQL AUTO_INCREMENT]
     * USE: Uniquely identifies each user row in the table.
     * WHY: 
     *  - @Id: Declares this field as the Primary Key.
     *  - @GeneratedValue(strategy = GenerationType.IDENTITY): Delegates auto-incrementing to MySQL (1, 2, 3...).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // [Core Java Long wrapper]

    // [Hibernate / JPA @Column]: Maps to MySQL VARCHAR(100) NOT NULL
    @Column(nullable = false, length = 100)
    private String name; // [Core Java String]

    // [Hibernate / JPA @Column]: Maps to MySQL VARCHAR(150) NOT NULL UNIQUE
    // WHY: Database-level unique constraint prevents duplicate accounts even under concurrent requests
    @Column(nullable = false, unique = true, length = 150)
    private String email; // [Core Java String]

    // [Hibernate / JPA @Column]: Maps to MySQL VARCHAR(10) NOT NULL
    @Column(nullable = false, length = 10)
    private String phone; // [Core Java String]

    // [Hibernate / JPA @Column]: Maps to MySQL VARCHAR(100) NOT NULL
    // WHY: Stores 60-character BCrypt hash string ($2a$10$...)
    @Column(nullable = false, length = 100)
    private String password; // [Core Java String]

    // [Hibernate / JPA @Column]: Maps to MySQL DATETIME(6) NOT NULL (Immutable creation timestamp)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // [Hibernate / JPA @Column]: Maps to MySQL DATETIME(6) NOT NULL (Updated on every change)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default No-Args Constructor
     * 
     * TECHNOLOGY: [Core Java Constructor] (Mandatory for JPA/Hibernate)
     * USE: Required by JPA/Hibernate to instantiate the object via reflection when querying from MySQL.
     */
    public User() {
    }

    /**
     * Parameterized Constructor
     * 
     * TECHNOLOGY: [Core Java Constructor]
     * USE: Convenient constructor to create a new User before persisting it to the database.
     */
    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // [Core Java] Getters and Setters: Standard Java encapsulation methods to read and write private fields
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}