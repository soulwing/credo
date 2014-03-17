$(document).ready(function() {
	$('#details\\:tags').selectize({
	    delimiter: ',',
	    persist: false,
	    create: function(input) {
	        return {
	            value: input,
	            text: input
	        };
	    }
	});
	
	var $owner = $("#details\\:owner");
	var $ownerStatus = $("#details\\:ownerStatus");
	var $inputGroup = $owner.parent().parent();
	var $feedback = $owner.parent().children(".form-control-feedback");
	var $helpWillCreate = $("#help-will-create");
	var $helpInaccessible = $("#help-inaccessible");

	var passwordTimeout = function() { };

	var updateFeedback = function() {
		var $ownerErrors = $("#details\\:ownerErrors");
		if ($ownerErrors.text().length > 0) {
			$inputGroup.removeClass("has-warning has-success");
			$inputGroup.addClass("has-error");
			$feedback.removeClass("glyphicon glyphicon-ok glyphicon-warning-sign");
			$feedback.addClass("glyphicon glyphicon-exclamation-sign");
			$feedback.removeClass("hidden");
			$helpWillCreate.addClass("hidden");
			$helpInaccessible.addClass("hidden");
			$ownerErrors.removeClass("hidden");
			return;
		}		
		var status = $ownerStatus.val();
		if (status == "NONE") {
			$inputGroup.removeClass("has-success has-warning has-error");
			$feedback.removeClass("glyphicon glyphicon-ok glyphicon-warning-sign glyphicon-exclamation-sign");
			$feedback.addClass("hidden");
			$helpWillCreate.addClass("hidden");
			$helpInaccessible.addClass("hidden");
			$ownerErrors.addClass("hidden");
		}
		else if (status == "EXISTS") {
			$inputGroup.removeClass("has-error has-warning");
			$inputGroup.addClass("has-success");
			$feedback.removeClass("glyphicon glyphicon-warning-sign glyphicon-exclamation-sign");
			$feedback.addClass("glyphicon glyphicon-ok");
			$feedback.removeClass("hidden");
			$helpWillCreate.addClass("hidden");
			$helpInaccessible.addClass("hidden");
			$ownerErrors.addClass("hidden");
		}
		else if (status == "WILL_CREATE") {
			$inputGroup.removeClass("has-error has-success");
			$inputGroup.addClass("has-warning");
			$feedback.removeClass("glyphicon glyphicon-ok glyphicon-exclamation-sign");
			$feedback.addClass("glyphicon glyphicon-warning-sign");
			$feedback.removeClass("hidden");
			$helpWillCreate.removeClass("hidden");
			$helpInaccessible.addClass("hidden");
			$ownerErrors.addClass("hidden");
		}
		else if (status == "INACCESSIBLE") {
			$inputGroup.removeClass("has-warning has-success");
			$inputGroup.addClass("has-error");
			$feedback.removeClass("glyphicon glyphicon-ok glyphicon-warning-sign");
			$feedback.addClass("glyphicon glyphicon-exclamation-sign");
			$feedback.removeClass("hidden");
			$helpWillCreate.addClass("hidden");
			$helpInaccessible.removeClass("hidden");
			$ownerErrors.addClass("hidden");
		}
	};

	$owner.on("input", function(event) {
		var source = this;
		var ajaxRequest = function() { 
			jsf.ajax.request(source, event, {
				execute: $owner.attr("id"),
				render: $ownerStatus.attr("id") + " details:ownerErrors",
				onevent: function(data) {
					if (data.status == "success") {
						updateFeedback();
					}			
				}
			});
		};
		
		window.clearTimeout(passwordTimeout);
		passwordTimeout = window.setTimeout(ajaxRequest, 250);
		return false;
	});
	
	$owner.on("focus", function(event) { 
		updateFeedback();
		return false;
	});
	
	$owner.on("blur", function(event) { 
		var status = $ownerStatus.val();
		if (status == "NONE" || status == "EXISTS") {
			$inputGroup.removeClass("has-success");
			$feedback.removeClass("glyphicon glyphicon-ok");
			$feedback.addClass("hidden");
		}
		return false;
	});

	if ($ownerStatus.val() != "EXISTS") {
		updateFeedback();
	}
});
