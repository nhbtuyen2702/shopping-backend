package com.shoppingcart.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shoppingcart.admin.entity.Category;

@Service
@Transactional
public class CategoryService {

	public static final int ROOT_CATEGORIES_PER_PAGE = 4;

	@Autowired
	private CategoryRepository repo;

	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
		Sort sort = Sort.by("name");

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);//1 trang sẽ có 4 root categories -->có nghĩa là 4 categories cao nhất(ko có cha) 

		Page<Category> pageCategories = null;

		if (keyword != null && !keyword.isEmpty()) {
			pageCategories = repo.search(keyword, pageable);//nếu có nhập keyword thì tìm theo keyword, lưu ý khi tìm theo keyword sẽ ko cần phân cấp theo thứ tự các categories
		} else {
			pageCategories = repo.findRootCategories(pageable);//ko nhập keyword thì tìm tất cả, sẽ phân cấp theo thứ tự các categories
		}

		List<Category> rootCategories = pageCategories.getContent();

		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());

		if (keyword != null && !keyword.isEmpty()) {
			List<Category> searchResult = pageCategories.getContent();//nếu có nhập keyword thì sẽ phân trang như users -->ko cần phân cấp theo thứ tự và ko cần thêm dấu -- vào mỗi cấp độ
			for (Category category : searchResult) {
				category.setHasChildren(category.getChildren().size() > 0);//true -->có con, false -->ko có con
			}

			return searchResult;

		} else {
			return listHierarchicalCategories(rootCategories, sortDir);//ko nhập keyword thì tìm tất cả, phân cấp theo thứ tự và thêm dấu -- vào mỗi cấp độ
		}
	}

	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {//phương thức dùng cho list categories, sẽ trả về tất cả các thuộc tính của category
		List<Category> hierarchicalCategories = new ArrayList<>();

		for (Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));//copy tất cả thuộc tính và set giá trị cho hasChildren

			Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);//lấy ra các category con trực tiếp và sắp xếp theo name tăng dần hoặc giảm dần

			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));

				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);//dủng đệ quy để lấy ra tất cả category con, cháu....
			}
		}

		return hierarchicalCategories;
	}

	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		int newSubLevel = subLevel + 1;//level 1: --, level 2: ----, level 3: ------,...

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();

			hierarchicalCategories.add(Category.copyFull(subCategory, name));

			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		}

	}

	public Category save(Category category) {
		Category parent = category.getParent();
		if (parent != null) {
			String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIds);
		}
		
		return repo.save(category);
	}

	public List<Category> listCategoriesUsedInForm() {//phương thức dùng cho form category, sẽ trả id và name của category
		List<Category> categoriesUsedInForm = new ArrayList<>();

		Iterable<Category> categoriesInDB = repo.findRootCategories(Sort.by("name").ascending());

		for (Category category : categoriesInDB) {
			categoriesUsedInForm.add(Category.copyIdAndName(category));//chỉ copy thuộc tính id và name -->vì chỉ cần hiển thị lên dropdown

			Set<Category> children = sortSubCategories(category.getChildren());

			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

				listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
			}
		}

		return categoriesUsedInForm;
	}

	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren());

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();

			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

			listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
		}
	}

	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("Could not find any category with ID " + id);
		}
	}

	public void delete(Integer id) throws CategoryNotFoundException {
		Long countById = repo.countById(id);
		if (countById == null || countById == 0) {
			throw new CategoryNotFoundException("Could not find any category with ID " + id);
		}

		repo.deleteById(id);
	}

	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}

	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);

		Category categoryByName = repo.findByName(name);

		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = repo.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}

			Category categoryByAlias = repo.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}

		}

		return "OK";
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {//Anonymous function, vì đây là TreeSet nên phải khai báo tiêu chí sắp xếp các đối tượng category cho nó, nếu ko sẽ báo lỗi vì ko biết sắp xếp theo tiêu chí gì
			@Override
			public int compare(Category cat1, Category cat2) {//phương thức compare trả về int
				if (sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());//name là String -->phải dùng compareTo để so sánh, vì compareTo trả về int(ko dùng .equals vì .equals trả về boolean)
				} else {
					return cat2.getName().compareTo(cat1.getName());
				}
			}
		});

		sortedChildren.addAll(children);

		return sortedChildren;
	}

}
