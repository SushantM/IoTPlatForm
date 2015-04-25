var mongo = require('mongodb');
var fs=require('fs'); 
var Server = mongo.Server,
    Db = mongo.Db,
    BSON = mongo.BSONPure,
    ObjectID = mongo.ObjectID,
    apps="apps";
 

var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));


var server = new Server(config.filter_server_ip, config.filter_server_port, {auto_reconnect: true});
db = new Db('datadb', server);
 
db.open(function(err, db) {
    if(!err) {
        console.log("Connected to 'datadb' database");
        db.collection(apps, {strict:true}, function(err, collection) {
            if (err) {
                //console.log("The "+ apps + " collection doesn't exist. Creating it with sample data...");
                //populateDB();
		//call create collection
            }
        });
    }
});
 
exports.findById = function(req, res) {
    var id = ObjectID(req.params.id);
    console.log('Retrieving apps: ' + id);
    db.collection(apps, function(err, collection) {
        collection.findOne({'_id':id}, function(err, item) {
            res.send(item);
        });
    });
};
 
exports.findAll = function(req, res) {
    db.collection(apps, function(err, collection) {
        collection.find().toArray(function(err, items) {
            res.send(items);
        });
    });
};
 
exports.registerApp = function(req, res) {
    var reg = req.body;
    console.log('Adding app: ' + JSON.stringify(reg));

	db.collection(apps, {strict:true}, function(err, collection2) {
		if (err) {
			console.log("The "+ apps +" collection doesn't exist. Creating it ...");
			db.collection(apps, function(err, collection1) {
        		collection1.insert(reg, {safe:true}, function(err, result) {});
				if (err) {
					console.log('error An error has occurred');
			    	} else {
					console.log("Collection Created and Successfully inserted: " + JSON.stringify(reg) + '\n');
				}
		        });
		}
		else {
			collection2.insert(reg, {safe:true}, function(err, result) {
				if (err) {
					console.log('error An error has occurred');
				} else {
					console.log('Successfully inserted: ' + JSON.stringify(reg) + '\n');
			    	}
        		});
		}
		res.send( "Added!!" );
	});

}

exports.deleteapp = function(req, res) {
    var reg = req.body;

    console.log('Adding app: ' + JSON.stringify(reg));
    db.collection(apps, function(err, collection) {
        collection.remove({'name':reg['name']}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' App(s) deleted');
                res.send(req.body);
            }
        });
    });
}
 
exports.updateApp = function(req, res) {
    //var id = req.params.id;
    var id = ObjectID(req.params.id);
    var reg = req.body;
    console.log('Updating app: ' + id);
    console.log(JSON.stringify(reg));
    db.collection(apps, function(err, collection) {
        collection.update({'_id':id}, reg, {safe:true}, function(err, result) {
            if (err) {
                console.log('Error updating app: ' + err);
                res.send({'error':'An error has occurred'});
            } else {
                console.log('' + result + ' document(s) updated');
                res.send(reg);
            }
        });
    });
}
 
exports.deleteApp = function(req, res) {
    var id = req.params.id;
    //var id = ObjectID(req.params.id);
    console.log('Deleting app: ' + id);
    db.collection(apps, function(err, collection) {
        collection.remove({'name':id}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' App(s) deleted');
                res.send(req.body);
            }
        });
    });
}
 
/*--------------------------------------------------------------------------------------------------------------------*/
// Populate database with sample data -- Only used once: the first time the application is started.
var populateDB = function() {
 
    var apps = [
    {
        name: "Test1",
        idlist: "Temperature",
    }];
 
    db.collection(apps, function(err, collection) {
        collection.insert(apps, {safe:true}, function(err, result) {});
    });
 
};
