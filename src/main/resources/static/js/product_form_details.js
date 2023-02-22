$(document).ready(function() {		
	$("a[name='linkRemoveDetail']").each(function(index) {
		$(this).click(function() {
			removeDetailSectionByIndex(index);
		});
	});
	
});

function addNextDetailSection() {
	allDivDetails = $("[id^='divDetail']");
	divDetailsCount = allDivDetails.length;//lấy ra length div cuối
	
	htmlDetailSection = `
		<div class="form-inline" id="divDetail${divDetailsCount}">
			<input type="hidden" name="detailIDs" value="0" />
			<label class="m-3">Name:</label>
			<input type="text" class="form-control w-25" name="detailNames" maxlength="255" />
			<label class="m-3">Value:</label>
			<input type="text" class="form-control w-25" name="detailValues" maxlength="255" />
		</div>	
	`;
	
	$("#divProductDetails").append(htmlDetailSection);//thêm vào div lớn

	previousDivDetailSection = allDivDetails.last();//lấy ra div cuối
	previousDivDetailID = previousDivDetailSection.attr("id");//lấy ra id div cuối
	 	
	htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x icon-dark"
			href="javascript:removeDetailSectionById('${previousDivDetailID}')"
			title="Remove this detail"></a>
	`;
	
	previousDivDetailSection.append(htmlLinkRemove);//thêm dấu x để delete
	
	$("input[name='detailNames']").last().focus();//focus vào fix cuối
}

function removeDetailSectionById(id) {
	$("#" + id).remove();
}

function removeDetailSectionByIndex(index) {
	$("#divDetail" + index).remove();	
}