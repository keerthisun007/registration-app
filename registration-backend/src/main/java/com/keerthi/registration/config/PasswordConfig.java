// [Core Java] Package declaration: Groups this class under the configuration layer
package com.keerthi.registration.config;

// [Spring Framework IoC] Importing @Bean annotation to register objects into Spring's ApplicationContext container
import org.springframework.context.annotation.Bean;
// [Spring Framework IoC] Importing @Configuration annotation to mark this class as a source of Spring bean definitions
import org.springframework.context.annotation.Configuration;
// [Spring Security] Importing BCryptPasswordEncoder: industry-standard password hashing implementation
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// [Spring Security] Importing PasswordEncoder interface representing password hashing contracts
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordConfig Class
 * 
 * TECHNOLOGY: [Core Java Class] + [Spring Framework @Configuration] + [Spring Security Crypto]
 * USE: Configures and exposes the password hashing mechanism for the entire application.
 * WHY:
 *  1. Spring Security doesn't automatically create a PasswordEncoder bean by default.
 *  2. Defining this @Bean allows UserService to inject PasswordEncoder without tight coupling.
 *  3. BCrypt uses a secure cryptographic salt and cost factor to resist rainbow table and brute-force attacks.
 */
@Configuration // [Spring Framework]: Tells Spring IoC container: "Process this class during startup to generate bean definitions"
public class PasswordConfig {

    /**
     * passwordEncoder Bean Definition
     * 
     * TECHNOLOGY: [Spring @Bean] + [Spring Security BCryptPasswordEncoder]
     * USE: Creates and registers a singleton instance of BCryptPasswordEncoder in Spring's ApplicationContext.
     * WHY: Any service (like UserService) that requires password encryption can inject this bean.
     * 
     * @return [Spring Security PasswordEncoder]: a thread-safe PasswordEncoder using BCrypt algorithm
     */
    @Bean // [Spring Framework]: Registers the returned object as a Spring-managed Bean in the ApplicationContext
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // [Spring Security]: Creates new BCryptPasswordEncoder instance
    }
}