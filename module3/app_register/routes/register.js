var mongo = require('mongodb');
var http = require('http');

var fs = require('fs');
var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));

var Server = mongo.Server,
    Db = mongo.Db,
    ObjectID = mongo.ObjectID;
 
var server = new Server(config.app_engine_ip, 27017, {auto_reconnect: true});
db = new Db('appsdb', server);
 
db.open(function(err, db) {
    if(!err) {
        console.log("Connected to 'appsdb' database");
        db.collection('apps', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The 'apps' collection doesn't exist. Creating it with sample data...");
                //populateDB();
            }
        });
    }
});
 
exports.findById = function(req, res) {
    //var id = req.params.id;
    var id = ObjectID(req.params.id);
    console.log('Retrieving app: ' + id);
    db.collection('apps', function(err, collection) {
        collection.findOne({'_id':id}, function(err, item) {
            res.send(item);
        });
    });
};
 
exports.findAll = function(req, res) {
    db.collection('apps', function(err, collection) {
        collection.find().toArray(function(err, items) {
            res.send(items);
        });
    });
};
var line=""
function append(arr,client, args,app) {
	 for( var key in arr ) { line+=arr[key]+','}
	  
}
exports.addApp = function(req, res) {
	var app = req.body;
    	console.log('Adding app: ' + JSON.stringify(app));
    	var Client = require('node-rest-client').Client;
    	client = new Client();
	args ={
		headers:{"test-header":"client-api"} 
	 };
	
	 //console.log("http://10.42.0.37:8081/sensorsinrange?lat="+app["name"]+"&long="+app["name"]+"&range=200&stype=health")
	 client.get("http://"+config.repo_ip+":"+config.repo_port+"/sensorsinrange?lat="+app["name"]+"&long="+app["name"]+"&range=200&stype=health",
	 		args,
	 		function(response){
	 	console.log(response);
	 	var range=getDist(app['startX'],app['startY'],app['endX'],app['endY'])+10000;
          	client.get("http://"+config.repo_ip+":"+config.repo_port+"/sensorsinrange?lat="+app["startX"]+"&long="+app["startY"]+"&range="+range+"&stype=traffic",
          			args, 
          			function(response1){
          		console.log(response1);
			
			var l=""
			for( var k in response )
				l+=response[k]+','
			for( var k in response1 )
				l+=response1[k]+','
			console.log(l);
			
			
			var postheaders = {
			    'Content-Type' : 'application/json'			    
			     };
			var optionspost = {
			    host : '10.42.0.94',
			    port : 3002,
			    path : '/register',
			    method : 'POST',
			    headers : postheaders
			};

			console.info('Options prepared:');
			console.info(optionspost);
			console.info('Do the POST call');



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
			var obj={
				name : app['name'],
				idlist : l
			};
			reqPost.write(JSON.stringify(obj));
			reqPost.end();
			reqPost.on('error', function(e) {
			    console.error(e);
			});    
			db.collection('apps', function(err, collection) {
				collection.insert(app, {safe:true}, function(err, result) {
				    if (err) {
					res.send({'error':'An error has occurred'});
				    } else {
					console.log('Success: ' + JSON.stringify(result[0]));
					
					res.send(result[0]);
				    }
				});
			    });
         	});
         	
         });


}
exports.updateApp = function(req, res) {
    	//var id = req.params.id;
	    var id = ObjectID(req.params.id);
    var app = req.body;
    console.log('Updating app: ' + id);
    console.log(JSON.stringify(app));
    db.collection('apps', function(err, collection) {
        collection.update({'name':id}, app, {safe:true}, function(err, result) {
            if (err) {
                console.log('Error updating app: ' + err);
                res.send({'error':'An error has occurred'});
            } else {
                console.log('' + result + ' document(s) updated');
                res.send(app);
            }
        });
    });
    
    
}
 
exports.deleteApp = function(req, res) {
    var id = req.params.id;
    //var id = ObjectID(req.params.id);
    console.log('Deleting app: ' + id);
    db.collection('apps', function(err, collection) {
        collection.remove({'name':id}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' document(s) deleted');
                res.send(req.body);
            }
        });
    });
    
    var postheaders = {
			    'Content-Type' : 'application/json'			    
			     };
			var optionspost = {
			    host : '10.42.0.94',
			    port : 3002,
			    path : '/deleteapp',
			    method : 'POST',
			    headers : postheaders
			};

			console.info('Options prepared:');
			console.info(optionspost);
			console.info('Do the POST call');



			var reqPost = http.request(optionspost, function(res) {
			    console.log("statusCode: ", res.statusCode);
		    	// uncomment it for header details
			//  console.log("headers: ", res.headers);

			});
			 
			// write the json data
			var obj={
				name : id,
			};
			reqPost.write(JSON.stringify(obj));
			reqPost.end();
			reqPost.on('error', function(e) {
			    console.error(e);
			});
}
 
/*--------------------------------------------------------------------------------------------------------------------*/
// Populate database with sample data -- Only used once: the first time the application is started.
// You'd typically not find this code in a real-life app, since the database would already exist.
/*var populateDB = function() {
 
    var apps = [
    {
        name: "Test20_Application",
        valueType: "Temperature",
        description: "HeatSense"
    }];
 
    db.collection('apps', function(err, collection) {
        collection.insert(apps, {safe:true}, function(err, result) {});
    });
 
};
*/



function getDist(slat, slong, mylat, mylong)
{

	var theta = slong - mylong;
	var dist = Math.sin(deg2rad(slat)) * Math.sin(deg2rad(mylat)) + Math.cos(deg2rad(slat)) * Math.cos(deg2rad(mylat)) * Math.cos(deg2rad(theta));
	dist = Math.acos(dist);
	dist = rad2deg(dist);
	dist = dist * 60 * 1.1515;
	     
	/*assuming unit is in KM */
        dist = dist * 1.609344;

	return dist;
}

function deg2rad(deg)
	{
	    return (deg * Math.PI / 180.0);
	}
	 
function rad2deg(rad)
	{
	    return (rad * 180 / Math.PI);
	}
