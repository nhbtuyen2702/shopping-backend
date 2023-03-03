dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function() {
	
	$("#shortDescription").richText();//<textarea th:field="*{shortDescription}"/>
	$("#fullDescription").richText();//<textarea th:field="*{fullDescription}"/>
	
	dropdownBrands.change(function() {
		dropdownCategories.empty();//khi thay đổi dropdown Brand thì xóa tất cả các giá trị trong dropdown Category
		getCategories();
	});	
	
	getCategoriesForNewForm();

});

function getCategoriesForNewForm() {
	catIdField = $("#categoryId");
	editMode = false;
	
	if (catIdField.length) {
		editMode = true;
	}
	
	if (!editMode) getCategories();//trường hợp edit thì lần đầu ko cần load category theo brand, vì bên trong product đã có brand và category rồi
}

function getCategories() {
	brandId = dropdownBrands.val(); 
	url = brandModuleURL + "/" + brandId + "/categories";
	
	$.get(url, function(responseJson) {//dùng Ajax JQuery để gọi xuống controller /brands/1/categories
		$.each(responseJson, function(index, category) {
			$("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);//tạo ra thẻ <option> và lần lượt gán giá trị trả về từ controller, sau đó append vào dropdownCategories 
		});			
	});
}

function checkUnique(form) {
	productId = $("#id").val();
	productName = $("#name").val();
	
	csrfValue = $("input[name='_csrf']").val();
	
	params = {id: productId, name: productName, _csrf: csrfValue};
	
	$.post(checkUniqueUrl, params, function(response) {
		if (response == "OK") {
			form.submit();
		} else if (response == "Duplicate") {
			showWarningModal("There is another product having the name " + productName);	
		} else {
			showErrorModal("Unknown response from server");
		}
		
	}).fail(function() {
		showErrorModal("Could not connect to the server");
	});
	
	return false;
}	