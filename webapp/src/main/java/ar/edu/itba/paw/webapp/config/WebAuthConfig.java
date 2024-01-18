package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.UserDetailsService;
import ar.edu.itba.paw.webapp.security.api.filter.HttpMethodFilter;
import ar.edu.itba.paw.webapp.security.api.filter.NeighborhoodAccessControlFilter;
import ar.edu.itba.paw.webapp.security.api.jwt.JwtAuthenticationEntryPoint;
import ar.edu.itba.paw.webapp.security.api.jwt.JwtAuthenticationProvider;
import ar.edu.itba.paw.webapp.security.api.jwt.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.web.cors.CorsConfiguration.ALL;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ComponentScan({"ar.edu.itba.paw.webapp.auth","ar.edu.itba.paw.webapp.security"})
@CrossOrigin(origins = "http://localhost:4200/")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    @Value("classpath:rememberme.key")
    private Resource rememberMeKey;
    @Autowired
    private UserDetailsService userDetails;
    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;
    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter(authenticationManagerBean(), authenticationEntryPoint);
    }

    @Bean
    public NeighborhoodAccessControlFilter neighborhoodAccessControlFilter() {
        return new NeighborhoodAccessControlFilter();
    }

    @Bean
    public HttpMethodFilter httpMethodFilter() {
        return new HttpMethodFilter();
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetails).passwordEncoder(passwordEncoder());
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER_WHEN_DOWNGRADE);
        http
                .cors().and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(neighborhoodAccessControlFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(httpMethodFilter(), FilterSecurityInterceptor.class)
                .authorizeRequests()
                .antMatchers("/auth").permitAll()
                .antMatchers(
                        "/",
                        "/languages",       "/languages/{id:[1-9][0-9]*}",
                        "/roles",           "/roles/{id:[1-9][0-9]*}",
                        "/departments",     "/departments/{id:[1-9][0-9]*}",
                        "/professions",     "/professions/{id:[1-9][0-9]*}",
                        "/times",           "/times/{id:[1-9][0-9]*}",
                        "/days",            "/days/{id:[1-9][0-9]*}",
                        "/images/*",
                        "/shifts",          "/shifts/{id:[1-9][0-9]*}",
                        "/neighborhoods",   "/neighborhoods/{id:[1-9][0-9]*}",
                        "/neighborhoods/{id:[1-9][0-9]*}/amenities",


                        "/test/**"
                ).permitAll()
                .anyRequest().authenticated();
    }

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .invalidSessionUrl("/login")
                .and().authorizeRequests()
                .antMatchers("/signup", "/login", "/signup-worker").anonymous()
                .antMatchers("/admin/**").hasRole("ADMINISTRATOR")
                .antMatchers("/unverified").hasRole("UNVERIFIED_NEIGHBOR")
                .antMatchers("/services").hasAnyRole("WORKER", "ADMINISTRATOR", "NEIGHBOR")
                .antMatchers("/services/neighborhoods").hasRole("WORKER")
                .antMatchers("/rejected").hasRole("REJECTED")
                .antMatchers("/profile").hasAnyRole("ADMINISTRATOR", "NEIGHBOR", "WORKER")
                .antMatchers("/").hasAnyRole("NEIGHBOR", "ADMINISTRATOR")
                .antMatchers("/complaints", "/announcements", "/amenities", "/information", "/reservation", "/posts/**").hasAnyRole("NEIGHBOR", "ADMINISTRATOR")
                .antMatchers("/**").hasAnyRole("NEIGHBOR", "ADMINISTRATOR", "WORKER")
                .and().formLogin()
                .failureHandler(new CustomAuthenticationFailureHandler())
                .usernameParameter("mail")
                .passwordParameter("password")
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler())
                .and().rememberMe()
                .rememberMeParameter("rememberMe")
                .userDetailsService(userDetails)
                .key(StreamUtils.copyToString(rememberMeKey.getInputStream(), StandardCharsets.UTF_8))
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/landingPage")
                .and().exceptionHandling()
                .accessDeniedPage("/errors/errorPage")
                .and().csrf().disable();
    }*/


    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            for (GrantedAuthority authority : authorities) {
                String authorityName = authority.getAuthority();

                switch (authorityName) {
                    case "ROLE_UNVERIFIED_NEIGHBOR":
                        String unverifiedRedirectUrl = request.getContextPath() + "/unverified";
                        response.sendRedirect(unverifiedRedirectUrl);
                        return;
                    case "ROLE_WORKER":
                        String workerRedirectUrl = request.getContextPath() + "/services";
                        response.sendRedirect(workerRedirectUrl);
                        return;
                    case "ROLE_REJECTED":
                        String rejectedRedirectUrl = request.getContextPath() + "/rejected";
                        response.sendRedirect(rejectedRedirectUrl);
                        return;
                }
            }

            String defaultRedirectUrl = request.getContextPath() + "/";
            response.sendRedirect(defaultRedirectUrl);
        };
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/postImages/*", "/resources/**", "/css/**", "/js/**", "/img/**", "/favicon.ico", "/403");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(ALL));
//        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200/"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.addAllowedHeader(ALL);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Link", "Location", "ETag", "Total-Elements"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Collections.singletonList(ALL));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}