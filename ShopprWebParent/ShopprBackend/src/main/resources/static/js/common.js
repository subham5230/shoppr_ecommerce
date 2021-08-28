$(document).ready(function(){
    
	$("#logout-link").on("click", function(e){
		e.preventDefault();
		document.logoutForm.submit();
	})
	
});

