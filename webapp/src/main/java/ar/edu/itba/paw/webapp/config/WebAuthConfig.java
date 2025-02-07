package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.security.UserDetailsService;
import ar.edu.itba.paw.webapp.security.jwt.JwtAuthenticationEntryPoint;
import ar.edu.itba.paw.webapp.security.jwt.JwtAuthenticationProvider;
import ar.edu.itba.paw.webapp.security.jwt.JwtAuthenticationTokenFilter;
import ar.edu.itba.paw.webapp.security.service.AuthenticationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.web.cors.CorsConfiguration.ALL;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ComponentScan({"ar.edu.itba.paw.webapp.auth", "ar.edu.itba.paw.webapp.security", "ar.edu.itba.paw.webapp.validation"})
@CrossOrigin(origins = {"http://localhost:4200/", "http://old-pawserver.it.itba.edu.ar/paw-2023b-02/"})
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
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

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
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

    /*
     * Spring creates a proxy around the bean to enable method-level validation.
     * However, this proxy can sometimes cause issues with dependency injection or the initialization of certain components,
     * such as our custom ConstraintValidator.
     * So the solution is to avoid using the default bean validator and instead choosing a custom one which is constraint aware.
     * */
    @Bean
    public LocalValidatorFactoryBean validatorFactoryBean(AutowireCapableBeanFactory beanFactory) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setConstraintValidatorFactory(new SpringConstraintValidatorFactory(beanFactory));
        // This is required so the Group Sequences fail instantly instead of executing the whole sequence (causing exceptions)
        factoryBean.getValidationPropertyMap().put("hibernate.validator.fail_fast", "true");
        return factoryBean;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(LocalValidatorFactoryBean validatorFactoryBean) {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validatorFactoryBean);
        return postProcessor;
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

                ////////////////////////////////////////////////////////////////////////////////////////

                // Affiliations
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API + "/" + Endpoint.AFFILIATIONS
                ).hasAnyRole(
                        UserRole.WORKER.name(), UserRole.NEIGHBOR.name(), UserRole.ADMINISTRATOR.name(), UserRole.SUPER_ADMINISTRATOR.name()
                )
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.AFFILIATIONS
                ).hasAnyRole(
                        UserRole.WORKER.name(), UserRole.SUPER_ADMINISTRATOR.name()
                )
                .antMatchers(
                        HttpMethod.PATCH,
                        "/" + Endpoint.API + "/" + Endpoint.AFFILIATIONS
                ).hasAnyRole(
                        UserRole.ADMINISTRATOR.name(), UserRole.SUPER_ADMINISTRATOR.name()
                )
                .antMatchers(
                        HttpMethod.DELETE,
                        "/" + Endpoint.API + "/" + Endpoint.AFFILIATIONS
                ).hasAnyRole(
                        UserRole.WORKER.name(), UserRole.SUPER_ADMINISTRATOR.name()
                )

                // Amenities
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.AMENITIES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.AMENITIES + "/*"
                ).access(
                "hasAnyRole('NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.AMENITIES
                ).access(
                        "hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.PATCH,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.AMENITIES + "/*"
                ).access(
                        "hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.DELETE,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.AMENITIES + "/*"
                ).access(
                        "hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )

                // Attendance, Bookings, Comments, Inquiries, Likes, Products, Requests
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.ATTENDANCE,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.ATTENDANCE + "/" + Endpoint.COUNT,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.BOOKINGS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.BOOKINGS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS + "/*/" + Endpoint.COMMENTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS + "/*/" + Endpoint.COMMENTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*/" + Endpoint.INQUIRIES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*/" + Endpoint.INQUIRIES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.LIKES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.LIKES + "/" + Endpoint.COUNT,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.REQUESTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.REQUESTS + "/" + Endpoint.COUNT,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.REQUESTS + "/*"
                ).access(
                        "hasAnyRole('NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.ATTENDANCE,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.BOOKINGS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS + "/*/" + Endpoint.COMMENTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*/" + Endpoint.INQUIRIES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.LIKES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.REQUESTS
                ).access(
                        "hasAnyRole('NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.PATCH,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*/" + Endpoint.INQUIRIES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.REQUESTS + "/*"
                ).access(
                        "hasAnyRole('NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.DELETE,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.ATTENDANCE,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.BOOKINGS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS + "/*/" + Endpoint.COMMENTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*/" + Endpoint.INQUIRIES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.LIKES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.REQUESTS + "/*"
                ).access(
                        "hasAnyRole('NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )

                // Channels, Contacts, Events
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CHANNELS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CHANNELS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CONTACTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CONTACTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.EVENTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.EVENTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.RESOURCES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.RESOURCES + "/*"
                ).access(
                        "hasAnyRole('WORKER', 'NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CHANNELS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CONTACTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.EVENTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.RESOURCES
                ).access(
                        "hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.PATCH,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CONTACTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.EVENTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.RESOURCES + "/*"
                ).access(
                        "hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.DELETE,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CONTACTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.EVENTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.RESOURCES + "/*"
                ).access(
                        "hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )

                // Departments, Neighborhoods
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API + "/" + Endpoint.DEPARTMENTS,
                        "/" + Endpoint.API + "/" + Endpoint.DEPARTMENTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*"
                ).permitAll()
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.DEPARTMENTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS
                        ).hasAnyRole(
                        UserRole.SUPER_ADMINISTRATOR.name()
                )
                .antMatchers(
                        HttpMethod.DELETE,
                        "/" + Endpoint.API + "/" + Endpoint.DEPARTMENTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*"
                ).hasAnyRole(
                        UserRole.SUPER_ADMINISTRATOR.name()
                )

                // Root Endpoint, Images, Languages, Post Statuses, Professions, Request Statuses, Shifts, Transaction Types, User Roles, Worker Roles, Worker Statuses
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API,
                        "/" + Endpoint.API + "/",
                        "/" + Endpoint.API + "/" + Endpoint.IMAGES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.LANGUAGES,
                        "/" + Endpoint.API + "/" + Endpoint.LANGUAGES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.POST_STATUSES,
                        "/" + Endpoint.API + "/" + Endpoint.POST_STATUSES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.PRODUCT_STATUSES,
                        "/" + Endpoint.API + "/" + Endpoint.PRODUCT_STATUSES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.PROFESSIONS,
                        "/" + Endpoint.API + "/" + Endpoint.PROFESSIONS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.REQUEST_STATUSES,
                        "/" + Endpoint.API + "/" + Endpoint.REQUEST_STATUSES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.SHIFTS,
                        "/" + Endpoint.API + "/" + Endpoint.SHIFTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.TRANSACTION_TYPES,
                        "/" + Endpoint.API + "/" + Endpoint.TRANSACTION_TYPES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.USER_ROLES,
                        "/" + Endpoint.API + "/" + Endpoint.USER_ROLES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.WORKER_ROLES,
                        "/" + Endpoint.API + "/" + Endpoint.WORKER_ROLES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.WORKER_STATUSES,
                        "/" + Endpoint.API + "/" + Endpoint.WORKER_STATUSES + "/*"
                ).permitAll()
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.IMAGES,
                        "/" + Endpoint.API + "/" + Endpoint.PROFESSIONS
                ).hasAnyRole(
                        UserRole.REJECTED.name(),
                        UserRole.UNVERIFIED_NEIGHBOR.name(),
                        UserRole.WORKER.name(),
                        UserRole.NEIGHBOR.name(),
                        UserRole.ADMINISTRATOR.name(),
                        UserRole.SUPER_ADMINISTRATOR.name()
                )
                .antMatchers(
                        HttpMethod.DELETE,
                        "/" + Endpoint.API + "/" + Endpoint.IMAGES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.PROFESSIONS + "/*"
                ).hasAnyRole(
                        UserRole.SUPER_ADMINISTRATOR.name()
                )

                // Posts
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS + "/" + Endpoint.COUNT,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS + "/*"
                ).access(
                        "hasAnyRole('WORKER', 'NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS
                ).access(
                        "hasAnyRole('WORKER', 'NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.DELETE,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS + "/*"
                ).access(
                        "hasAnyRole('WORKER', 'NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )

                // Reviews
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*/" + Endpoint.REVIEWS,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*/" + Endpoint.REVIEWS + "/" + Endpoint.COUNT,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*/" + Endpoint.REVIEWS + "/" + Endpoint.AVERAGE,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*/" + Endpoint.REVIEWS + "/*"
                ).hasAnyRole(
                        UserRole.WORKER.name(), UserRole.NEIGHBOR.name(), UserRole.ADMINISTRATOR.name(), UserRole.SUPER_ADMINISTRATOR.name()
                )
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*/" + Endpoint.REVIEWS
                ).hasAnyRole(
                        UserRole.NEIGHBOR.name(), UserRole.ADMINISTRATOR.name(), UserRole.SUPER_ADMINISTRATOR.name()
                )
                .antMatchers(
                        HttpMethod.DELETE,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*/" + Endpoint.REVIEWS + "/*"
                ).hasAnyRole(
                        UserRole.NEIGHBOR.name(), UserRole.ADMINISTRATOR.name(), UserRole.SUPER_ADMINISTRATOR.name()
                )

                // Tags
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.TAGS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.TAGS + "/*"
                ).access(
                        "hasAnyRole('NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.TAGS
                ).access(
                        "hasAnyRole('NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        HttpMethod.DELETE,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.TAGS + "/*"
                ).access(
                        "hasAnyRole('SUPER_ADMINISTRATOR') " + "and " + "@accessControlHelper.isNeighborhoodMember(request)"
                )

                // Users
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API + "/" + Endpoint.USERS,
                        "/" + Endpoint.API + "/" + Endpoint.USERS + "/*"
                ).hasAnyRole(
                        UserRole.REJECTED.name(),
                        UserRole.UNVERIFIED_NEIGHBOR.name(),
                        UserRole.WORKER.name(),
                        UserRole.NEIGHBOR.name(),
                        UserRole.ADMINISTRATOR.name(),
                        UserRole.SUPER_ADMINISTRATOR.name()
                )
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.USERS
                ).permitAll()
                .antMatchers(
                        HttpMethod.PATCH,
                        "/" + Endpoint.API + "/" + Endpoint.USERS + "/*"
                ).hasAnyRole(
                        UserRole.REJECTED.name(),
                        UserRole.UNVERIFIED_NEIGHBOR.name(),
                        UserRole.WORKER.name(),
                        UserRole.NEIGHBOR.name(),
                        UserRole.ADMINISTRATOR.name(),
                        UserRole.SUPER_ADMINISTRATOR.name()
                )

                // Workers
                .antMatchers(
                        HttpMethod.GET,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*"
                ).hasAnyRole(
                        UserRole.WORKER.name(),
                        UserRole.NEIGHBOR.name(),
                        UserRole.ADMINISTRATOR.name(),
                        UserRole.SUPER_ADMINISTRATOR.name()
                )
                .antMatchers(
                        HttpMethod.POST,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS
                ).permitAll()
                .antMatchers(
                        HttpMethod.PATCH,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*"
                ).hasAnyRole(
                        UserRole.WORKER.name(),
                        UserRole.NEIGHBOR.name(),
                        UserRole.ADMINISTRATOR.name(),
                        UserRole.SUPER_ADMINISTRATOR.name()
                )

                .anyRequest().denyAll();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(ALL));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
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
                "Content-Type",
                "Accept",
                "Authorization",
                "X-Requested-With", // sus
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-User-URL",
                "X-Neighborhood-URL",
                "X-Workers-Neighborhood-URL",
                "X-Access-Token",
                "X-Refresh-Token",
                "Location",
                "Link"
        ));
        corsConfiguration.setExposedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-User-URL",
                "X-Neighborhood-URL",
                "X-Workers-Neighborhood-URL",
                "X-Access-Token",
                "X-Refresh-Token",
                "Location",
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

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/static/**");
    }
}