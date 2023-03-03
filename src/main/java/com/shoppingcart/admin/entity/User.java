package com.shoppingcart.admin.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
//Lưu ý cách đặt tên trong toàn bộ entity này, khi tạo xong table trong db thì phải alter table và sắp xếp thứ tự các cột giống thứ tự thuộc tính trong entity
@Entity//khai báo class này là 1 entity, nếu ko khai báo name cho entity thì nó lấy tên class làm tên entity(thường tên entity sẽ là tên class)
@Table(name = "users")//khai báo table tương ứng của entity này là users, nếu ko khai báo name cho table thì nó lấy tên class làm tên table(tên table là số nhiều)
public class User extends IdBasedEntity {

	@Column(length = 128, nullable = false, unique = true)//length: độ dài lưu trữ, nullable: có cho save null hay ko, unique: có cho phép trùng hay ko
	private String email;

	@Column(length = 64, nullable = false)
	private String password;

	@Column(name = "first_name", length = 45, nullable = false)//khai báo tên cột là first_name, nếu ko khai báo sẽ lấy tên thuộc tính làm tên cột(mỗi từ trong tên cột được ngăn bởi dấu _)
	private String firstName;

	@Column(name = "last_name", length = 45, nullable = false)
	private String lastName;

	@Column(length = 64)
	private String photos;

	private boolean enabled;

	@ManyToMany(fetch = FetchType.EAGER)//fetch = FetchType.EAGER, nếu lấy user thì cũng sẽ lấy toàn bộ roles thuộc về user đó
	@JoinTable(name = "users_roles", //tạo ra table tên là users_roles, users_roles chứa khóa ngoại user_id trỏ đến id của table users và role_id trỏ đến id của table roles
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();//dùng Set ko dùng List

	public User() {
	}

	public User(String email, String password, String firstName, String lastName) {
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void addRole(Role role) {
		this.roles.add(role);
	}

	@Override
	public String toString() {//System.out.println(user) sẽ chạy vào phương thức này
		return "User [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", roles=" + roles + "]";
	}

	@Transient//@Transient sẽ bị bỏ qua khi làm việc với entity user
	public String getFullName() {
		return firstName + " " + lastName;
	}

	@Transient
	public String getPhotosImagePath() {
		if (id == null || photos == null)
			return "/images/default-user.png";//nếu id == null thì hiển thị hình mặc định
		return "/user-photos/" + this.id + "/" + this.photos;//đường dẫn đến file hình
	}

	public boolean hasRole(String roleName) {
		Iterator<Role> iterator = roles.iterator();

		while (iterator.hasNext()) {
			Role role = iterator.next();
			if (role.getName().equals(roleName)) {
				return true;
			}
		}

		return false;
	}
}
