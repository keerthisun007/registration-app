// [Core Java] Package declaration: Root package for the entire Spring Boot backend project
package com.keerthi.registration;

// [Spring Boot] Importing SpringApplication class which bootstraps and launches the Spring application
import org.springframework.boot.SpringApplication;
// [Spring Boot] Importing @SpringBootApplication annotation
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RegistrationApplication Class
 * 
 * TECHNOLOGY: [Core Java Class] + [Spring Boot Framework]
 * USE: The main entry point that boots up the Spring Boot backend server.
 * WHY:
 *  - @SpringBootApplication [Spring Boot]: Convenience annotation that combines 3 key annotations:
 *    1. @Configuration: Tags the class as the source of bean definitions.
 *    2. @EnableAutoConfiguration: Automatically configures Tomcat, HikariCP, Hibernate, and Jackson.
 *    3. @ComponentScan: Scans all sub-packages (controller, service, repository, config, etc.) to discover Spring components.
 */
@SpringBootApplication // [Spring Boot]: Triggers auto-configuration and component scanning
public class RegistrationApplication {

    /**
     * main Method
     * 
     * TECHNOLOGY: [Core Java Entry Point] + [Spring Boot Runtime]
     * USE: The standard Java main entry point executed by JVM (Java Virtual Machine).
     * WHY: Calls SpringApplication.run() which:
     *      1. Starts the Spring IoC ApplicationContext container.
     *      2. Initializes embedded Apache Tomcat web server on port 8080.
     *      3. Connects HikariCP connection pool to MySQL database.
     *      4. Maps REST endpoints (/api/users/...) ready to accept client traffic.
     * 
     * @param args Command line arguments passed when launching the program
     */
    public static void main(String[] args) {
        SpringApplication.run(RegistrationApplication.class, args); // [Spring Boot]: Boots the application
    }
}