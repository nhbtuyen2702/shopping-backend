package com.shoppingcart.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration//@Configuration + @Bean -->khởi tạo Spring Bean -->để các Spring Bean khác có thể @Autowired
@EnableWebSecurity//kích hoạt Spring Security
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {//kế thừa WebSecurityConfigurerAdapter để @Override phương thức configure

	@Bean
	public UserDetailsService userDetailsService() {
		return new ShoppingUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());//gán đối tượng shoppingUserDetailsService -->khi user bấm submit form login nó sẽ vào đối tượng này để kiểm tra email và password
		authProvider.setPasswordEncoder(passwordEncoder());//gán đối tượng bCryptPasswordEncoder -->nó sẽ so sánh password sau khi giải mã có giống với password user nhập hay ko

		return authProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());//gán đối tượng daoAuthenticationProvider
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {//cấu hình xác thực(authentication) và phân quyền(authorization)
		http.authorizeRequests()
					.antMatchers("/users/**").hasAuthority("Admin")//muốn gửi request /users/** thì phải đăng nhập thành công và có role Admin(phân quyền)
					.antMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
					
					.antMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
					
					.antMatchers("/products/edit/**", "/products/save", "/products/check_unique")
						.hasAnyAuthority("Admin", "Editor", "Salesperson")
						
					.antMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
						.hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
						
					.antMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
					
					.antMatchers("/products/detail/**").hasAnyAuthority("Admin", "Editor", "Salesperson", "Assistant")
			
				.anyRequest().authenticated()//tất cả các request đều phải dăng nhập(xác thực)
				.and()
				.formLogin()			
					.loginPage("/login")//khi cần login thì nó gửi request /login
					.usernameParameter("email")//<input type="email" name="email" /> -->khai báo giá trị của thuộc tính name
					.permitAll()//có thể gửi request /login mà ko cần đăng nhập
				.and().logout().permitAll()
				.and()
					.rememberMe()//khi tắt server thì user ko cần đăng nhập lại
						.key("AbcDefgHijKlmnOpqrs_1234567890")//key là chuỗi bất kỳ, khi user đăng nhập thành công và có checked rememberMe, nó sẽ lấy chuỗi này mã hóa và lưu vào trong cookies, đó là lý do user ko cần dăng nhập lại, khi hết hạn hoặc server thay đổi chuỗi key thì user phải đăng nhập lại
						.tokenValiditySeconds(7 * 24 * 60 * 60);//ko cần đăng nhập trong 7 ngày
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");//có thể truy cập các folder images, folder js, folder webjars mà ko cần đăng nhập
	}

}
