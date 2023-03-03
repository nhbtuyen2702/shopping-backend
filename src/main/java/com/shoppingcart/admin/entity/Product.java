package com.shoppingcart.admin.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shoppingcart.admin.entity.Brand;
import com.shoppingcart.admin.entity.Category;
import com.shoppingcart.admin.entity.IdBasedEntity;
import com.shoppingcart.admin.product.ProductDetail;
import com.shoppingcart.admin.product.ProductImage;

@Entity
@Table(name = "products")
public class Product extends IdBasedEntity {
	
	@Column(unique = true, length = 255, nullable = false)
	private String name;
	
	@Column(unique = true, length = 255, nullable = false)
	private String alias;
	
	@Column(length = 512, nullable = false, name = "short_description")
	private String shortDescription;
	
	@Column(length = 4096, nullable = false, name = "full_description")
	private String fullDescription;
	
	@Column(name = "main_image", nullable = false)
	private String mainImage;
	
	@Column(name = "created_time", nullable = false, updatable = false)
	private Date createdTime;//java.util.Date;
	
	@Column(name = "updated_time")
	private Date updatedTime;//java.util.Date;
	
	private boolean enabled;
	
	@Column(name = "in_stock")
	private boolean inStock;
	
	private float cost;
	
	private float price;
	
	@Column(name = "discount_percent")
	private float discountPercent;
	
	private float length;
	private float width;
	private float height;
	private float weight;
	
	@ManyToOne
	@JoinColumn(name = "brand_id")	
	private Brand brand;
		
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)//cascade = CascadeType.ALL -->khi thực hiện repo.save(product) thì nó sẽ thực hiện save tất cả các productImages, productDetails bên trong product  
	private Set<ProductImage> images = new HashSet<>();//vì 1 product có nhiều extraImages nên tạo 1 đối tượng productImage có mối quan hệ @ManyToOne với product
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductDetail> details = new ArrayList<>();//vì 1 product có nhiều details nên tạo 1 đối tượng productDetail có mối quan hệ @ManyToOne với product

	public Product(Integer id) {
		this.id = id;
	}

	public Product() {
	}
	
	public Product(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isInStock() {
		return inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(float discountPercent) {
		this.discountPercent = discountPercent;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + "]";
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public Set<ProductImage> getImages() {
		return images;
	}

	public void setImages(Set<ProductImage> images) {
		this.images = images;
	}
	
	public void addExtraImage(String imageName) {
		this.images.add(new ProductImage(imageName, this));
	}
	
	@Transient
	public String getMainImagePath() {
		if (id == null || mainImage == null) return "/images/image-thumbnail.png";
		
		return "/product-images/" + this.id + "/" + this.mainImage;
	}

	public List<ProductDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ProductDetail> details) {
		this.details = details;
	}
	
	public void addDetail(String name, String value) {
		this.details.add(new ProductDetail(name, value, this));//productDetail chưa từng được save xuống db
	}

	public void addDetail(Integer id, String name, String value) {
		this.details.add(new ProductDetail(id, name, value, this));//productDetail đã từng được save xuống db
	}
	
	public boolean containsImageName(String imageName) {//nếu extraImage có name trùng với extraImage đang tồn tại thì ko cho save
		Iterator<ProductImage> iterator = images.iterator();
		
		while (iterator.hasNext()) {
			ProductImage image = iterator.next();
			if (image.getName().equals(imageName)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Transient
	public String getShortName() {//hiển thị trên list categories tên ngắn lại(70 ký tự)
		if (name.length() > 70) {
			return name.substring(0, 70).concat("...");
		}
		return name;
	}
	
	@Transient
	public float getDiscountPrice() {
		if (discountPercent > 0) {
			return price * ((100 - discountPercent) / 100);
		}
		return this.price;
	}

}
