package com.dongwoo.SQM.config;


import com.dongwoo.SQM.config.security.FormAuthenticationDetailSource;
import com.dongwoo.SQM.config.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationDetailsSource authenticationDetailsSource;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectPostProcessor<Object> objectPostProcessor;
    private final AuthenticationFailureHandler customFailureHandler;

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless APIs
                .headers(headers ->
                            headers.xssProtection(
                                    xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                            ).contentSecurityPolicy(csp -> csp
                                    .policyDirectives("default-src 'self'; img-src 'self' data: https:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https:; style-src 'self' 'unsafe-inline' https:")
                                    //.reportOnly()
                            )
                        )
                .headers(
                        headersConfigurer ->
                                headersConfigurer
                                        .frameOptions(
                                                HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                        )
                )
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/css/**", "/js/**", "/plugin/**","/images/**", "/font/**", "/favicon.ico").permitAll() //resource 허용
                        .requestMatchers("/", "/login", "/auth/login", "/member/**", "/multiLanguage/getLocalStorage").permitAll()
                        .requestMatchers(HttpMethod.POST, "/sendIsoAuthData", "/multiLanguage/saveMultiLanguage", "/findDW", "/findCompanyID", "/findCompanyPW", "/otp", "/otpSend", "/otpOk" , "/idpwCheck").permitAll()  // Allow POST without authentication
                        // 관리자 권한만 가능
                        //.requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/auth/login")
                        .authenticationDetailsSource(authenticationDetailsSource)
                        .failureForwardUrl("/loginError")
                        .failureHandler(customFailureHandler)
                        .successHandler(new LoginSuccessHandler("/main"))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // 로그아웃 후 리다이렉트할 URL
                        .invalidateHttpSession(true) // 세션 무효화
                        .permitAll())
                .securityContext((securityContext) -> securityContext
                        .requireExplicitSave(true)
                )

                .sessionManagement((auth)->auth
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1) // 추가 로그인 차단
                        .maxSessionsPreventsLogin(false));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://example.com"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setSameSite("Lax");  // SameSite 속성 설정 (Strict, Lax, None)
        cookieSerializer.setCookieName("SESSIONID");  // 세션 쿠키 이름
     //   cookieSerializer.setSecure(true);  // HTTPS만 사용하여 쿠키 전송
      //  cookieSerializer.setHttpOnly(true);  // HttpOnly 속성 설정
        return cookieSerializer;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
