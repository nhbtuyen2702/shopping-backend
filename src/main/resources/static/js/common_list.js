function showDeleteConfirmModal(link, entityName) {
	entityId = link.attr("entityId");//link là 1 đối tượng JQuery -->lấy ra giá trị của thuộc tính entityId

	$("#yesButton").attr("href", link.attr("href"));//gán giá trị của thuộc tính href vào thuộc tính href của thẻ có id là yesButton

	$("#confirmText").text("Are you sure you want to delete this "
		+ entityName + " ID " + entityId + "?");//thay đổi nội dung của thẻ có id là confirmText

	$("#confirmModal").modal();//hiển thị modal
}

function clearFilter() {
	window.location = moduleURL;
}
