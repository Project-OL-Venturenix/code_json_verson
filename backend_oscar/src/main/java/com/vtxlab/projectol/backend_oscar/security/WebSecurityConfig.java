package com.vtxlab.projectol.backend_oscar.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.vtxlab.projectol.backend_oscar.security.jwt.AuthEntryPointJwt;
import com.vtxlab.projectol.backend_oscar.security.jwt.AuthTokenFilter;
import com.vtxlab.projectol.backend_oscar.security.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
// (securedEnabled = true,
// jsr250Enabled = true,
// prePostEnabled = true) // by default
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  // @Override
  // public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
  // authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  // }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }

  // @Bean
  // @Override
  // public AuthenticationManager authenticationManagerBean() throws Exception {
  // return super.authenticationManagerBean();
  // }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // @Override
  // protected void configure(HttpSecurity http) throws Exception {
  // http.cors().and().csrf().disable()
  // .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
  // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
  // .authorizeRequests().antMatchers("/api/auth/**").permitAll()
  // .antMatchers("/api/test/**").permitAll()
  // .anyRequest().authenticated();
  //
  // http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  // }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth//
            .requestMatchers("/api/auth/**").permitAll()//
            .requestMatchers("/api/test/**").permitAll()
            .requestMatchers("/api/events/**").permitAll()
            .requestMatchers("/api/eventgroups/**").permitAll()
            .requestMatchers("/api/eventquestions/**").permitAll()
            .requestMatchers("/api/eventUsers/**").permitAll()
            .requestMatchers("/api/groups/**").permitAll()
            .requestMatchers("/api/groupquestionsubmits/**").permitAll()
            .requestMatchers("/api/groupscores/**").permitAll()
            .requestMatchers("/api/grouptestcases/**").permitAll()
            .requestMatchers("/api/grouptusers/**").permitAll()
            .requestMatchers("/api/questions/**").permitAll()
            .requestMatchers("/api/testcases/**").permitAll()
            .requestMatchers("/api/users/**").permitAll()
            .requestMatchers("/api/userquestionsubmits/**").permitAll()
            .requestMatchers("/api/userscores/**").permitAll()
            .requestMatchers("/api/usertestcases/**").permitAll().anyRequest()
            .authenticated());
    http.authenticationProvider(authenticationProvider());

    http.addFilterBefore(authenticationJwtTokenFilter(),
      UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
