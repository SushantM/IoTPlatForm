var http = require('http');

jsonObject = JSON.stringify({
    "name" : "Test1_Application",
    "valueType": "Temperature",
    "value": "",
    "timestamp": "1423"
});

var postheaders = {
    'Content-Type' : 'application/json',
    'Content-Length' : Buffer.byteLength(jsonObject, 'utf8')
};

var optionspost = {
    host : 'http:10.42.0.94',
    port : 3002,
    path : '/register',
    method : 'POST',
    headers : postheaders
};

console.info('Options prepared:');
console.info(optionspost);
console.info('Do the POST call');


// do the POST call
var reqPost = http.request(optionspost, function(res) {
    console.log("statusCode: ", res.statusCode);
    // uncomment it for header details
//  console.log("headers: ", res.headers);
 
    res.on('data', function(d) {
        console.info('POST result:\n');
        process.stdout.write(d);
        console.info('\n\nPOST completed');
    });
});
 
// write the json data
reqPost.write(jsonObject);
reqPost.end();
reqPost.on('error', function(e) {
    console.error(e);
});



