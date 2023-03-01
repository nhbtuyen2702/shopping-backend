package com.shoppingcart.admin.user;

import org.springframework.data.repository.CrudRepository;

import com.shoppingcart.admin.entity.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {//tham số thứ 1 là entity, tham số thứ 2 là kiểu dữ liệu của khóa chính(Primary key)

}
