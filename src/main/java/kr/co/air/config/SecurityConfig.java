package kr.co.air.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final LoginFailHandler loginFailHandler;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.csrf(csrf -> csrf
                .ignoringRequestMatchers("/air-faq/**")  // FAQ 관련 모든 경로 CSRF 제외
                .disable()
            )
        	.formLogin(formLogin -> formLogin
                    .loginPage("/login")
                    .loginProcessingUrl("/login")  // 명시적으로 추가
                    .failureHandler(loginFailHandler)
                    .defaultSuccessUrl("/admin_list", true)
                    .permitAll()
                )
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                    .invalidateHttpSession(true)
                )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/image/**", "/ico/**", "/static/**").permitAll()
                // 회원가입 페이지 경로와 아이디 중복확인 API 경로는 모두 허용
                .requestMatchers("/index","/add_master", "/login", "/admin_req", "/admin/check_id", "/air-**", "/api/resv/**", "/ticket/**").permitAll()
                
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/notice/delete/**").hasRole("관리자")
                .requestMatchers("/admin_list").authenticated() 
                .anyRequest().authenticated()
            );
            
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}