package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@ComponentScan("ar.edu.itba.paw.webapp.auth")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    @Value("classpath:rememberme.key")
    private Resource rememberMeKey;
    @Autowired
    private UserDetailsService userDetails;

    // this defines that whenever spring has to compare passwords the strategy is to use the following encoder
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetails).passwordEncoder(passwordEncoder());
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .invalidSessionUrl("/login")
                .and().authorizeRequests()
                .antMatchers("/signup", "/login", "/signup-worker").anonymous()
                .antMatchers("/admin/**").hasRole("ADMINISTRATOR")
                .antMatchers("/unverified").hasRole("UNVERIFIED_NEIGHBOR")
                .antMatchers("/services").hasAnyRole("WORKER", "ADMINISTRATOR", "NEIGHBOR")
                .antMatchers("/rejected").hasRole("REJECTED")
                .antMatchers("/profile").permitAll()
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
            // Get the authorities of the authenticated user
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            for (GrantedAuthority authority : authorities) {
                String authorityName = authority.getAuthority();

                switch (authorityName) {
                    case "ROLE_UNVERIFIED_NEIGHBOR":
                        // Redirect to the "/unverified" page for unverified neighbors
                        String unverifiedRedirectUrl = request.getContextPath() + "/unverified";
                        response.sendRedirect(unverifiedRedirectUrl);
                        return;
                    case "ROLE_WORKER":
                        // Redirect to the "/services" page for workers
                        String workerRedirectUrl = request.getContextPath() + "/services";
                        response.sendRedirect(workerRedirectUrl);
                        return;
                    case "ROLE_REJECTED":
                        String rejectedRedirectUrl = request.getContextPath() + "/rejected";
                        response.sendRedirect(rejectedRedirectUrl);
                        return;
                }
            }

            // Default case: Redirect to the default page for other roles
            String defaultRedirectUrl = request.getContextPath() + "/";
            response.sendRedirect(defaultRedirectUrl);
        };
    }




    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/postImages/*","/resources/**","/css/**", "/js/**", "/img/**", "/favicon.ico", "/403");
    }
}