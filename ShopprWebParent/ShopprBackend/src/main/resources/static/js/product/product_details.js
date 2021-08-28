$(document).ready(function(){
	
	$("a[name='linkRemoveDetail']").each(function(index){
		$(this).click(function(){
			removeDetailByIndex(index)
		})
	})
	
})


function addNextDetailSection(){
	
	allDivDetails = $("[id^='divDetail']")
	divDetailsCount = allDivDetails.length
	
	htmlDetailSection = `
	
		<div class="form-inline" id="divDetail${divDetailsCount}">
			<input type="hidden" name="detailIDs" th:value="0" />
            <label class="m-3">Name:</label>
            <input type="text" class="form-control w-25" name="detailNames" maxlength="255"/>

            <label class="m-3">Value:</label>
            <input type="text" class="form-control w-25" name="detailValues" maxlength="255"/>
        </div>
	
	`
	
	previousDivDetailSection = allDivDetails.last()
	previousDivDetailId = previousDivDetailSection.attr("id")
	
	removeDetail = `
	
			<a class="btn fas fa-times-circle float-right"
				title="Remove this image"
				href="javascript:removeDetailById('${previousDivDetailId}')"></a>
	
	`
	
	$("#divProductDetails").append(htmlDetailSection)
	
	previousDivDetailSection.append(removeDetail)
	
	$("input[name='detailNames']").last().focus()
}

function removeDetailById(id){
	$("#" + id).remove()
}

function removeDetailByIndex(index){
	$("#divDetail" + index).remove()
}

