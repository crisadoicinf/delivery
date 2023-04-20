package com.crisado.delivery.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import lombok.Setter;

@Configuration
@EnableWebSecurity
@ConfigurationProperties("security")
@Setter
public class SecurityConfiguration {
	
	private List<UserProp> users;

    @Bean
    public UserDetailsManager userDetailsService(PasswordEncoder encoder) {
    	if (users == null) return new InMemoryUserDetailsManager();
        return new InMemoryUserDetailsManager(users.stream()
        		.map(prop -> User.builder()
        				.username(prop.username())
        				.password(encoder.encode(prop.password()))
        				.roles("ADMIN")
        				.build())
        		.collect(Collectors.toList()));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.requireCsrfProtectionMatcher(request -> false))
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin(withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    public static record UserProp(String username, String password) {};
}