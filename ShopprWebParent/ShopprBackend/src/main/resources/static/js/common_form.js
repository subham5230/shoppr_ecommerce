function showImageThumbnail(fileInput){
		var file = fileInput.files[0];
		var reader = new FileReader();
		reader.onload = function(e){
			$("#thumbnail").attr("src", e.target.result);
		}
		reader.readAsDataURL(file);
}
	
function errorAlert(message){
	$('#alert_placeholder').html(
	    `<div class = "alert alert-danger text-center" role="alert">
	       ${message}
	        <button type="button" class="btn btn-sm close text-right" data-dismiss="alert" aria-label="Close">
	            <span aria-hidden="true">&times;</span>
	        </button>
	    </div>
	    `
	) 
}

function handleEmailUnique(form){

    userEmail = $("#email").val();
    csrfValue = $("input[name='_csrf']").val();
    userId = $("#id").val();
    params = {id: userId, email: userEmail, _csrf: csrfValue};

    $.post(checkMailUrl, params, function(response){
        if(response == "OK"){
            form.submit();
        }
        else if(response == "Duplicated"){
            errorAlert("This email is already registered.");
        }
        else{
			errorAlert("Server responded abnormally.");
		}
    }).fail(function(){
		
		errorAlert("Couldn't connect to the server");
});
    
    return false;
}

function checkPasswordMatch(confirmPassword){
	if(confirmPassword.value != $("#password").val()){
		confirmPassword.setCustomValidity("Passwords do not match!")
	}
	else{
		confirmPassword.setCustomValidity("")
	}
}

function handleCategorySubmit(form){

	categoryName = $("#name").val();
    csrfValue = $("input[name='_csrf']").val();
    categoryId = $("#id").val();
    params = {id: categoryId, name: categoryName, _csrf: csrfValue};

    $.post(checkCategoryUrl, params, function(response){
	        if(response == "OK"){
	            form.submit();
	        }
	        else if(response == "Duplicated"){
	            errorAlert("This category already exists.");
	        }
	        else{
				errorAlert("Server responded abnormally.");
			}
	    }).fail(function(){
			
			errorAlert("Couldn't connect to the server");
	});
    
    return false;
}

function handleBrandSubmit(form){

	brandName = $("#name").val();
    csrfValue = $("input[name='_csrf']").val();
    brandId = $("#id").val();
    params = {id: brandId, name: brandName, _csrf: csrfValue};

    $.post(checkBrandUrl, params, function(response){
	        if(response == "OK"){
	            form.submit();
	        }
	        else if(response == "Duplicated"){
	            errorAlert("This brand already exists.");
	        }
	        else{
				errorAlert("Server responded abnormally.");
			}
	    }).fail(function(){
			
			errorAlert("Couldn't connect to the server");
	});
    
    return false;
}

function handleProductSubmit(form){

	productName = $("#name").val();
    csrfValue = $("input[name='_csrf']").val();
    productId = $("#id").val();
    params = {id: productId, name: productName, _csrf: csrfValue};

    $.post(checkProductUrl, params, function(response){
	        if(response == "OK"){
	            form.submit();
	        }
	        else if(response == "Duplicated"){
	            errorAlert("The product name already exists.");
	        }
	        else{
				errorAlert("Server responded abnormally.");
			}
	    }).fail(function(){
			
			errorAlert("Couldn't connect to the server");
	});
    
    return false;
}
	
$(document).ready(function(){
	$("#fileImage").change(function(){
		fileSize = this.files[0].size;
		
		if(fileSize > 1048576){
			this.setCustomValidity("You can only upload image of size less than 1MB.");
			this.reportValidity();
		}
		else if(fileSize < 1048576){
			this.setCustomValidity("");
			showImageThumbnail(this);
		}
	})
	
});