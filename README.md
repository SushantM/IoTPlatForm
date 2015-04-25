# IoTPlatForm


# Module1: Gateway, Sensor

Inside "APK" directory there is an android app file named "Gateway.apk". Install this app on android device which you want to treat as gateway for sensors. Do same for "Sensor.apk".

Turn on bluetooth of both Gateway and Sensor devices.

To Start Gateway
	-On opening Gateway app on Gateway device, click on "Start Gateway" button to activate gateway.

To Start Sensor
	-On opening Sensor app on sensor device, click on "Start Sensor" button to activate sensor.

# Module2: Filter Server, Registry Repository Security Severs

Filter Server
Inside "filter_server"

Inside "receive_data" directory, there are files for creating socket server and receiving data
and to post the data to the app engine upon matching the conditions.

Edit the config.json file to configure the filter server.

To start the socket server and receive data from gateways

>> node socket_server.js

To Start the service which will check conditions (id lists) and post the data to app engine
using POST API. It will delete the data once it is posted to app engine.

>> node detect_data.js

Inside "register_app" directory, there is code to register the app with the filter server.
The code will take appid and the list of sensor ids related to that app from app engine and
whenever the data is received from those sensors, filter will send the data to app engine
using "detect_data.js" script previously run.

>> node server.js

Registry Repository Security Severs
Inside "registry-repository"

Inside "boot_strap" directory, socket server which exposes socket APIS to gateway are kept.
These APIs help in boot straping the system and updating the status related to sensors.
Database used for registry, repository and security is "iotdb"

run

>> bash setup.sh
>> node boot_strap.sh

Inside "api_server" directory, all the apis for accessing the repository and registry are kept.
Install all the packages needed using npm. To expose the APIs, run

>> node server.js

Web UI Portal

Inside "web_admin_portal" directory, all the code related to UI is placed.
It is built using express package with help of JADE utility.
Run
Need to install the required npm packages before running this.

>> npm build
>> npm start

# Module 3
App Engine server and apps

Start the server to register new apps

Enter app_register folder
Config file can be edited to change ip and port addresses.

>> node server.js

Now apps can be added by sending a POST request to the server or through the web UI

Start the server for applications

Enter postAppData folder
Config file can be edited to change ip and port addresses.

>> node server.js

Enter postAppData_helper directory and run helper.js 

>>node helper.js


This platform was developed by following students under the guidance of Mr. Ramesh Loganathan
as part of Internal of Application server course.
1. Gangasagar Patil
2. Poorva Bhawsar
3. Sushant Makode
4. Kedar Biradar
5. Omprakash Shewale
6. Nikhil Barote
7. Harshad Jalan
8. Kapil Chhajer
9. Diwas Joshi
