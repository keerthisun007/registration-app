// [Core Java] Package declaration: Groups this interface under the repository layer of our application
package com.keerthi.registration.repository;

// [Core Java] Importing our custom User entity model representing the database table 'users'
import com.keerthi.registration.entity.User;
// [Spring Data JPA] Importing JpaRepository which provides pre-built CRUD methods (save, findById, findAll, delete)
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserRepository Interface
 * 
 * TECHNOLOGY: [Core Java Interface] + [Spring Data JPA Framework]
 * USE: Acts as the Data Access Layer (DAO) between Java application and MySQL
 * database.
 * WHY:
 * - [Spring Data JPA]: By extending JpaRepository<User, Long>, Spring
 * automatically provides
 * methods like .save(user), .findAll(), .findById(id), etc., at runtime without
 * writing any manual SQL.
 * - [Core Java Generics]: '<User, Long>' specifies:
 * 1. 'User' -> The entity class mapped to the database table.
 * 2. 'Long' -> The wrapper type of the primary key (id).
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Custom Query Method: existsByEmailIgnoreCase
     * 
     * TECHNOLOGY: [Spring Data JPA Query Method] + [Hibernate SQL Generation]
     * USE: Checks if a user already exists in MySQL with the given email address.
     * WHY:
     * - Prevents duplicate registrations with the same email address.
     * - 'IgnoreCase' ensures case-insensitive matching ('Test@gmail.com' matches
     * 'test@gmail.com').
     * - [Spring Data JPA + Hibernate]: Automatically translates this method name
     * into the SQL query:
     * "SELECT count(*) > 0 FROM users WHERE UPPER(email) = UPPER(?);"
     * 
     * @param email The email address to check in the database
     * @return [Core Java boolean]: true if email already exists, false otherwise
     */
    boolean existsByEmailIgnoreCase(String email);
}