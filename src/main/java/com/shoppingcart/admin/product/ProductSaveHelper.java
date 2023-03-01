package com.shoppingcart.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shoppingcart.admin.FileUploadUtil;
import com.shoppingcart.admin.entity.Product;

public class ProductSaveHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

	static void deleteExtraImagesWeredRemovedOnForm(Product product) {
		String extraImageDir = "product-images/" + product.getId() + "/extras";//đường dẫn đến folder extras
		Path dirPath = Paths.get(extraImageDir);
		
		try {
			Files.list(dirPath).forEach(file -> {
				String filename = file.toFile().getName();
				
				if(!product.containsImageName(filename)) {//nếu trong folder extras tồn tại file hình có tên ko nằm trong product.images -->hình này đã bị xóa trên frontend -->xóa hình này trong folder extra 
					try {
						Files.delete(file);
						LOGGER.error("Delete extra image: " + filename);
					} catch (IOException e) {
						LOGGER.error("Could not delete extra image: " + filename);
					}
				}
			});
		} catch (Exception e) {
			LOGGER.error("Could not list directory: " + dirPath);
		}
	}
	
	static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames,//lưu những extraImages đang tồn tại vào product
			Product product) {
		if (imageIDs == null || imageIDs.length == 0) return;
		
		Set<ProductImage> images = new HashSet<>();
		
		for (int count = 0; count < imageIDs.length; count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];
			
			images.add(new ProductImage(id, name, product));
		}
		
		product.setImages(images);
		
	}

	static void setProductDetails(String[] detailIDs, String[] detailNames, //lưu tất cả details
			String[] detailValues, Product product) {
		if (detailNames == null || detailNames.length == 0) return;
		
		for (int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			
			if (id != 0) {//id != 0 -->những details đã từng được save -->edit
				product.addDetail(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) { 
				product.addDetail(name, value);//những details vừa được thêm mới(có id = 0) -->create
			}
		}
	}

	static void saveUploadedImages(MultipartFile mainImageMultipart, //save mainImage vào folder product-images và extraImages vào folder extras
			MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			String uploadDir = "product-images/" + savedProduct.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}
			
		if(extraImageMultiparts.length > 0) {
			String uploadDir = "product-images/" + savedProduct.getId() + "/extras";
			
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (multipartFile.isEmpty()) continue;//nếu ko chọn hình -->multipartFile empty
				
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
	}

	static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {//lưu những extraImages được tạo mới vào product
		if (extraImageMultiparts.length > 0) {
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					
					if (!product.containsImageName(fileName)) {//nếu extraImage bị trùng tên với extraImage khác -->ko cho save
						product.addExtraImage(fileName);
					}
				}
			}
		}
	}

	static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
		if (!mainImageMultipart.isEmpty()) {//nếu ko chọn hình(nhấn vào <input type="file"> -->mainImageMultipart empty
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
}
