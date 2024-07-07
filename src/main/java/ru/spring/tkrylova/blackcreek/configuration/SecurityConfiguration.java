package ru.spring.tkrylova.blackcreek.configuration;

import javax.security.auth.login.AccountException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserDetailService;

import java.text.MessageFormat;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class SecurityConfiguration {

  private final BlackCreekUserDetailService blackCreekUserDetailService;

  public SecurityConfiguration(BlackCreekUserDetailService blackCreekUserDetailService) {
    this.blackCreekUserDetailService = blackCreekUserDetailService;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws AccountException {
    try {
      return config.getAuthenticationManager();
    } catch (Exception e) {
      throw new AccountException(MessageFormat.format("AuthenticationManager not configured: {0}", e.getMessage()));
    }
  }

  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(blackCreekUserDetailService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity.csrf(AbstractHttpConfigurer::disable)
        .authenticationProvider(authenticationProvider())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/account/registration", "/account/login")
            .permitAll()
            .anyRequest()
            .authenticated())
        .formLogin(form -> form
            .usernameParameter("application_user_email")
            .passwordParameter("application_user_password")
            .loginPage("/account/login")
            .loginProcessingUrl("/account/login")
            .failureUrl("/account/login?failed")
            .defaultSuccessUrl("/account")
            .permitAll())
        .logout(logout -> logout.logoutUrl(
                "/account/logout")
            .logoutSuccessUrl("/account/login")
            .permitAll())
        .build();
  }

}