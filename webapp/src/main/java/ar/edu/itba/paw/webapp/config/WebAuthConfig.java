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
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ComponentScan({"ar.edu.itba.paw.webapp.auth","ar.edu.itba.paw.webapp.security"})
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

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(neighborhoodAccessControlFilter(), UsernamePasswordAuthenticationFilter.class)
                *//*.addFilterBefore(httpMethodFilter(), FilterSecurityInterceptor.class)*//*
                .authorizeRequests()
                .antMatchers("/auth").permitAll()
                .antMatchers("/","/images/*", "/departments", "/professions", "/shifts", "/times", "/neighborhoods", "/neighborhoods/*").permitAll()
                .anyRequest().authenticated();
    }*/

    @Override
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
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/postImages/*", "/resources/**", "/css/**", "/js/**", "/img/**", "/favicon.ico", "/403");
    }
}