package ua.toptar.toptarfuelstationpos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ua.toptar.toptarfuelstationpos.repository.UserRepository;

/**
 * Configuration class for Spring Security settings.
 * Defines security rules, user authentication, and password encoding for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    /**
     * Constructs a new {@code SecurityConfig} with the specified user repository.
     *
     * @param userRepository the repository used to access user data
     */
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * Defines access rules for different endpoints, form-based login, and logout functionality.
     *
     * @param http the {@code HttpSecurity} to configure
     * @return the configured {@code SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/api/transactions").permitAll()
                .requestMatchers("/api/transactions/user").authenticated()
                .requestMatchers("/pos", "/pos/transaction").permitAll()
                .requestMatchers("/login", "/register", "/", "/css/**", "/images/**", "/js/**", "/webfonts/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/pos")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }

    /**
     * Provides a {@code UserDetailsService} implementation for user authentication.
     * Loads user details from the repository based on the username.
     *
     * @return the configured {@code UserDetailsService}
     * @throws RuntimeException if the user is not found in the repository
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
            .map(user -> org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Provides a {@code PasswordEncoder} implementation for password hashing.
     * Uses {@code BCryptPasswordEncoder} for secure password encoding.
     *
     * @return the configured {@code PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}