package com.example.tickets.security;

import com.example.tickets.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())   // для простоты отключаем CSRF на API
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http
                //.securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // ПУБЛИЧНЫЕ страницы
                        .requestMatchers("/", "/login", "/register").permitAll()
                        .requestMatchers("/performances").permitAll()
                        .requestMatchers(HttpMethod.GET, "/performances/*").permitAll()
                        .requestMatchers("/performances/*/buy").authenticated()

                        // Доступ к билетам
                        .requestMatchers(HttpMethod.GET, "/tickets", "/tickets/").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/tickets/edit/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/tickets/add").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/tickets/edit/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/tickets/delete/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll()
                )


                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            String referer = request.getHeader("Referer");
                            response.sendRedirect(referer != null ? referer : "/");
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

}


