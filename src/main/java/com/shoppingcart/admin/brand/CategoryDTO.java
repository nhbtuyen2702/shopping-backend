package com.shoppingcart.admin.brand;

public class CategoryDTO {//DTO: Data Transfer Object -->đây chỉ là đối tượng dùng để lưu trữ dữ liệu tạm thời -->ko có table tương ứng trong database
	
	private Integer id;
	private String name;

	public CategoryDTO() {
	}

	public CategoryDTO(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
