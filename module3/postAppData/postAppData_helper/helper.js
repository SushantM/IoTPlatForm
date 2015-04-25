var express = require('express'),
    postAppData = require('./routes/postAppData');
    
 
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
 
app.listen(3004);
console.log('Listening on port 3004...');
