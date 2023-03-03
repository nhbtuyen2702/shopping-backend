package com.shoppingcart.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shoppingcart.admin.entity.Role;
import com.shoppingcart.admin.entity.User;

@Service//ko khai báo tên Spring Bean thì sẽ tạo Spring Bean tên là userService
@Transactional//khi thực hiện INSERT/UPDATE/DELETE bắt buộc khai báo @Transactional
public class UserService {

	public static final int USERS_PER_PAGE = 4;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> listAll() {
		return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
	}

	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {//Page là đối tượng trong Spring Data JPA
		Sort sort = Sort.by(sortField);//đối tượng sort sẽ sắp xếp các giá trị trả về theo biến sortField tăng dần(asc) hoặc giảm dần(desc)
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);//Giả sử có tổng cộng 10 records, 1 trang có 4 records -->có tổng cộng 3 trang

		if (keyword != null) {//nếu có truyền keyword thì lấy tất tìm user theo keyword
			return userRepo.findAll(keyword, pageable);
		}

		return userRepo.findAll(pageable);//tìm tất cả users nếu ko nhập keyword
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();//.findAll() trả về Iterable<Role> -->ép kiểu thành List<Role>
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);//nếu id == null -->Create, nếu id != null -->Update

		if (isUpdatingUser) {//trường hợp edit
			User existingUser = userRepo.findById(user.getId()).get();//trường hợp update thì lấy user theo id

			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());//nếu ko nhập password thì lấy password cũ để save lại
			} else {
				encodePassword(user);//nếu có nhập password thì mã hóa password
			}

		} else {
			encodePassword(user);//trường hợp create -->luôn mã hóa password
		}

		return userRepo.save(user);//save user xuống db
	}

	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());//mã hóa password bằng BCrypt
		user.setPassword(encodedPassword);
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();//lấy user theo id
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);//khai báo exception do mình tự tạo sẽ giúp cho code dễ đọc, dễ hiểu, dễ debug hơn
		}
	}

	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		if (countById == null || countById == 0) {//nếu count == null hoặc count == 0 -->ko tồn tại user với id này
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}

		userRepo.deleteById(id);
	}

	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}

	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);//lấy user theo email

		if (userByEmail == null)//nếu userByEmail == null -->ko bị trùng email
			return true;

		boolean isCreatingNew = (id == null);//trường hợp nếu trùng email thì xem xét đang create hay update

		if (isCreatingNew) {
			if (userByEmail != null)//nếu là create thì ko được trùng
				return false;
		} else {
			if (userByEmail.getId() != id) {//nếu là edit thì có thể trùng
				return false;
			}
		}

		return true;
	}

	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}

	public User updateAccount(User userInForm) {
		User userInDB = userRepo.findById(userInForm.getId()).get();

		if (!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}

		if (userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}

		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());

		return userRepo.save(userInDB);
	}

}
