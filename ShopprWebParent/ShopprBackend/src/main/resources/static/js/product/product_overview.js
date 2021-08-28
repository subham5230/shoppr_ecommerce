dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function(){

	dropdownBrands.change(function(){
		dropdownCategories.empty()
		getCategories()
	})
	
	getCategories()
	
})

function getNewCategories(){
	categoryIdFileld = $("#categoryId")
	editMode = false
	
	if(categoryIdField.length){
		editMode = true
	}
	
	if(!editMode){
		getCategories()
	}
}

function getCategories(){
	brandId = dropdownBrands.val()
	url = brandModuleUrl + "/" + brandId + "/categories"
	
	$.get(url, function(responseJson){
		$.each(responseJson, function(index, category){
			$("<option>").val(category.id).text(category.name).appendTo(dropdownCategories)
		})
	})
}