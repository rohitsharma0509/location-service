package com.scb.location.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

  @Bean
  @Profile("local | test")
  WebSecurityConfigurerAdapter noAuth() {
      return new WebSecurityConfigurerAdapter() {
          @Override
          protected void configure(HttpSecurity http) throws Exception {
            http.headers().frameOptions();
            http.csrf().disable();
            http.authorizeRequests().antMatchers("/").permitAll();
          }
      };
  }
  
  @Bean
  @Profile("!local & !test")
  WebSecurityConfigurerAdapter auth() {
      return new WebSecurityConfigurerAdapter() {
          @Override
          protected void configure(HttpSecurity http) throws Exception {
            http.headers().frameOptions();
            http.csrf().disable();
            http.addFilterBefore(new RequestValidationFilter(), BasicAuthenticationFilter.class);
            http.authorizeRequests().antMatchers("/").permitAll();
          }
      };
  }
}