package com.seowolseong.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // 개발 단계에선 일단 편하게

      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()
      ).logout(lo -> lo
    	        .logoutUrl("/api/auth/logout")
    	        .logoutSuccessHandler((req, res, auth) -> {
    	          res.setStatus(200);
    	          res.setContentType("application/json;charset=UTF-8");
    	          res.getWriter().write("{\"message\":\"logout ok\"}");
    	        })
    	        .invalidateHttpSession(true)
    	        .deleteCookies("JSESSIONID")
    	        .clearAuthentication(true)
    	      )


      .formLogin(form -> form.disable());

    return http.build();
  }
}
