package com.shoppingcart.admin.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.shoppingcart.admin.entity.Role;
import com.shoppingcart.admin.entity.User;

public class ShoppingUserDetails implements UserDetails {//implements Interface UserDetails để @Override lại các phương thức liên quan đến user

	private static final long serialVersionUID = 1L;

	private User user;

	public ShoppingUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {//lấy ra các roles của user
		Set<Role> roles = user.getRoles();//vì user và role có mối quan hệ @ManyToMany nên từ user có thể lấy ra tất cả các roles thuộc về user này, lưu ý trong role phải khai báo @ManyToMany(fetch = FetchType.EAGER)

		List<SimpleGrantedAuthority> authories = new ArrayList<>();

		for (Role role : roles) {
			authories.add(new SimpleGrantedAuthority(role.getName()));//tạo đối tượng SimpleGrantedAuthority và thêm các role vào đối tượng này
		}

		return authories;
	}

	@Override
	public String getPassword() {
		return user.getPassword();//lấy ra password của user
	}

	@Override
	public String getUsername() {
		return user.getEmail();//lấy ra email của user
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;//account sẽ ko hết hạn
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;//account sẽ ko bị khóa
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();//enabled = true -->có thể đang nhập, enabled = false -->ko thể đăng nhập
	}

	public String getFullname() {
		return this.user.getFirstName() + " " + this.user.getLastName();
	}

	public void setFirstName(String firstName) {
		this.user.setFirstName(firstName);
	}

	public void setLastName(String lastName) {
		this.user.setLastName(lastName);
	}

	public boolean hasRole(String roleName) {
		return user.hasRole(roleName);
	}
}
