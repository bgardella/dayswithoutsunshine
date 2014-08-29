define([
    'intern!object',
    'intern/chai!assert',
    'require',
    'intern/lib/args'
], function (registerSuite, assert, require, args) {

    registerSuite({
        name: 'ui_test',

        'ui_test': function () {

            return this.remote
		            .get('http://'+args.appHost)
		            .setFindTimeout(360000)
		            .findByCssSelector('body.loaded')
		            .findById('main-search')
		                .then(function (element) {
		                	console.info("==>BEGIN ui test********");
                            element.click();
                            element.type('lom');
                            //console.info(theMap);
		                }).end()
                    .findByClassName('suggest-row') 
                        .then(function (element) {
                        	console.info("=>select autocomplete");
                        	element.click();
		                }).end()
		            .sleep(2000)
		            .findByClassName('random-btn')
		            	.then(function (element) {
		            		console.info("=>select random");
                        	element.click();
                        	console.info("==>END ui test********");
		            	}).end()
		            .sleep(2000)
		            .quit();                	
        }
          
    });
});