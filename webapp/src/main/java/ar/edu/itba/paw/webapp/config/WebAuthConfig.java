package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.UserDetailsService;
import ar.edu.itba.paw.webapp.security.api.filter.HttpMethodFilter;
import ar.edu.itba.paw.webapp.security.api.jwt.JwtAuthenticationEntryPoint;
import ar.edu.itba.paw.webapp.security.api.jwt.JwtAuthenticationProvider;
import ar.edu.itba.paw.webapp.security.api.jwt.JwtAuthenticationTokenFilter;
import ar.edu.itba.paw.webapp.security.service.AuthenticationTokenService;
import ar.edu.itba.paw.webapp.security.service.impl.JwtTokenIssuer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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
    @Autowired
    private AuthenticationTokenService authenticationTokenService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter(authenticationManagerBean(), authenticationEntryPoint, authenticationTokenService);
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
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .exceptionHandling().accessDeniedHandler(authenticationEntryPoint)
                .and()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                // UNRESTRICTED
                .antMatchers(
                        "/test/**",
                        "/",
                        "/auth/**",
                        "/professions/**",
                        "/departments/**",
                        "/shifts/**",
                        "/times/**",
                        "/days/**",
                        "/base-channels/**",
                        "/user-roles/**",
                        "/worker-roles/**",
                        "/transaction-types/**",
                        "/post-statuses/**",
                        "/product-statuses/**",
                        "/shift-statuses/**",
                        "/languages/**",
                        "/images/**",

                        "/neighborhoods",                           // needed for sign-up, restriction on QP
                        "/neighborhoods/*/users",                   // needed for sign-up, restriction on GET
                        "/workers"                                  // needed for sign-up, restriction on GET
                ).permitAll()
                // ANY USER WITH AN ACCOUNT CAN ACCESS HIS PROFILE
                .antMatchers(
                        "/neighborhoods/*/users/*"
                ).hasAnyRole("UNVERIFIED_NEIGHBOR","WORKER", "NEIGHBOR", "ADMINISTRATOR", "REJECTED")
                // BELONGING CONDITION
                .antMatchers("/neighborhoods/**").access("@accessControlHelper.isNeighborhoodMember(request)")
                // WORKERS, NEIGHBOR AND ADMINISTRATOR
                .antMatchers(
                        "/affiliations/*",
                        "/workers/**",
                        "/neighborhoods/*/posts/*"
                ).hasAnyRole("WORKER", "NEIGHBOR", "ADMINISTRATOR")
                // NEIGHBORS AND ADMINISTRATORS
                .antMatchers(
                        "/neighborhoods/*/products/*/comments/**",  // custom product restrictions
                        "/neighborhoods/*/products/**",             // custom product restrictions
                        "/neighborhoods/*/tags/**",
                        "/neighborhoods/*/likes/**",
                        "/neighborhoods/*/channels/**",             // only an Administrator can do more than GET
                        "/neighborhoods/*/amenities/**",            // only an Administrator can do more than GET
                        "/neighborhoods/*/bookings/**",
                        "/neighborhoods/*/resources/**",            // only an Administrator can do more than GET
                        "/neighborhoods/*/contacts/**",             // only an Administrator can do more than GET
                        "/neighborhoods/*/events/**"                // only an Administrator can do more than GET
                ).hasAnyRole("NEIGHBOR", "ADMINISTRATOR")
                .anyRequest().authenticated();
    }

   /* @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER_WHEN_DOWNGRADE);
        http
                .cors().and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll();  // Allow access to all paths for testing purposes
    }*/
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
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Access-Control-Allow-Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "Origin, Accept",
                "X-Requested-With",
                "Access-Control-RequestForm-Method",
                "Access-Control-RequestForm-Headers",
                "X-User-Urn",
                "Link"
        ));
        corsConfiguration.setExposedHeaders(Arrays.asList(
                "Origin",
                "Access-Control-Allow-Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "Origin, Accept",
                "X-Requested-With",
                "Access-Control-RequestForm-Method",
                "Access-Control-RequestForm-Headers",
                "X-User-Urn",
                "Link"
        ));
        corsConfiguration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}