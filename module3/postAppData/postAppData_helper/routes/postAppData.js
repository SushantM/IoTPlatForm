var mongo = require('mongodb');
var Graph = require('graph.js').Graph;
var Dijkstra = require('dijkstra.js').Dijkstra;
var http = require('http');
var Server = mongo.Server,
    Db = mongo.Db,
    ObjectID = mongo.ObjectID;
    
var fs = require('fs');
var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));

 
var server = new Server(config.app_engine_ip, 27017, {auto_reconnect: true});
db = new Db('filterDataDb', server);

graph=new Graph('new graph');
dj=new Dijkstra(graph);

graph.addVertex('A');
graph.addVertex('B');
graph.addVertex('C');
graph.addVertex('D');
graph.addVertex('E');

var hash = { 'A' : {'x':645.0, 'y':745.0},
	     'B': {'x':57.0, 'y':45.0},
	     'C' : {'x':60.0, 'y':760.0},
	     'D' : {'x':645.0, 'y':40.0},
	     'E' : {'x':348.0, 'y':1060.0} }

db.open(function(err, db) {
    if(!err) {
        console.log("Connected to 'filterDataDb' database");
        db.collection('filterData_traffic', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The 'filterData_traffic' collection doesn't exist. Creating it with sample data...");
                db.createCollection("filterData_traffic", function(err, collection){
   			 console.log("filterData_traffic is created"); 
		});//populateDB1();
            }
            for( var key in hash ) {
            	console.log( key, hash[key] )
	    }
		graph.addEdge('B','D',3);
		graph.addEdge('B','C',10);
		graph.addEdge('D','C',6);
		graph.addEdge('D','A',8);
		graph.addEdge('B','A',12);
		graph.addEdge('A','E',3);
		graph.addEdge('C','E',2);
        });
        
        db.collection('traffic_message', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The 'traffic_message' collection doesn't exist. Creating it with sample data...");
                db.createCollection("traffic_message", function(err, collection){
   			 console.log("traffic_message is created");
		});//populateDB2();
            }
        });
    }
});
 
exports.findById = function(req, res) {
    db.collection('filterData_traffic', function(err, collection) {
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
exports.getUserData = function(req, res) {
    db.collection('traffic_message', function(err, collection) {
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
    db.collection('filterData_traffic', function(err, collection) {
        collection.find().toArray(function(err, items) {
            res.send(items);
        });
    });
};
exports.getAllUserData = function(req, res) {
    db.collection('traffic_message', function(err, collection) {
        collection.find().toArray(function(err, items) {
            res.send(items);
        });
    });
};
 
exports.addApp = function(req, res) {
    var app = req.body;
    console.log('Adding filter data: ' + JSON.stringify(app));
    
    sX=parseInt(app['start_x']);
    sY=parseInt(app['start_y']);
    	
    dX=parseInt(app['end_x']);
    dY=parseInt(app['end_y']);
    
    edgeWeight=parseInt( app['value'] )
    id=app['id']

    
    var S=get_mapping(sX,sY)
    var D=get_mapping(dX,dY)
    
    console.log("Updating weight from "+ S + " to " + D  +" by " + edgeWeight)
    graph.addEdge(S,D,edgeWeight);
    res.send( 'Edge ( '+S+' , '+ D +" )" + " updated to "+ edgeWeight+'\n')
}
 
exports.postUserData = function(req, res) {
    var app = req.body;
    console.log('Adding message data: ' + JSON.stringify(app));
    db.collection('traffic_message', function(err, collection) {
    	sX=app['sX'];
    	sY=app['sY'];
    	
    	dX=app['dX'];
    	dY=app['dY'];
    	
    	var S=get_mapping(sX,sY)
    	var D=get_mapping(dX,dY)
    	if( S==D ) 
		res.send(S);
	else {	    		
	    	console.log( "Finding path between " + S + "->" + D )
	    	var next=dj.getShortestPath( S, D )
		console.log( "Next point is : " + next);
		var xyz={'x':hash[next]['x'], 'y':hash[next]['y'] , 'z' :next }
		res.send(xyz);
	}
    });
}
exports.updateApp = function(req, res) {
    //var id = req.params.id;
    var id = ObjectID(req.params.id);
    var app = req.body;
    console.log('Updating filter Data: ' + id);
    console.log(JSON.stringify(app));
    db.collection('filterData_traffic', function(err, collection) {
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
    db.collection('filterData_traffic', function(err, collection) {
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
 


function get_mapping(x,y) {
	console.log("Checking", x,y)
	for( var key in hash ) {
            	var xy= hash[key];
            	if( xy['x']==x && xy['y']==y )
            		return key
	}
	return 'B'
}
