package com.mg.smartrent.gatewayapi.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfigAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() //
                .authorizeRequests() //
                .antMatchers(HttpMethod.GET, "/eureka/**").authenticated() // eureka client
                .antMatchers(HttpMethod.POST, "/eureka/**").authenticated() // eureka client
                .antMatchers(HttpMethod.DELETE, "/eureka/**").authenticated() // eureka client
                .anyRequest().authenticated().and().httpBasic(); // dashboard authorization
    }
}
