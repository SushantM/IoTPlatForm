var express = require('express'),
    postAppData = require('./routes/postAppData');
    
var fs = require('fs');
var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));
    
 
var app = express();
 
app.configure(function () {
    app.use(express.logger('dev'));     /* 'default', 'short', 'tiny', 'dev' */
    app.use(express.bodyParser());
});
 
app.get('/getAppData', postAppData.findAll);
app.get('/getUserData', postAppData.getAllUserData);
app.get('/getUserData/:id', postAppData.getUserData);
app.get('/getAppData/:id', postAppData.findById);
app.post('/postAppData', postAppData.addApp);
app.post('/postUserData', postAppData.postUserData);
app.put('/postAppData/:id', postAppData.updateApp);
app.delete('/postAppData/:id', postAppData.deleteApp);

app.post('/posttrafficData', postAppData.postTrafficData);
app.post('/posthealthData', postAppData.postHealthData);
app.get('/getTrafficData', postAppData.getTrafficData);
app.get('/getHealthData', postAppData.getHealthData);

app.listen(config.app_engine_port);
console.log('Listening on port',config.app_engine_port);
