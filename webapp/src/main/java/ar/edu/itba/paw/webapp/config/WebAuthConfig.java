package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.auth.UserDetailsService;
import ar.edu.itba.paw.webapp.security.api.jwt.JwtAuthenticationEntryPoint;
import ar.edu.itba.paw.webapp.security.api.jwt.JwtAuthenticationProvider;
import ar.edu.itba.paw.webapp.security.api.jwt.JwtAuthenticationTokenFilter;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
import java.util.Collection;
import java.util.Collections;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.web.cors.CorsConfiguration.ALL;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ComponentScan({"ar.edu.itba.paw.webapp.auth", "ar.edu.itba.paw.webapp.security", "ar.edu.itba.paw.webapp.validation"})
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
        // This is required so the Group Sequences fail instantly instead of executing the whole sequence
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
                        "/",
                        "/departments", "/departments/*",
                        "/languages", "/languages/*",
                        "/post-statuses", "/post-statuses/*",
                        "/product-statuses", "/product-statuses/*",
                        "/professions", "/professions/*",
                        "/request-statuses", "/request-statuses/*",
                        "/shifts", "/shifts/*",
                        "/shift-statuses", "/shift-statuses/*",
                        "/transaction-types", "/transaction-types/*",
                        "/user-roles", "/user-roles/*",
                        "/worker-roles", "/worker-roles/*",
                        "/worker-statuses", "/worker-statuses/*",
                        "/neighborhoods", "/neighborhoods/*",
                        // User and Worker Creation can be accessed by anyone
                        // User and Worker List share the same endpoint so they have additional authentication
                        "/neighborhoods/*/users",
                        "/workers"
                ).permitAll()

                // Registered Users Endpoints
                .antMatchers(
                        "/neighborhoods/*/users/*",
                        "/workers/*",

                        "/images", "/images/*",
                        "/affiliations", "/affiliations/*",
                        "/workers/*/reviews", "/workers/*/reviews/*"
                ).hasAnyRole(
                        UserRole.REJECTED.name(),
                        UserRole.UNVERIFIED_NEIGHBOR.name(),
                        UserRole.WORKER.name(),
                        UserRole.NEIGHBOR.name(),
                        UserRole.ADMINISTRATOR.name(),
                        UserRole.SUPER_ADMINISTRATOR.name()
                )

                // Neighborhood Specific Endpoints
                .antMatchers(
                        "/likes", "/likes/**"
                ).hasAnyRole(
                        UserRole.NEIGHBOR.name(),
                        UserRole.ADMINISTRATOR.name(),
                        UserRole.SUPER_ADMINISTRATOR.name()
                )
                .antMatchers(
                        "/neighborhoods/*/posts", "/neighborhoods/*/posts/*"
                ).access(
                        "hasAnyRole('WORKER', 'NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " +
                                "and " +
                                "@pathAccessControlHelper.isNeighborhoodMember(request)"
                )
                .antMatchers(
                        "/neighborhoods/*/posts/*/comments", "/neighborhoods/*/posts/*/comments/*",
                        "/neighborhoods/*/products", "/neighborhoods/*/products/*",
                        "/neighborhoods/*/products/*/inquiries", "/neighborhoods/*/products/*/inquiries/*",
                        "/neighborhoods/*/requests", "/neighborhoods/*/requests/*",
                        "/neighborhoods/*/tags", "/neighborhoods/*/tags/*",
                        "/neighborhoods/*/channels", "/neighborhoods/*/channels/*",
                        "/neighborhoods/*/amenities", "/neighborhoods/*/amenities/*",
                        "/neighborhoods/*/bookings", "/neighborhoods/*/bookings/*",
                        "/neighborhoods/*/resources", "/neighborhoods/*/resources/*",
                        "/neighborhoods/*/contacts", "/neighborhoods/*/contacts/*",
                        "/neighborhoods/*/events", "/neighborhoods/*/events/*",
                        "/neighborhoods/*/events/*/attendance", "/neighborhoods/*/events/*/attendance/*"
                ).access(
                        "hasAnyRole('NEIGHBOR', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR') " +
                                "and " +
                                "@pathAccessControlHelper.isNeighborhoodMember(request)"
                )

                .anyRequest().denyAll();
    }

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
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers("/postImages/*", "/resources/**", "/css/**", "/js/**", "/img/**", "/favicon.ico", "/403");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(ALL));
//        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200/"));
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