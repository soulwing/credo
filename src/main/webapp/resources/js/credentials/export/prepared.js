$(document).ready(function() {

	var href = $("#downloadLink").attr("href");
	var detailAnchor = $("#detail > a");
	detailAnchor.attr("href", href);
	setTimeout(function() {
		$("#downloadFrame").attr("src", href);
	}, 1500);
	
	$("#returnToCredentials").trigger("focus");
	
});