package com.dongwoo.SQM.config.security;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

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
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/css/**", "/js/**", "/plugin/**","/images/**", "/font/**", "/favicon.ico").permitAll() //resource 허용
                        .requestMatchers("/", "/login", "/join","/member/**","/siteMgr/**","/userMgr/**").permitAll()
                        // 관리자 권한만 가능
                        //.requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/main", true)
                        .successHandler(new LoginSuccessHandler("/main"))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // 로그아웃 후 리다이렉트할 URL
                        .invalidateHttpSession(true) // 세션 무효화
                        .permitAll())
                .sessionManagement((auth)->auth
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)); // 추가 로그인 차단

        return http.build();
    }

}

   /* private final LoginService loginService;

    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    private final CustomLoginFailHandler customLoginFailHandler;

    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        // white list (Spring Security 체크 제외 목록)
        MvcRequestMatcher[] permitAllWhiteList = {
                mvc.pattern("/"),
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
        // csrf disable
        http.csrf(AbstractHttpConfigurer::disable);
        // http request 인증 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                .requestMatchers("/user/**").permitAll() // '/user/'로 시작하는 요청 모두 접근 허가
                .requestMatchers(permitAllWhiteList).permitAll()
                // 사용자 삭제는 관리자 권한만 가능
                .requestMatchers("/delete").hasRole("admin")
                // 그 외 요청 체크
                .anyRequest().authenticated()
        ).formLogin(formLogin -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/main")
                .successHandler(customLoginSuccessHandler)
                .failureHandler(customLoginFailHandler)
        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        // session management
        *//*http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        );*//*

        // entry point handler
     *//*   http.exceptionHandling(conf -> conf
                .authenticationEntryPoint(customAuthenticationEntryPointHandler)
                .accessDeniedHandler(customAccessDeniedHandler)
        );*//*

        // build
        return http.build();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(bCryptPasswordEncoder(), loginService);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        CustomAuthenticationProvider authProvider = customAuthenticationProvider();
        return new ProviderManager(authProvider);
    }
*/

