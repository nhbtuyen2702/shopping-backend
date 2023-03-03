$(document).ready(function () {
	$("#buttonCancel").on("click", function () {
		window.location = moduleURL;//window.location dùng để gọi đến 1 @GetMapping bất kỳ
	});

	$("#fileImage").change(function () {
		if (!checkFileSize(this)) {
			return;
		}

		showImageThumbnail(this);
	});
});

function showImageThumbnail(fileInput) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function (e) {
		$("#thumbnail").attr("src", e.target.result);//gán giá trị của e.target.result vào thuộc tính src -->đây là cách thay đổi hình khi chọn file hình mới
	};

	reader.readAsDataURL(file);
}

function checkFileSize(fileInput) {
	fileSize = fileInput.files[0].size;

	if (fileSize > MAX_FILE_SIZE) {
		fileInput.setCustomValidity("You must choose an image less than " + MAX_FILE_SIZE + " bytes!");//hiện lỗi bên cạnh button choose file khi chọn file hình vượt quá kích thước cho phép
		fileInput.reportValidity();

		return false;
	} else {
		fileInput.setCustomValidity("");//nếu chọn hình đúng kích thước cho phép thì xóa lỗi

		return true;
	}
}

function showModalDialog(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal();
}

function showErrorModal(message) {//modal hiển thị lỗi
	showModalDialog("Error", message);
}

function showWarningModal(message) {//modal hiển thị cảnh báo
	showModalDialog("Warning", message);
}