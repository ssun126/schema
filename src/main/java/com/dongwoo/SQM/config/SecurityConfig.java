package com.dongwoo.SQM.config;


import com.dongwoo.SQM.system.handler.*;
import com.dongwoo.SQM.system.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/*@Configuration
@EnableWebSecurity
@RequiredArgsConstructor*/
public class SecurityConfig {

 /*   private final LoginService loginService;

    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    private final CustomLoginFailHandler customLoginFailHandler;

    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;*/

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
/*
    @Bean
    public SecurityFilterChain config(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        // white list (Spring Security 체크 제외 목록)
        MvcRequestMatcher[] permitAllWhiteList = {
                mvc.pattern("/"),
                mvc.pattern("/**"),
                mvc.pattern("/login"),
                mvc.pattern("/main"),
                mvc.pattern("/logout"),
                mvc.pattern("/member/**"),
                mvc.pattern("/css/**"),
                mvc.pattern("/js/**"),
                mvc.pattern("/images/**"),
                mvc.pattern("/plugin/**"),
                mvc.pattern("/font/**"),
                mvc.pattern("/excelTemplate/**"),
                mvc.pattern("/error/**"),
                mvc.pattern("/messageSource/**"),
                mvc.pattern("/favicon.ico")
        };

        // http request 인증 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(permitAllWhiteList).permitAll()
                // 사용자 삭제는 관리자 권한만 가능
                .requestMatchers("/delete").hasRole("admin")
                // 그 외 요청 체크
                .anyRequest().authenticated()
        );

        // form login 중복 실행 방지
        http.formLogin(login -> login
                .loginProcessingUrl("/login")
                .successForwardUrl("/main")
                .successHandler(customLoginSuccessHandler)
                .failureHandler(customLoginFailHandler)
        );

        // logout 설정
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        // csrf disable
        //http.csrf(AbstractHttpConfigurer::disable);

        // session management
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        );

        // entry point handler
        http.exceptionHandling(conf -> conf
                .authenticationEntryPoint(customAuthenticationEntryPointHandler)
                .accessDeniedHandler(customAccessDeniedHandler)
        );

        // build
        return http.build();
    }*/
/*
    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(bCryptPasswordEncoder(), loginService);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        CustomAuthenticationProvider authProvider = customAuthenticationProvider();
        return new ProviderManager(authProvider);
    }*/

}
