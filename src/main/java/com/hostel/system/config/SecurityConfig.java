package com.hostel.system.config;

import com.hostel.system.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
public UserDetailsService userDetailsService(UserRepository userRepository) {
    return username -> {

        System.out.println("LOGIN ATTEMPT : " + username);

        return userRepository.findByUsername(username)
                .map(user -> {

                    System.out.println("FOUND USER : " + user.getUsername());
                    System.out.println("ROLE : " + user.getRole());
                    System.out.println("PASSWORD HASH : " + user.getPassword());

                    return new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(user.getRole().name()))
                    );

                })
                .orElseThrow(() ->
                        new UsernameNotFoundException(username));
    };
}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Enabled or customized as needed
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/error").permitAll()
                .requestMatchers("/students/new", "/students/edit/**", "/students/delete/**").hasAnyRole("ADMIN", "WARDEN")
                .requestMatchers("/rooms/new", "/rooms/edit/**", "/rooms/delete/**").hasRole("ADMIN")
                .requestMatchers("/complaints/resolve/**").hasAnyRole("ADMIN", "WARDEN")
                .requestMatchers("/fees/generate/**").hasAnyRole("ADMIN", "WARDEN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
