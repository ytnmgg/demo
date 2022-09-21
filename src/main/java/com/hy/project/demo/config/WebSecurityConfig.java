package com.hy.project.demo.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import com.hy.project.demo.security.CustomAccessDecisionManager;
import com.hy.project.demo.security.CustomAccessDeniedHandler;
import com.hy.project.demo.security.CustomAuthenticationEntryPoint;
import com.hy.project.demo.security.CustomFilterInvocationSecurityMetadataSource;
import com.hy.project.demo.security.CustomLogoutSuccessHandler;
import com.hy.project.demo.security.CustomUserDetailsService;
import com.hy.project.demo.security.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.hy.project.demo.util.ResultUtil.CODE_401;

/**
 * spring security = 认证 + 鉴权
 * 认证：
 * 核心类：AbstractAuthenticationProcessingFilter
 *
 * 鉴权：
 * 核心类：FilterSecurityInterceptor、AccessDecisionManager
 *
 * @author rick.wl
 * @date 2021/11/04
 */
@Configuration
@EnableWebSecurity(debug=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * [暂时不用，自己做登录逻辑]
     * 自定义用户信息查询逻辑
     * 用于在登录过程中，DaoAuthenticationProvider 的 retrieveUser 方法，获取用户信息
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * token认证过滤器
     * 只做登录后的token换用户，不做登录处理
     */
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    ///**
    // * 跨域过滤器
    // */
    //@Autowired
    //private CorsFilter corsFilter;

    ///**
    // * 允许匿名访问的地址
    // */
    //@Autowired
    //private PermitAllUrlProperties permitAllUrl;

    /**
     * 匿名用户无权限处理器
     */
    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /**
     * 认证用户无权限处理器
     */
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * 自定义权限信息获取器
     */
    @Autowired
    private CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;

    /**
     * 自定义鉴权器
     */
    @Autowired
    private CustomAccessDecisionManager customAccessDecisionManager;

    /**
     * 登出处理器
     */
    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    /**
     * [暂时不用，自己做登录逻辑]
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 身份认证配置
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(bcryptPasswordEncoder());
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 注解标记允许匿名访问的url
        //ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
        // .authorizeRequests();
        //permitAllUrl.getUrls().forEach(url -> registry.antMatchers(url).permitAll());

        httpSecurity
            // CSRF禁用，因为不使用session
            .csrf().disable()
                // 认证失败处理类，在 ExceptionTranslationFilter 中处理
                .exceptionHandling()
                //AuthenticationEntryPoint 用来解决匿名用户的鉴权异常问题
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                // accessDeniedHandler 用来处理登录用户（非匿名用户）的鉴权异常问题
                .accessDeniedHandler(customAccessDeniedHandler)
            .and()
                // 基于token，所以不需要session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                // 过滤请求
                .authorizeRequests()
                // 配置了自定义鉴权，下面这些系统鉴权配置都不起作用了
                //// 对于登录login 注册register 验证码captchaImage 允许匿名用户访问,不允许已登入用户访问
                //.antMatchers("/login", "/register", "/captchaImage").anonymous()
                //// 静态资源，不管登入或不登入 都能访问
                //.antMatchers(HttpMethod.GET, "/**/*.css", "/**/*.js").permitAll()
                ////.antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/*/api-docs", "/druid/**")
                //// .permitAll()
                //// 除上面外的所有请求全部对认证登录用户开放（这里只处理认证问题，鉴权问题单独处理）
                //.antMatchers("/abb").hasRole("roo")
                .anyRequest().authenticated()
                // 鉴权，替换 FilterSecurityInterceptor 的属性，实现自定义鉴权
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>(){
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setAccessDecisionManager(customAccessDecisionManager);
                        object.setSecurityMetadataSource(customFilterInvocationSecurityMetadataSource);
                        return object;
                    }
                })
            .and()
            // [暂时不用，自己做登录逻辑]
            //    .formLogin()
            //    //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
            //    .loginPage("/login")
            //    .loginProcessingUrl("/login.json")
            //    // 自定义 认证异常 处理器，替换默认的 SimpleUrlAuthenticationFailureHandler，默认的handler的行为是跳转/login页面
            //    // 注意这里的异常处理，专用于do_login接口的登录验证流程，被 UsernamePasswordAuthenticationFilter 调用
            //    .failureHandler(new AuthenticationFailureHandler() {
            //
            //        @Override
            //        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            //            AuthenticationException exception) throws IOException, ServletException {
            //            response.setStatus(200);
            //            response.setContentType("application/json");
            //            response.setCharacterEncoding("utf-8");
            //            response.getWriter().print(JSON.toJSONString(buildErrorResult(CODE_401, "登录失败")));
            //        }
            //    })
            //    .permitAll()//和表单登录相关的接口统统都直接通过
            //.and()
            .headers().frameOptions().disable();
        // 添加Logout filter
        httpSecurity.logout().logoutUrl("/logout").logoutSuccessHandler(customLogoutSuccessHandler);
        // 添加JWT filter
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //// 添加CORS filter
        //httpSecurity.addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class);
        //httpSecurity.addFilterBefore(corsFilter, LogoutFilter.class);
    }
}
