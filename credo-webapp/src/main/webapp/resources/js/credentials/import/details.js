$(document).ready(function() {
	
	var $owner = $("#details\\:owner");
	var $ownerStatus = $("#details\\:ownerStatus");
	var $inputGroup = $owner.parent().parent();
	var $feedback = $owner.parent().children(".form-control-feedback");
	var $helpWillCreate = $("#help-will-create");
	var $helpInaccessible = $("#help-inaccessible");
	var $ownerErrors = $("#details\\:ownerErrors");

	var passwordTimeout = function() { };

	var groups = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: {
			url: $owner.parent().data("autocomplete-url"),
			// the data is returned as an array of string group names
			// convert to an array of objects with a name property
			filter: function(list) {
				return $.map(list, function(group) { return { name: group }; });
			}
		}
	});
	
	groups.initialize();
	
	$owner.typeahead(
		{
			minLength: 1,
			highlight: true,
		},
		{
			name: 'groups',
			displayKey: 'name',
			source: groups.ttAdapter()
		}
	);
	
	var $tags = $("#details\\:tags");
	var tagsSelector = $tags.selectize({
	    delimiter: ',',
	    persist: true,
	    openOnFocus: false,
	    sortField: [{field: 'text', direction: 'asc'}],
	    maxOptions: 5,
	    create: function(input) { return { value: input, text: input }; },
	    onChange: function(value) { tagsSelector[0].selectize.close(); }
	});

	tagsSelector[0].selectize.load(function(callback) { 
		$.ajax({
			url: $tags.parent().data("autocomplete-url"),
			type: "GET",
			dataType: "json",
			success: function(tags) {
				// the data is returned as an array of string tags
				// convert to an array of name/value pair objects
				callback($.map(tags, function(tag) { 
					return { value: tag, text: tag };
				}));
			},
			error: function() {
				callback();
			}
		});
	});
	
	var hideFeedback = function() {
		var $ownerErrors = $("#details\\:ownerErrors");
		$inputGroup.removeClass("has-error has-warning");
		$feedback.removeClass("glyphicon glyphicon-warning-sign glyphicon-exclamation-sign");
		$feedback.addClass("hidden");
		$helpWillCreate.addClass("hidden");
		$helpInaccessible.addClass("hidden");
		$ownerErrors.addClass("hidden");
	};

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
		if (status == "EXISTS") {
			hideFeedback();
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

	$owner.on("focus", function(event) { 
		hideFeedback();
		return false;
	});
	
	$owner.on("blur", function(event) { 
		var source = this;
		var ajaxRequest = function() { 
			jsf.ajax.request(source, event, {
				execute: $owner.attr("id"),
				render: $ownerStatus.attr("id") + " details:ownerErrors",
				onevent: function(data) {
					if (data.status == "success") {
						$ownerErrors = $("#details\\:ownerErrors");
						updateFeedback();
					}			
				}
			});
		};
		ajaxRequest();
		return false;
	});

	if ($ownerStatus.val() != "EXISTS") {
		updateFeedback();
	}
});
