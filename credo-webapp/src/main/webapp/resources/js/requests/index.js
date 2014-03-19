$(document).ready(function() {
    $.extend($.tablesorter.themes.bootstrap, {
		// these classes are added to the table. To see other table classes available,
		// look here: http://twitter.github.com/bootstrap/base-css.html#tables
		table      : 'table table-bordered table-condensed table-hover table-responsive',
		caption    : 'caption',
		header     : 'bootstrap-header', // give the header a gradient background
		footerRow  : '',
		footerCells: '',
		icons      : '', // add "icon-white" to make them white; this icon class is added to the <i> in the header
		sortNone   : 'bootstrap-icon-unsorted',
		sortAsc    : 'icon-chevron-up glyphicon glyphicon-chevron-up',     // includes classes for Bootstrap v2 & v3
		sortDesc   : 'icon-chevron-down glyphicon glyphicon-chevron-down', // includes classes for Bootstrap v2 & v3
		active     : '', // applied when column is sorted
		hover      : '', // use custom css here - bootstrap class may not override it
		filterRow  : '', // filter row class
		even       : '', // odd row zebra striping
		odd        : ''  // even row zebra striping
	});
    
	$('#requests').tablesorter({ 
	    theme : "bootstrap",

	    widthFixed: true,

	    headerTemplate : '{content} {icon}', // new in v2.7. Needed to add the bootstrap icon!

	    // widget code contained in the jquery.tablesorter.widgets.js file
	    // use the zebra stripe widget if you plan on hiding any rows (filter widget)
	    widgets : [ "uitheme", "filter" ],

	    widgetOptions : {

	      // reset filters button
	      filter_reset : ".reset"

	      // set the uitheme widget to use the bootstrap theme class names
	      // this is no longer required, if theme is set
	      // ,uitheme : "bootstrap"

	    },		
		
		sortList:[[0,0]] 
	});	
	
});
