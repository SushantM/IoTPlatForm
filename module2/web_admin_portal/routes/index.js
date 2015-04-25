var express = require('express');
var router = express.Router();
var CT = require('country-list');
var AM = require('account-manager');
var EM = require('email-dispatcher');
var RM = require('rest_manager')

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

console.log(dbName,registry, repository, mongodbPort)
MongoClient.connect('mongodb://localhost:'+mongodbPort+'/'+dbName, function (err, database) {
	if (err) {
		throw err;
		console.log("error connecting to the database");
	}
	else {
		console.log(dbName,registry, repository, mongodbPort)
		dbDriver=new DatabaseDriver(database, dbName, registry, repository, mongodbPort)
       		//console.log("successfully connected to the database "+dbDriver.db.s.databaseName);
	}
});

/* GET home page. */
router.get('/', function(req, res, next) {
  //res.render('login', { title: 'Web Admin Portal Login' });
  // check if the user's credentials are saved in a cookie //
	if (req.cookies.user == undefined || req.cookies.pass == undefined){
		res.render('login', { title: 'Hello - Please Login To Your Account' });
	}	else{
// attempt automatic login //
		AM.autoLogin(req.cookies.user, req.cookies.pass, function(o){
			if (o != null){
			    req.session.user = o;
				res.redirect('/home');
			}	else{
				res.render('login', { title: 'Hello - Please Login To Your Account' });
			}
		});
	}
});

/* POST to Login a User */
router.post('/', function(req, res){
   if (req.param('email') != null){
      AM.getEmail(req.param('email'), function(o){
         if (o){
	    res.send('ok', 200);
	    EM.send(o, function(e, m){ console.log('error : '+e, 'msg : '+m)});	
	 } else{
	    res.send('email-not-found', 400);
	 }
      });
   } else{
  // attempt manual login //
   AM.manualLogin(req.param('user'), req.param('pass'), function(e, o){
      if (!o){
         res.send(e, 400);
      }	else{
	 req.session.user = o;
      if (req.param('remember-me') == 'true'){
	 res.cookie('user', o.user, { maxAge: 900000 });
	 res.cookie('pass', o.pass, { maxAge: 900000 });
      }			
	 res.send(o, 200);
      }
    });
   }
});

router.get('/view_active_sensors', function(req, res, next) { 
  dbDriver.getAllActiveSensors(50*1000*60, function(sensorList) {
  		//console.log(sensorList.length)
  		console.log(sensorList)
  		res.render('view_active_sensors', {title: 'Active Sensors', sensorList:sensorList});
  	})
  
  
});

router.get('/add_gateway', function(req, res, next) {
  res.render('add_gateway', { title: 'Add a New Gateway'});
});
router.get('/add_sensor', function(req, res, next) {
  res.render('add_sensor', { title: 'Add a New Sensor'});
});
// creating new accounts //
router.get('/signup', function(req, res, next) {
  res.render('signup', { title: 'Create a New Account', countries : CT });
});


router.post('/signup', function(req, res){
		AM.addNewAccount({
			name 	: req.param('name'),
			email 	: req.param('email'),
			user 	: req.param('user'),
			pass	: req.param('pass'),
			country : req.param('country')
		}, function(e){
			if (e){
				res.send(e, 400);
			}	else{
				res.send('ok', 200);
			}
		});
	});

// logged-in user homepage //
	
	router.get('/home', function(req, res) {
	    if (req.session.user == null){
	// if user is not logged-in redirect back to login page //
	        res.redirect('/');
	    }   else{
			res.render('home', {
				title : 'Control Panel',
				countries : CT,
				udata : req.session.user
			});
	    }
	});
	
	router.post('/home', function(req, res){
		if (req.param('user') != undefined) {
			AM.updateAccount({
				user 		: req.param('user'),
				name 		: req.param('name'),
				email 		: req.param('email'),
				country 	: req.param('country'),
				pass		: req.param('pass')
			}, function(e, o){
				if (e){
					res.send('error-updating-account', 400);
				}else{
					req.session.user = o;
			// update the user's login cookies if they exists //
					if (req.cookies.user != undefined && req.cookies.pass != undefined){
						res.cookie('user', o.user, { maxAge: 900000 });
						res.cookie('pass', o.pass, { maxAge: 900000 });	
					}
					res.send('ok', 200);
				}
			});
		}	else if (req.param('logout') == 'true'){
			res.clearCookie('user');
			res.clearCookie('pass');
			req.session.destroy(function(e){ res.send('ok', 200); });
		}
	});
	
	router.post('/add_sensor', function(req, res){
		jsonObject = JSON.stringify ({
			"gatewayid":req.param('gid'),
			"sensorid":req.param('sid'),
			"sensortype":req.param('stype'),
			"devicehandler":req.param('dhandle'),
			"protocol":req.param('wfmat'),
			"lat":req.param('lat'),
			"long":req.param('long'),
			"mode":req.param('mode')
		});
		
		//console.log(jsonObject)
		dbDriver.addNewSensor({
			"gatewayid":req.param('gid'),
			"sensorid":req.param('sid'),
			"sensortype":req.param('stype'),
			"devicehandler":req.param('dhandle'),
			"protocol":req.param('wfmat'),
			"lat":req.param('lat'),
			"long":req.param('long'),
			"mode":req.param('mode')
		});
		
		res.redirect('/home');
	});
	
	
	
	router.post('/app_register', function(req, res){
		jsonObject = JSON.stringify ({
		name: req.param('name'),
		startX: req.param('startX'),
		startY: req.param('startY'),
		endX: req.param('endX'),
		endY: req.param('endY')
		});	

		var postheaders = {
		    'Content-Type' : 'application/json',
		    'Content-Length' : Buffer.byteLength(jsonObject, 'utf8')
		};

		var optionspost = {
		    host : '10.42.0.24',
		    port : 3000,
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
		//res.send('ok', 200);
		res.redirect('/home');
	});

	router.get('/app_register', function(req, res, next) {
  	res.render('app_register', { title: 'Register a New Application'});
});
module.exports = router;
