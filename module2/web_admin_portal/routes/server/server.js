// call the packages we need
var fs = require('fs');
var express    = require('express');
var app        = express();
//var bodyParser = require('body-parser');
var MongoClient = require('mongodb').MongoClient;
DatabaseDriver = require('./databaseDriver').DatabaseDriver;

var config = JSON.parse(fs.readFileSync('./config.json', 'utf8'));
var mongodbPort=config.mongodb_port;
var registry=config.registry_name;
var repository=config.repository_name;
var dbName=config.db_name;
var dbDriver;

MongoClient.connect('mongodb://localhost:'+mongodbPort+'/'+dbName, function (err, database) {
	if (err) {
		throw err;
		console.log("error connecting to the database");
	}
	else {
		dbDriver=new DatabaseDriver(database, dbName, registry, repository, mongodbPort)
       		console.log("successfully connected to the database "+dbDriver.db.s.databaseName);
	}
});

//app.use(bodyParser.urlencoded({ extended: true }));
//app.use(bodyParser.json({ type: 'application/json' }));


// ROUTES FOR OUR API
// =============================================================================
var router = express.Router();              // get an instance of the express Router

// test route to make sure everything is working (accessed at GET http://localhost:8080/api)
router.get('/', function(req, res) {
   	res.send("home page");   
});

// more routes for our API will happen here

router.get('/adddevice', function(req, res) {
	console.log(req.param('gid'));
	console.log(req.param);
	dbDriver.addNewSensor([{"gatewayid":req.param('gid'),"sensorid":req.param('sid'),"sensortype":req.param('stype'),"devicehandler":req.param('handle'),"protocol":req.param('wformat'),"location":{"latitude":req.param('lat'), "longitude":req.param('long')}}], function(result){res.send(result)});  
});


router.get('/sensorsinrange', function(req, res) {
dbDriver.getSensorByLocation({"latitude":req.param('lat'), "longitude":req.param('long')}, req.param('range'),req.param('stype'),function(result){res.send(result)}); 
});

router.get('/sensorsbytype', function(req, res) {
dbDriver.getSensorByType(req.param('type'),function(result){res.send(result)}); 
});


// REGISTER OUR ROUTES -------------------------------
app.use('/', router);

// START THE SERVER
// =============================================================================
app.listen(config.server_port);
console.log('Server running on port ' + config.server_port);
