var net = require('net');
var fs = require('fs');
var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));
var HOST = config.repo_ip;
var PORT = config.repo_port;
var mongo = require('mongodb');

 
var Server = mongo.Server,
    Db = mongo.Db,
    ObjectID = mongo.ObjectID

var server = new Server('localhost', 27017, {auto_reconnect: true});
db = new Db('iotdb', server);

db.open(function(err, db) {
	if(!err) {
		console.log("Connected to 'iotdb' database");
	}
});


var http = require('http');
var fs = require('fs');
var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));

var mongodbPort=config.mongodb_port;
var registry=config.registry_name;
var repository=config.repository_name;
var dbName=config.db_name;
var MongoClient = require('mongodb').MongoClient;
DatabaseDriver = require('./databaseDriver').DatabaseDriver;
var dbDriver;

MongoClient.connect('mongodb://localhost:'+mongodbPort+'/'+dbName, function (err, database) {
	if (err) {
		throw err;
		console.log("error connecting to the database");
	}
	else {
		dbDriver=new DatabaseDriver(database, dbName, registry, repository, mongodbPort)
       		console.log("successfully connected to the database "+dbDriver.db.s.databaseName)
	}
});

//creates a server to listen for data
net.createServer(function(sock) {
	console.log('Connected to: ' + sock.remoteAddress +': '+ sock.remotePort);
	var addr= sock.remoteAddress, prt= sock.remotePort;
	sock.on('data', function(data) {
		console.log('Data received from ' + sock.remoteAddress + ': ' + data);
		data = data.toString('utf-8').trim();
		if( data==='request_ids' )
			write_sensor_ids(sock);
		else {
			var sp=data.toString().split(",")
			heart_beat(sp)
		}
	});
	sock.on('close', function(data) {
		console.log('Closed!!');
	});
	
}).listen(PORT, HOST);

console.log('Server listening on ' + HOST +':'+ PORT);

function write_sensor_ids(sock) {
	dbDriver.getSensorList(	function(result ) {
		console.log(result)
		var list="";
		for( var id in result ) list+=result[id]+','
		
		console.log("Sending: " + list)	
		sock.write(list);
	});
}

function heart_beat(sp) {
	
	
	dbDriver.isSensorInRegistry( sp[0],function(result ) {
			
			if(result) {
				var d = new Date();
				dbDriver.updateLastUptime( sp[0], d.getTime() );
			}
			else {
				var d = new Date();
				dbDriver.insertToRegistry({ "gatewayid" : "1234",
							"sensorid" : sp[0],
							"lastdowntime" : null,
							"lastupdatetime" : d.getTime()});
			}
		});
}
