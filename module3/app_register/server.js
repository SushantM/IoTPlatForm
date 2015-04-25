var express = require('express'),
    register = require('./routes/register');
    
 
var app = express();

var fs = require('fs');
var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));



app.configure(function () {
    app.use(express.logger('dev'));     /* 'default', 'short', 'tiny', 'dev' */
    app.use(express.bodyParser());
});
 
app.get('/register', register.findAll);
app.get('/register/:id', register.findById);
app.post('/register', register.addApp);
app.put('/register/:id', register.updateApp);
app.delete('/register/:id', register.deleteApp);
 
app.listen(config.app_registering_port);
console.log('Listening on port ', config.app_registering_port);
