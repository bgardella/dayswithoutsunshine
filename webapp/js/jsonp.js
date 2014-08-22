
callAPI = function(uri,callback){

    var script = document.createElement('script'),
        ext = '.json';

	var url = uri + '.json?callback='+callback;

    script.type = 'text/javascript';
    script.id = 'stash-items-script';
    script.src = url;
    document.body.appendChild(script);
}