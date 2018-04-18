$(function() {
	var Accordion = function(el, multiple) {
		this.el = el || {};
		this.multiple = multiple || false;

		// Variables privadas
		var links = this.el.find('.link');
		// Evento
		links.on('click', {
			el: this.el,
			multiple: this.multiple
		}, this.dropdown)
	}

	Accordion.prototype.dropdown = function(e) {
		var $el = e.data.el;
		$this = $(this),
			$next = $this.next();

		$next.slideToggle();
		$this.parent().toggleClass('open');

		if(!e.data.multiple) {
			$el.find('.submenu').not($next).slideUp().parent().removeClass('open');
		};
	}

	var accordion = new Accordion($('#accordion'), false);

});


function show_list_item_by_id(item_id) {
	iconAreaDisplay();
	var string = '#' + item_id;
	$('.submenu').slideUp().parent().removeClass('open');
	$(string).parent().parent().slideDown().parent().addClass('open');
	$(string).addClass('cell_background');
}
var u = navigator.userAgent;

function resetScale()
{
    var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
    if(isiOS)
    {
       // window.location.href='scale://disable';
    }
    else
    {
    var viewportmeta = document.querySelector('meta[name="viewport"]');
    if (viewportmeta) {
        
        viewportmeta.content = 'viewport-fit=cover;width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=yes';

        setTimeout(function(){ restartScale() }, 100);
    }
}
}

function restartScale()
{
        var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
        if(isiOS)
        {
            window.location.href='scale://enable';
        }
        else
        {
        var viewportmeta = document.querySelector('meta[name="viewport"]');
        if (viewportmeta) {
        viewportmeta.content = 'viewport-fit=cover,;width=device-width; initial-scale=1.0; maximum-scale=3.0; user-scalable=yes';

        }
        }
}

var hidepageStatus = 0;

function zoneAreaDisplay() {
    resetScale();

	var myZoneArea = document.getElementById("zone_list");
	myZoneArea.style.display = "block";
	var myGreyBg = document.getElementById("grey_background");
	myGreyBg.style.display = "block";
	hidepageStatus = 1;
}

function hideArea() {
	if(hidepageStatus == 1) {
		var myZoneArea = document.getElementById("zone_list");
		myZoneArea.style.display = "none";
	} else if(hidepageStatus == 2){
		var myIconArea = document.getElementById("test_list");
		myIconArea.style.display = "none";
		$('.submenu').slideUp().parent().removeClass('open');
		$('.cell_background').removeClass('cell_background');
	}else{
        var myBusArea = document.getElementById("bus_list");
        myBusArea.style.display = "none";
    }
	var myGreyBg = document.getElementById("grey_background");
	myGreyBg.style.display = "none";
	hidepageStatus = 0;

}

function iconAreaDisplay() {
    resetScale();
	var myIconArea = document.getElementById("test_list");
	myIconArea.style.display = "block";
	var myGreyBg = document.getElementById("grey_background");
	myGreyBg.style.display = "block";
	hidepageStatus = 2;
}


                              function busAreaDisplay() {
                              resetScale();
                              var myBusArea = document.getElementById("bus_list");
                              myBusArea.style.display = "block";
                              var myGreyBg = document.getElementById("grey_background");
                              myGreyBg.style.display = "block";
                              hidepageStatus = 3;
                              }
                              

function show_location(){

	$('.location_icon').hide();
	var i,arguments_length=arguments.length;
	for (i=0; i<arguments_length; i++) {
		var string = '#' + arguments[i];
	    $(string).css('display', 'block');
	}
	$('#test_list').css('display', 'none');
    $('#bus_list').css('display', 'none');
	$('#zone_list').css('display', 'none');
	$('#grey_background').css('display', 'none');
    $('.cell_background').removeClass('cell_background');

}

