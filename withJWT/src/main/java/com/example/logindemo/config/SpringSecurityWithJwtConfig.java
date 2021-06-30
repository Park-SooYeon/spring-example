package com.example.logindemo.config;

import com.example.logindemo.jwt.JwtAuthenticationEntryPoint;
import com.example.logindemo.jwt.JwtRequestFilter;
import com.example.logindemo.jwt.JwtUserDetailsService;
import com.example.logindemo.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class SpringSecurityWithJwtConfig extends WebSecurityConfigurerAdapter {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // 인증에 사용할 유저 정보 (db에 저장)
        auth
                .jdbcAuthentication().dataSource(dataSource) // 연결 DB 설정 (default : h2)
                .and()
                .userDetailsService(jwtUserDetailsService) // userDetails 가져올 때, 사용할 비즈니스 로직 구현체
                .passwordEncoder(passwordEncoder); // 비밀번호 암호화 bcrypt 사용
    }

    // Secure the endpoints with JWT authentication
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // roles별 권한 세팅
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/books/**").hasAuthority(Role.USER.getValue())
                .antMatchers(HttpMethod.POST, "/books").hasAuthority(Role.ADMIN.getValue())
                .antMatchers(HttpMethod.PUT, "/books/**").hasAuthority(Role.ADMIN.getValue())
                .antMatchers(HttpMethod.PATCH, "/books/**").hasAuthority(Role.ADMIN.getValue())
                .antMatchers(HttpMethod.DELETE, "/books/**").hasAuthority(Role.ADMIN.getValue())
                .anyRequest().permitAll() // 나머지 요청은 누구나 접근 가능
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter 실행 전, 실행할 필터 설정
                .formLogin().disable() // formLogin 인증 사용 안함
                .httpBasic().disable() // http basic 인증 사용 안함 (rest api만을 고려)
                .csrf().disable() // csrf 사용 안함
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 토큰 기반 인증이므로 세션 사용 X
    }
}
