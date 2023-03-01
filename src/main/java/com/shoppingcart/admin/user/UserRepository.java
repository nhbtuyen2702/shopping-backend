package com.shoppingcart.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shoppingcart.admin.entity.User;
										//PagingAndSortingRepository là đối tượng dùng để phân trang, kế thừa từ CrudRepository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {//tham số thứ 1 là entity, tham số thứ 2 là kiểu dữ liệu của khóa chính(Primary key)

	@Query("SELECT u FROM User u WHERE u.email = :email")//Spring Data JPA sẽ truy vấn theo entity và thuộc tính
	public User getUserByEmail(@Param("email") String email);//@Param("email") String email sẽ gán giá trị của biến email vào vị trí :email
	
	public Long countById(Integer id);
	
	//@Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE %?1% OR u.email LIKE %?1%")
	@Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' '," + " u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);//pageable đối tượng chứa các thông tin phân trang và sắp xếp

	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying//khi INSERT/UPDATE/DELETE thì bắt buộc khai báo @Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);//dùng ?1 để gán giá trị của tham số thứ 1 vào vị trí này 

	

}
