DatabaseDriver = function(db,dbName, registry, repository, mongodbPort) {
	this.dbName=dbName;
	this.registry=registry;
	this.repository=repository;
	this.dbport=mongodbPort;
	this.db=db;
};

exports.addNewSensor = function(jsonRecords,callback) {
	//insert record
	console.log("Sensoe add");
	console.log(jsonRecords)
	this.db.collection(this.repository).insert(jsonRecords,{w: 1}, function(err, records) {
		if (err) throw err;
		console.log("New sensor added ");
	});
};

DatabaseDriver.prototype.insertToRegistry = function(jsonRecords) {
	//insert record
	this.db.collection(this.registry).insert(jsonRecords, function(err, records) {
		if (err) throw err;
		console.log("New record added in registry ");
	});
};

DatabaseDriver.prototype.isValidSensor = function(sensorID, callback) {
	this.db.collection(this.repository).find({'sensorid':sensorID}).toArray(function(err, records) {
		if (err) throw err;
		if(records.length>=1)
		{
			console.log("sensor found");
			callback(true);
		}
		else
			callback(false);
	});
};

DatabaseDriver.prototype.isValidGateway = function(gatewayID, callback) {
	this.db.collection(this.repository).find({'gatewayid':gatewayID}).toArray(function(err, records) {
		if (err) throw err;
		if(records.length>=1)
		{
			console.log("gateway found");
			callback(true);
		}
		else
			callback(false);
	});
};

DatabaseDriver.prototype.getSensorByLocation = function(mylocation, range, sensortype, callback) {
	/* location={"latitude":123, "longitude":465} */
	/* callback function shall provide and empty array, which is populated during runtime of this function and returned */

	this.db.collection(this.repository).find({'sensortype':sensortype},{'sensorid':1,'location':1}).toArray(function(err, records) {
		if (err) throw err;
		var result=[];
		records.forEach(function(record) {
			if(inRange(mylocation, record.location, range))
				result.push(record.sensorid);
		});
		callback(result);
	});
};

DatabaseDriver.prototype.getSensorByType = function(sensortype, callback) {
	/* callback function shall provide and empty array, which is populated during runtime of this function and returned */

	this.db.collection(this.repository).find({'sensortype':sensortype},{'sensorid':1}).toArray( function(err, records) {
		if (err) throw err;
		var result=[];
		records.forEach(function(record) {
			result.push(record.sensorid);
		});
		callback(result);
	});
};

function inRange(mylocation, sensorlocation, range)
{
	var slat=sensorlocation.latitude;
	var slong=sensorlocation.longitude; //1
	var mylat=mylocation.latitude;
	var mylong=mylocation.longitude; //2

	var theta = slong - mylong;
	var dist = Math.sin(deg2rad(slat)) * Math.sin(deg2rad(mylat)) + Math.cos(deg2rad(slat)) * Math.cos(deg2rad(mylat)) * Math.cos(deg2rad(theta));
	dist = Math.acos(dist);
	dist = rad2deg(dist);
	dist = dist * 60 * 1.1515;
	     
	/*assuming unit is N */
        dist = dist * 0.8684;

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

DatabaseDriver.prototype.updateLastUptime = function(sensorid, time) {
	this.db.collection(this.registry).update({'sensorid':sensorid},{$set:{'lastupdatetime':time}}, function(err, result) {
		if (err) throw err;
		console.log("Record updated with sensor id "+sensorid+","+result);
	});
};

DatabaseDriver.prototype.updateLastDownTime = function(sensorid, time) {
	this.db.collection(this.registry).update({'sensorid':sensorid},{$set:{'lastdowntime':time}}, function(err, result) {
		if (err) throw err;
		console.log("Record updated with sensor id "+sensorid+","+result);
	});
};

DatabaseDriver.prototype.isSensorActive = function(sensorid, timelimit, callback) {
	/* timelimit must be in miliseconds */
	this.db.collection(this.registry).find({'sensorid':sensorid},{'lastupdatetime':1}).toArray( function(err, records) {
		if (err) throw err;
		var d = new Date();
		if((d.getTime()-records[0].lastupdatetime)<timelimit)
			callback(true);
		else
			callback(false);
	});
};


exports.DatabaseDriver = DatabaseDriver;
