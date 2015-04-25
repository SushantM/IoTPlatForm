var mongo = require('mongodb');
var fs = require('fs');
var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));


var Server = mongo.Server,
    Db = mongo.Db,
    ObjectID = mongo.ObjectID;
 
var server = new Server(config.app_engine_ip, 27017, {auto_reconnect: true});
db = new Db('filterDataDb', server);
 
db.open(function(err, db) {
    if(!err) {
        console.log("Connected to 'filterDataDb' database");
        db.collection('filterData_health', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The 'filterData_health' collection doesn't exist. Creating it with sample data...");
                //populateDB1();
            }
        });
        
        db.collection('user_message', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The 'user_message' collection doesn't exist. Creating it with sample data...");
                //populateDB2();
            }
        });
    }
});
 
exports.findById = function(req, res) {
    db.collection('filterData_health', function(err, collection) {
        collection.findOne({'name':req.params.id}, function(err, item) {
            /*collection.remove({'name':req.params.id}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' document(s) deleted');
                
            }
            })*/
            res.send(item);
        });
    });
};
exports.getUserData = function(req, res) {
    db.collection('user_message', function(err, collection) {
        collection.findOne({'name':req.params.id}, function(err, item) {
            collection.remove({'name':req.params.id}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' document(s) deleted');
                
            }
            })
            res.send(item);
        });
    });    
};
 
exports.findAll = function(req, res) {
    db.collection('filterData_health', function(err, collection) {
        collection.find().toArray(function(err, items) {
            /*collection.remove({'name':'TEST1_APPLICATION'}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' document(s) deleted');
                
            }
            })*/
            res.send(items);
        });
    });
};
exports.getAllUserData = function(req, res) {
    db.collection('user_message', function(err, collection) {
        collection.find().toArray(function(err, items) {
            res.send(items);
        });
    });
};
 
exports.addApp = function(req, res) {
    var app = req.body;
    console.log('Adding filter data: ' + JSON.stringify(app));
    db.collection('filterData_health', function(err, collection) {
        collection.insert(app, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred'});
            } else {
                console.log('Success: ' + JSON.stringify(result[0]));
				
                res.send(result[0]);
            }
        });
    });
}
 
exports.postUserData = function(req, res) {
    var app = req.body;
    console.log('Adding message data: ' + JSON.stringify(app));
    db.collection('user_message', function(err, collection) {
        collection.insert(app, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred'});
            } else {
                console.log('Success: ' + JSON.stringify(result[0]));
                res.send(result[0]);
            }
        });
    });
}
exports.updateApp = function(req, res) {
    //var id = req.params.id;
    var id = ObjectID(req.params.id);
    var app = req.body;
    console.log('Updating filter Data: ' + id);
    console.log(JSON.stringify(app));
    db.collection('filterData_health', function(err, collection) {
        collection.update({'_id':id}, app, {safe:true}, function(err, result) {
            if (err) {
                console.log('Error updating filter Data: ' + err);
                res.send({'error':'An error has occurred'});
            } else {
                console.log('' + result + ' document(s) updated');
                res.send(app);
            }
        });
    });
}
 
exports.deleteApp = function(req, res) {
   // var id = req.params.id;
    var id = ObjectID(req.params.id);
    console.log('Deleting filter Data: ' + id);
    db.collection('filterData_health', function(err, collection) {
        collection.remove({'_id':id}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' document(s) deleted');
                res.send(req.body);
            }
        });
    });
}
 
/*--------------------------------------------------------------------------------------------------------------------*/
// Populate database with sample data -- Only used once: the first time the application is started.

var populateDB1 = function() {
 
    var apps = [
    {
        name: "Test1_Application",
        valueType: "Temperature",
        value: "40",
        timestamp:"1234",
       	lat:"12",
       	long:"15"
    }];
 
    db.collection('filterData_health', function(err, collection) {
        collection.insert(apps, {safe:true}, function(err, result) {});
    });
 
};

var populateDB2 = function() {
 
    var apps = [
    {
        name: "Test1_Application",
        message: "Hello!!"
    }];
 
    db.collection('user_message', function(err, collection) {
        collection.insert(apps, {safe:true}, function(err, result) {});
    });
 
};


exports.postTrafficData = function(req, res) {
    var app = req.body;
    console.log('Adding filter data: ' + JSON.stringify(app));
    db.collection('filterData_traffic', function(err, collection) {
        collection.insert(app, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred'});
            } else {
                console.log('Success: ' + JSON.stringify(result[0]));
				
                res.send(result[0]);
            }
        });
    });
}



exports.postHealthData = function(req, res) {
    var app = req.body;
    console.log('Adding filter data: ' + JSON.stringify(app));
    db.collection('filterData_health', function(err, collection) {
        collection.insert(app, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred'});
            } else {
                console.log('Success: ' + JSON.stringify(result[0]));
				
                res.send(result[0]);
            }
        });
    });
}

exports.getTrafficData = function(req, res) {
    db.collection('filterData_traffic', function(err, collection) {
        collection.find().toArray(function(err, items) {
            /*collection.remove({'name':'TEST1_APPLICATION'}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' document(s) deleted');
                
            }
            })*/
            res.send(items);
        });
    });
};

exports.getHealthData = function(req, res) {
    db.collection('filterData_health', function(err, collection) {
        collection.find().toArray(function(err, items) {
            collection.remove({'name':'1234'}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' document(s) deleted');
                
            }
            })
            res.send(items);
        });
    });
};
