package com.shoppingcart.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shoppingcart.admin.FileUploadUtil;
import com.shoppingcart.admin.brand.BrandService;
import com.shoppingcart.admin.category.CategoryService;
import com.shoppingcart.admin.security.ShoppingUserDetails;
import com.shoppingcart.admin.entity.Brand;
import com.shoppingcart.admin.entity.Category;
import com.shoppingcart.admin.entity.Product;
import com.shoppingcart.admin.product.ProductNotFoundException;

@Controller
public class ProductController {
	
	private String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";//đường dẫn mặc định
	
	@Autowired private ProductService productService;
	
	@Autowired private BrandService brandService;
	
	@Autowired private CategoryService categoryService;
	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, 
			@Param("keyword") String keyword, @Param("categoryId") Integer categoryId) {
		
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();//đổ list categories lên dropdown
		
		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;

		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		if (categoryId != null) model.addAttribute("categoryId", categoryId);
		model.addAttribute("listCategories", listCategories);
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "products/products";		
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistingExtraImages", 0);//khi tạo mới product thì extraImage đầu tiên sẽ có numberOfExistingExtraImages = 0
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,//gán giá trị của thẻ có name="fileImage" vào mainImageMultipart 			
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,//gán giá trị của tất cả các thẻ có name="extraImage" vào extraImageMultiparts
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,//gán giá trị của tất cả các thẻ có name="detailIDs" vào detailIDs
			@RequestParam(name = "detailNames", required = false) String[] detailNames,//...
			@RequestParam(name = "detailValues", required = false) String[] detailValues,//...
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,//...
			@RequestParam(name = "imageNames", required = false) String[] imageNames,//...
			@AuthenticationPrincipal ShoppingUserDetails loggedUser
			) throws IOException {
		
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
			if (loggedUser.hasRole("Salesperson")) {
				productService.saveProductPrice(product);
				ra.addFlashAttribute("message", "The product has been saved successfully.");			
				return defaultRedirectURL;
			}
		}
		
		ProductSaveHelper.setMainImageName(mainImageMultipart, product);//save mainImage
		ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);//save extraImages đang tồn tại
		ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);//save extraImages vừa thêm mới
		ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);//save product details
		
		Product savedProduct = productService.save(product);//save product -->details và extraImages bên trong product cũng sẽ được save luôn.
		
		ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);//save mainImage và extraImages vào folder tương ứng 
		
		ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);//xóa extraImages đã bị xóa trên frontend
			
		ra.addFlashAttribute("message", "The product has been saved successfully.");
		
		return defaultRedirectURL;
	}

	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Product ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, 
			Model model, RedirectAttributes redirectAttributes) {
		try {
			productService.delete(id);
			String productExtraImagesDir = "../product-images/" + id + "/extras";
			String productImagesDir = "../product-images/" + id;
			
			FileUploadUtil.removeDir(productExtraImagesDir);//xóa folder chứa extraImges
			FileUploadUtil.removeDir(productImagesDir);//xóa folder chứa mainImage
			
			redirectAttributes.addFlashAttribute("message", 
					"The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra, @AuthenticationPrincipal ShoppingUserDetails loggedUser) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();//số lượng extraImages đang có
			
			boolean isReadOnlyForSalesperson = false;
			
			if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
				if (loggedUser.hasRole("Salesperson")) {
					isReadOnlyForSalesperson = true;
				}
			}
			
			model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);//numberOfExistingExtraImages = số lượng extraImages đang có
			
			return "products/product_form";
			
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
			
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra) {
		try {
			Product product = productService.get(id);//lấy ra product theo id để hiển thị lên modal
			model.addAttribute("product", product);
			
			return "products/product_detail_modal";
			
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
			
			return defaultRedirectURL;
		}
	}	
}
