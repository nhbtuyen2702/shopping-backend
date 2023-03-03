var extraImagesCount = 0;//biến này dùng để xác định có đang chọn hình extraImage cuối cùng hay ko

$(document).ready(function() {	
	$("input[name='extraImage']").each(function(index) {//lấy ra tất cả thẻ <input type="file" name="extraImage"/> và bắt sự kiện cho các thẻ này
		extraImagesCount++;//có bao nhiêu extraImage thì sẽ tăng lên bấy nhiêu -->extraImagesCount là tổng số extraImage
		
		$(this).change(function() {
			if (!checkFileSize(this)) {
				return;
			}			
			showExtraImageThumbnail(this, index);//khi chọn hình thì load hình vào thẻ có id là extraThumbnail0 hoặc extraThumbnail1,...
		});
	});
	
	$("a[name='linkRemoveExtraImage']").each(function(index) {//tất cả extraImages được load từ db sẽ gán sự kiện delete cho nó
		$(this).click(function() {
			removeExtraImage(index);
		});
	});
	
});

function showExtraImageThumbnail(fileInput, index) {
	var file = fileInput.files[0];
	
	fileName = file.name;
	
	imageNameHiddenField = $("#imageName" + index);
	if (imageNameHiddenField.length) {
		imageNameHiddenField.val(fileName);
	}
		
	
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#extraThumbnail" + index).attr("src", e.target.result);
	};
	
	reader.readAsDataURL(file);	
	
	if (index >= extraImagesCount - 1) {//nếu index >= extraImagesCount - 1 thì đang chọn hình của extraImage cuối cùng -->thêm 1 div extraImage mới vào cuối
		addNextExtraImageSection(index + 1);		
	}
}

function addNextExtraImageSection(index) {
	htmlExtraImage = `
		<div class="col border m-3 p-2" id="divExtraImage${index}">
			<div id="extraImageHeader${index}"><label>Extra Image #${index + 1}:</label></div>
			<div class="m-2">
				<img id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" class="img-fluid"
					src="${defaultImageThumbnailSrc}"/>
			</div>
			<div>
				<input type="file" name="extraImage"
					onchange="showExtraImageThumbnail(this, ${index})" 
					accept="image/png, image/jpeg" />
			</div>
		</div>	
	`;
	//khi nhấn vào link này thì gọi function removeExtraImage
	htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x icon-dark float-right"
			href="javascript:removeExtraImage(${index - 1})" 
			title="Remove this image"></a>
	`;
	
	$("#divProductImages").append(htmlExtraImage);//thêm extraImage vào cuối divProductImages
	
	$("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);//thêm link delete vào extraImage kế cuối
	
	extraImagesCount++;//tăng biến extraImagesCount lên 1 để điều kiện index >= extraImagesCount - 1 nhận biết được extraImage cuối cùng 
}

function removeExtraImage(index) {
	$("#divExtraImage" + index).remove();//xóa div theo id
}