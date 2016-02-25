$(document).ready(function() { 
	var $textarea = $("#certificationRequest");
	$textarea.on("focus", function(event) { 
		$(this).select();
		$textarea.on("mouseup", function(event) { 
			$textarea.off("mouseup");
			return false;
		});
		return false;
	});
	
	$("#returnToRequests").trigger("focus");
	
});