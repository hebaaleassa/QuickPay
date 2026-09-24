package com.progressoft.quickpay.payments.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("tolay")
                        .password(passwordEncoder.encode("12345"))
                        .roles("PAYMENT")
                        .build(),
                User.withUsername("admin")
                        .password(passwordEncoder.encode("56789"))
                        .roles("PAYMENT", "TEMPLATE")
                        .build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/payments/**").hasAnyRole("PAYMENT", "TEMPLATE")
                        .requestMatchers("/api/templates/**").hasRole("TEMPLATE")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
