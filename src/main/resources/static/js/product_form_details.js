$(document).ready(function() {		
	$("a[name='linkRemoveDetail']").each(function(index) {//tất cả details được load từ db sẽ gán sự kiện delete cho nó
		$(this).click(function() {
			removeDetailSectionByIndex(index);
		});
	});
	
});

function addNextDetailSection() {
	allDivDetails = $("[id^='divDetail']");//lấy ra tất cả các thẻ có id bắt đầu là divDetail -->đã có được list tất cả các div details
	divDetailsCount = allDivDetails.length;//trả về độ dài của list -->divDetailsCount chính là index cần thêm vào cuối
	
	//tất cả detail được thêm mới sẽ có detailIDs = 0
	htmlDetailSection = `
		<div class="form-inline" id="divDetail${divDetailsCount}">
			<input type="hidden" name="detailIDs" value="0" />
			<label class="m-3">Name:</label>
			<input type="text" class="form-control w-25" name="detailNames" maxlength="255" />
			<label class="m-3">Value:</label>
			<input type="text" class="form-control w-25" name="detailValues" maxlength="255" />
		</div>	
	`;
	
	$("#divProductDetails").append(htmlDetailSection);//thêm detail vào cuối divProductDetails

	previousDivDetailSection = allDivDetails.last();//lấy ra div cuối tại thời điểm trước khi thêm
	previousDivDetailID = previousDivDetailSection.attr("id");//lấy ra id
	 	
	htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x icon-dark"
			href="javascript:removeDetailSectionById('${previousDivDetailID}')"
			title="Remove this detail"></a>
	`;
	
	previousDivDetailSection.append(htmlLinkRemove);//thêm link delete
	
	$("input[name='detailNames']").last().focus();//focus vào detail cuối(sau khi đã thêm detail)
}

function removeDetailSectionById(id) {
	$("#" + id).remove();
}

function removeDetailSectionByIndex(index) {
	$("#divDetail" + index).remove();	
}