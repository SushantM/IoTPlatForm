var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var passport = require('passport');
var routes = require('./routes/index');
var users = require('./routes/users');
var monk = require('monk');
var mongoose = require('mongoose');
var AM = require('./node_modules/account-manager');
var EM = require('./node_modules/email-dispatcher');
//var DD = require('./node_modules/databaseDriver');
var app = express();
var dbName = monk('localhost:27017/iotdb');
var session = require('client-sessions');

//var stylus = require('stylus');
//mongoose.connect(configDB.url); // connect to our database

// require('./config/passport')(passport); // pass passport for configuration

// view engine setup

/*function compile(str, path) {
  return stylus(str)
    .set('filename', path)
    .use(nib())
}
app.use(stylus.middleware(
  { src: __dirname + '/public'
  , compile: compile
  }
))*/
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(express.static(path.join(__dirname, 'public')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(function(req,res,next){
    req.dbName = dbName;
    next();
});
app.use(session({
  cookieName: 'session',
  secret: 'something',
  duration: 30 * 60 * 1000,
  activeDuration: 5 * 60 * 1000,
}));
app.use('/', routes);
app.use('/users', users);


// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});


module.exports = app;
