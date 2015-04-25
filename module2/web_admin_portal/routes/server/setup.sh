#!/bin/bash

# start mondoDb
service mongodb start

#set up database
mongo db_setup.js

#start server
node server.js
