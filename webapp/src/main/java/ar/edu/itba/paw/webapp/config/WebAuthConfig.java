package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.security.UserDetailsService;
import ar.edu.itba.paw.webapp.security.jwt.JwtAuthenticationEntryPoint;
import ar.edu.itba.paw.webapp.security.jwt.JwtAuthenticationProvider;
import ar.edu.itba.paw.webapp.security.jwt.JwtAuthenticationTokenFilter;
import ar.edu.itba.paw.webapp.security.service.AuthenticationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
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

                // Public endpoints
                .antMatchers(
                        // Root
                        "/" + Endpoint.API,"/" + Endpoint.API + "/",
                        "/" + Endpoint.API + "/" + Endpoint.DEPARTMENTS, "/" + Endpoint.API + "/" + Endpoint.DEPARTMENTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.LANGUAGES, "/" + Endpoint.API + "/" + Endpoint.LANGUAGES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.POST_STATUSES, "/" + Endpoint.API + "/" + Endpoint.POST_STATUSES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.PRODUCT_STATUSES, "/" + Endpoint.API + "/" + Endpoint.PRODUCT_STATUSES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.PROFESSIONS, "/" + Endpoint.API + "/" + Endpoint.PROFESSIONS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.REQUEST_STATUSES, "/" + Endpoint.API + "/" + Endpoint.REQUEST_STATUSES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.SHIFTS, "/" + Endpoint.API + "/" + Endpoint.SHIFTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.TRANSACTION_TYPES, "/" + Endpoint.API + "/" + Endpoint.TRANSACTION_TYPES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.USER_ROLES, "/" + Endpoint.API + "/" + Endpoint.USER_ROLES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.WORKER_ROLES, "/" + Endpoint.API + "/" + Endpoint.WORKER_ROLES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.WORKER_STATUSES, "/" + Endpoint.API + "/" + Endpoint.WORKER_STATUSES + "/*",

                        "/" + Endpoint.API + "/" + Endpoint.IMAGES, "/" + Endpoint.API + "/" + Endpoint.IMAGES + "/*", // debatable, could be private
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS, "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*", // for signup
                        "/" + Endpoint.API + "/" + Endpoint.USERS, // for signup
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS // for signup
                ).permitAll()

                // Registered Users Endpoints
                .antMatchers(
                        "/" + Endpoint.API + "/" + Endpoint.USERS + "/*" // All users with an account can change their profile
                ).hasAnyRole(
                        UserRole.REJECTED.name(),
                        UserRole.UNVERIFIED_NEIGHBOR.name(),
                        UserRole.WORKER.name(),
                        UserRole.NEIGHBOR.name(),
                        UserRole.ADMINISTRATOR.name(),
                        UserRole.SUPER_ADMINISTRATOR.name()
                )

                // Worker Related Endpoints
                .antMatchers(
                        "/" + Endpoint.API + "/" + Endpoint.AFFILIATIONS, "/" + Endpoint.API + "/" + Endpoint.AFFILIATIONS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*/" + Endpoint.REVIEWS,
                        "/" + Endpoint.API + "/" + Endpoint.WORKERS + "/*/" + Endpoint.REVIEWS + "/*"
                ).hasAnyRole(
                        UserRole.WORKER.name(),
                        UserRole.NEIGHBOR.name(),
                        UserRole.ADMINISTRATOR.name(),
                        UserRole.SUPER_ADMINISTRATOR.name()
                )

                // Endpoints used in Neighborhoods and Workers Neighborhood
                .antMatchers(
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS + "/**",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CHANNELS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CHANNELS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.EVENTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.EVENTS + "/*"
                ).access(
                        "hasAnyRole('WORKER', 'NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " +
                                "and " +
                                "@pathAccessControlHelper.isNeighborhoodMember(request)"
                )

                // Endpoints used in Neighborhoods only
                .antMatchers(
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS + "/*/" + Endpoint.COMMENTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.POSTS + "/*/" + Endpoint.COMMENTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*/" + Endpoint.INQUIRIES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.PRODUCTS + "/*/" + Endpoint.INQUIRIES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.REQUESTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.REQUESTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.TAGS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.TAGS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.AMENITIES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.AMENITIES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.BOOKINGS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.BOOKINGS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.RESOURCES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.RESOURCES + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CONTACTS,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.CONTACTS + "/*",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.LIKES,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.LIKES + "/**",
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.ATTENDANCE,
                        "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/*/" + Endpoint.ATTENDANCE + "/**"
                ).access(
                        "hasAnyRole('NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " +
                                "and " +
                                "@pathAccessControlHelper.isNeighborhoodMember(request)"
                )

                .anyRequest().denyAll();
    }


    // WORKING VERSION
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(ALL));
        // configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://your-production-url.com"));
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