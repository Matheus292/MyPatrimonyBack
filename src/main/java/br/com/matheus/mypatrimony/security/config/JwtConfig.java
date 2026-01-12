package br.com.matheus.mypatrimony.security.config;


import br.com.matheus.mypatrimony.config.ConfigProperties;
import br.com.matheus.mypatrimony.security.filter.JwtAuthenticateFilter;
import br.com.matheus.mypatrimony.security.filter.JwtValidateFilter;
import br.com.matheus.mypatrimony.security.service.DetailLoginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class JwtConfig extends WebSecurityConfigurerAdapter {

    private final DetailLoginServiceImpl service;

    private final PasswordEncoder passwordEncoder;


    private final ConfigProperties configProperties;


    public JwtConfig(DetailLoginServiceImpl service, PasswordEncoder passwordEncoder, ConfigProperties configProperties) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.configProperties = configProperties;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.userDetailsService(service).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/signUp").permitAll()
                .antMatchers(HttpMethod.POST, "/api/forgotPassword").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/newPassword").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JwtAuthenticateFilter(authenticationManager(), configProperties))
                .addFilter(new JwtValidateFilter(authenticationManager(), configProperties))

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
