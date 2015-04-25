var express = require('express'),
    reg = require('./routes/register');

var fs=require('fs');  
var config = JSON.parse(fs.readFileSync('config.json', 'utf8'));
var app = express();
 
app.configure(function () {
    app.use(express.logger('dev'));     /* 'default', 'short', 'tiny', 'dev' */
    app.use(express.bodyParser());
});
 
app.get('/viewAll', reg.findAll);
app.get('/viewAll/:id', reg.findById);
app.post('/register', reg.registerApp);
app.post('/deleteapp', reg.deleteapp);
app.put('/updateApp/:id', reg.updateApp);
app.delete('/deleteApp/:id', reg.deleteApp);
 
app.listen(config.register_port);
console.log('Listening on port ',config.register_port);
