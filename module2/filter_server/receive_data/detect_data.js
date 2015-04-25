var mongo = require('mongodb');
var fs=require('fs');
var http = require('http');
var Server = mongo.Server,
    Db = mongo.Db,
    ObjectID = mongo.ObjectID,
    dataType1="health",
    dataType2="traffic";


var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));
var appEngineIp=config.app_engine_ip,
	appEnginePort=config.app_engine_port;

var server = new Server(config.filter_server_ip, config.filter_server_port, {auto_reconnect: true});
db = new Db('datadb', server);

db.open(function(err, db) {
	if(!err) {
		console.log("Connected to 'datadb' database");
	}
});

setInterval(function() {
	detect1();
	detect2();
}, 2000);

var detect1 = function() {
	
	var app=0;
	var D=7000;
	var dat=0;
	db.collection('apps', function(err, collection) {
		var id_list=[], appName=[];

		collection.find().toArray(function(err, items) {	
			items.forEach(function(item) {
				var jsonObj = JSON.parse(JSON.stringify( item ));
				appName.push( jsonObj[ "name" ].trim() );
				id_list.push( jsonObj[ "idlist" ].trim() );
				app += 1
			});
			
			var dataType=[], value =[], dataTime=[], id=[], sensor_id=[];
			var col1=db.collection(dataType1);
			col1.find().toArray(function(err, items1) {
				dat=0;	
				items1.forEach(function(item) {
					var jsonObj = JSON.parse(JSON.stringify( item ));
					sensor_id.push( jsonObj[ "id" ].trim() )
					dataType.push( jsonObj[ "type" ].trim() );
					value.push(parseInt(jsonObj[ "value" ]));	
					dataTime.push(parseInt(jsonObj[ "timestamp" ]));
					id.push(jsonObj[ "_id" ]);
					dat += 1;
				});
				console.log( "Type1: Apps : " + app  + " DataPoints: " + dat  );
				for( var i=0; i<app; i++ ) {

					for( var j=0; j<dat; j++ ) {
						var sp=id_list[i].toString().split(",");
						
						for( var key in sp ) {
							//console.log( sensor_id[j], sp[key] )
							if( sp[key]==sensor_id[j] ) {
								jsonObject = JSON.stringify({
									    "name" : appName[i],
									    "valueType": dataType[j],
									    "value": value[j],
									    "timestamp": dataTime[j],
								});
								console.log( "Found " + jsonObject )
								post_data(jsonObject, dataType1);
								col1.remove({'_id':ObjectID(id[j])}, {safe:true}, function(err, result) {
									    if (err) {
										console.log(" error = An error has occurred ");
									    } else {
										console.log('' + result + ' data(s) deleted');
										
									    }
								});
							}
						}
					}
				}
			});
	       	});
	});
};

var detect2 = function() {
	
	var app=0;
	var D=7000;
	var dat=0;
	db.collection('apps', function(err, collection) {
		var id_list=[], appName=[];

		collection.find().toArray(function(err, items) {	
			items.forEach(function(item) {
				var jsonObj = JSON.parse(JSON.stringify( item ));
				appName.push( jsonObj[ "name" ].trim() );
				id_list.push( jsonObj[ "idlist" ].trim() );
				app += 1
			});
			
			var dataType=[], value =[], dataTime=[], id=[], sensor_id=[], sX=[], sY=[], dX = [], dY=[] ;
			col1=db.collection(dataType2);
			col1.find().toArray(function(err, items1) {	
				dat=0;
				items1.forEach(function(item) {
					var jsonObj = JSON.parse(JSON.stringify( item ));
					sensor_id.push( jsonObj[ "id" ].trim() )
					dataType.push( jsonObj[ "type" ].trim() );
					value.push(parseInt(jsonObj[ "value" ]));	
					dataTime.push(jsonObj[ "timestamp" ]);
					sX.push(parseInt(jsonObj[ "lat1" ]));
					sY.push(parseInt(jsonObj[ "long1" ]));
					dX.push(parseInt(jsonObj[ "lat2" ]));
					dY.push(parseInt(jsonObj[ "long2" ]));
					id.push(jsonObj[ "_id" ]);
					dat += 1;
				});
				console.log( "Type2: Apps : " + app  + " DataPoints: " + dat  );
				for( var i=0; i<app; i++ ) {

					for( var j=0; j<dat; j++ ) {
						var sp=id_list[i].toString().split(",");
						
						for( var key in sp ) {
							//console.log( sensor_id[j], sp[key] )
							if( sp[key]==sensor_id[j] ) {
								var jsonObject = JSON.stringify({
									    "name" : appName[i],
									    "valueType": dataType[j],
									    "value": value[j],
									    "timestamp": dataTime[j],
									    "startX" : sX[j],
									    "startY" : sY[j],
									    "endX" : dX[j],
									    "endX" : dY[j]
								});
								
								post_data1(jsonObject, dataType1);
								col1.remove({'_id':ObjectID(id[j])}, {safe:true}, function(err, result) {
									    if (err) {
										console.log(" error = An error has occurred ");
									    } else {
										console.log('' + result + ' data(s) deleted');
										
									    }
								});
							}
						}
					}
				}
			});
	       	});
	});
};

function inRange(slat, slong, mylat, mylong,  range)
{
	var theta = slong - mylong;
	var dist = Math.sin(deg2rad(slat)) * Math.sin(deg2rad(mylat)) + Math.cos(deg2rad(slat)) * Math.cos(deg2rad(mylat)) * Math.cos(deg2rad(theta));
	dist = Math.acos(dist);
	dist = rad2deg(dist);
	dist = dist * 60 * 1.1515;
	     
	/*assuming unit is N */
        dist = dist * 0.8684;
	//console.log(dist)
	if(dist<=range)
		return true;
	return false;
}

function deg2rad(deg)
	{
	    return (deg * Math.PI / 180.0);
	}
	 
function rad2deg(rad)
	{
	    return (rad * 180 / Math.PI);
	}

function post_data( jsonObj, dataType ) {
	console.log( "Found " + jsonObj )
	var postheaders = {
		'Content-Type' : 'application/json',
		'Content-Length' : Buffer.byteLength(jsonObject, 'utf8')
	};

	var optionspost = {
	    host : '10.42.0.24',
	    port : 3005,
	    path : '/post' + dataType + 'Data',
	    method : 'POST',
	    headers : postheaders
	};
	var reqPost = http.request(optionspost, function(res) {
		console.log("statusCode: ", res.statusCode);
		res.on('data', function(d) {
			console.info('POST result:\n');
			process.stdout.write(d);
			console.info('\n\nPOST completed');
		});
	});
	reqPost.write(jsonObject);
	reqPost.end();
	reqPost.on('error', function(e) {
		    console.error(e);
	});
}
function post_data1( jsonObj, dataType ) {
	console.log( "Found " + jsonObj )
	var postheaders = {
		'Content-Type' : 'application/json',
		'Content-Length' : Buffer.byteLength(jsonObject, 'utf8')
	};

	var optionspost = {
	    host : appEngineIp,
	    port : appEnginePort,
	    path : '/postAppData',
	    method : 'POST',
	    headers : postheaders
	};
	var reqPost = http.request(optionspost, function(res) {
		console.log("statusCode: ", res.statusCode);
		res.on('data', function(d) {
			console.info('POST result:\n');
			process.stdout.write(d);
			console.info('\n\nPOST completed');
		});
	});
	reqPost.write(jsonObject);
	reqPost.end();
	reqPost.on('error', function(e) {
		    console.error(e);
	});
}
