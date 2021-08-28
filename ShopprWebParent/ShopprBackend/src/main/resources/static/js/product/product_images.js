var extraImagesCount = 0;

$(document).ready(function(){
	
	$("input[name='extraImage']").each(function(index){
		extraImagesCount++
		
		$(this).change(function(){
			fileSize = this.files[0].size;
			
			if(fileSize > 1048576){
				this.setCustomValidity("You can only upload image of size less than 1MB.");
				this.reportValidity();
			}
			else if(fileSize < 1048576){
				this.setCustomValidity("");
				showExtraImageThumbnail(this, index);
			}
		})
	})
	$("a[name='linkRemoveExtraImage']").each(function(index){
		$(this).click(function(){
			removeExtraImage(index)
		})
	})
})

function showExtraImageThumbnail(fileInput, index){
		var file = fileInput.files[0];
		
		filename = file.name
		console.log(index)
		imageNameHiddenField = $("#imageName" + index)
		if(imageNameHiddenField.length){
			imageNameHiddenField.val(filename)
		}
		
		var reader = new FileReader();
		reader.onload = function(e){
			$("#extraThumbnail" + index).attr("src", e.target.result);
		}
		reader.readAsDataURL(file);
		
		if(index >= extraImagesCount-1){
			addExtraImageSection(index + 1)
		}
}

function addExtraImageSection(index){
	html = `
			<div class="col border m-3 p-2" id="divExtraImage${index}">
    			<div id="extraImageHeader${index}"><label>Extra Image ${index + 1}:</label></div>
	    		<div class="m-2">
	    			<image id="extraThumbnail${index}" class="img-fluid" alt="Extra image ${index+1} preview" 
	    				src="${defaultImageThumbnailUrl}"
	            		style="width:200px; " />
	    		</div>
	    		<div>
	    			<input type="file" name="extraImage"
	    			onchange="showExtraImageThumbnail(this, ${index})"
	    			accept="image/png, image/jpeg, image/jpg"/>
	    		</div>
    		</div>
	
	`
	
	removeImage = `
	
			<a class="btn fas fa-times-circle float-right"
				title="Remove this image"
				href="javascript:removeExtraImage(${index - 1})"></a>
	
	`
	
	$("#divProductImages").append(html)
	$("#extraImageHeader"+ (index-1)).append(removeImage)
	
	
}

function removeExtraImage(index){
	$("#divExtraImage" + index).remove()
}
