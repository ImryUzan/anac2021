$(document).ready(function() {
		
	window.animateHold = function(jElement, cssTo, cssFrom, holdtime, animtime) {
		jElement.animate(cssTo, animtime, 'swing', function() {
			setTimeout(function() {
				jElement.animate(cssFrom, animtime);
			}, holdtime);
		});
	};
	
	window.identifyBrowser = function() {
        var regexps = {
                'Chrome': [ /Chrome\/(\S+)/ ],
                'Firefox': [ /Firefox\/(\S+)/ ],
                'Internet Explorer': [
                  	/MSIE (\S+);/,                  /*IE 10 and older */
                  	/Trident.*rv[ :]?(\S+)\)/       /*IE 11 */
              	],
                'Opera': [
                    /Opera\/.*?Version\/(\S+)/,     /* Opera 10 */
                    /Opera\/(\S+)/                  /* Opera 9 and older */
                ],
                'Safari': [ /Version\/(\S+).*?Safari\// ]
            },
            re, m, version;
     
        var elements = 1;
        var userAgent = navigator.userAgent;
        
        for (var browser in regexps)
            while (re = regexps[browser].shift())
                if (m = userAgent.match(re)) {
                    version = (m[1].match(new RegExp('[^.]+(?:\.[^.]+){0,' + --elements + '}')))[0];
                    return browser + ' ' + version;
                }
     
        return "UNKNOWN";
    };
	
	$('.currentBrowser').text(identifyBrowser());
	
	$('#username').on('input', function() {
		var j = $(this);
		j.val(j.val().replace(/ /g, ''));
	});
	
	
	$('body').keydown(function(e) {
		if(e.keyCode == 123)
			e.preventDefault();
	});
});

