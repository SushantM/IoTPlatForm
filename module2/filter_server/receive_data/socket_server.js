var net = require('net');
var fs=require('fs');

var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));
var HOST = config.socket_server_ip;
var PORT = config.socket_server_port;
var mongo = require('mongodb');
 
var Server = mongo.Server,
    Db = mongo.Db,
    ObjectID = mongo.ObjectID

var server = new Server(config.filter_server_ip, config.filter_server_port, {auto_reconnect: true});

db = new Db('datadb', server);

db.open(function(err, db) {
	if(!err) {
		console.log("Connected to 'datadb' database");
	}
});

//creates a server to listen for data
net.createServer(function(sock) {
	console.log('Connected to: ' + sock.remoteAddress +': '+ sock.remotePort);
	var addr= sock.remoteAddress, prt= sock.remotePort;
	sock.on('data', function(data) {
		console.log('Data received from ' + sock.remoteAddress + ': ' + data);
		var sp=data.toString().split(",")
		insert(sp,sp[1].toLowerCase());
	});
	sock.on('close', function(data) {
		console.log('Closed!!');
	});    
}).listen(PORT, HOST);

console.log('Server listening on ' + HOST +':'+ PORT);


function insert( sp, dataType ) {
	var data1=[]
	if( dataType==="traffic" ) {
		data1 = [{
			id: sp[0].trim(),
			type: sp[1].trim(),
			value: sp[2].trim(),		
			timestamp: sp[3].trim(),
			abs_lat: sp[4].trim(),
			abs_long: sp[5].trim(),		
			lat1: sp[6].trim(),
			long1: sp[7].trim(),
			lat2: sp[8].trim(),
			long2: sp[9].trim()
		}];
	}
	else {
		dataType="health";
		data1 = [{
			id: sp[0].trim(),
			type: sp[1].trim(),
			value: sp[2].trim(),		
			timestamp: sp[3].trim(),
			lat: sp[4].trim(),
			long: sp[5].trim()
		}];
	}
	db.collection(dataType, {strict:true}, function(err, collection2) {
		if (err) {
			console.log("The "+ dataType +" collection doesn't exist. Creating it ...");
			db.collection(dataType, function(err, collection1) {
        		collection1.insert(data1, {safe:true}, function(err, result) {});
				if (err) {
					console.log('error An error has occurred');
			    	} else {
					console.log("Collection Created and Successfully inserted: " + JSON.stringify(data1) + '\n');
				}
		        });
		}
		else {
			collection2.insert(data1, {safe:true}, function(err, result) {
				if (err) {
					console.log('error An error has occurred');
				} else {
					console.log('Successfully inserted: ' + JSON.stringify(data1) + '\n');
			    	}
        		});
		}
	});
};
