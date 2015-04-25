# IoTPlatForm


Module1: Gateway, Sensor

Inside "APK" directory there is an android app file named "Gateway.apk". Install this app on android device which you want to treat as gateway for sensors. Do same for "Sensor.apk".

Turn on bluetooth of both Gateway and Sensor devices.

To Start Gateway
	-On opening Gateway app on Gateway device, click on "Start Gateway" button to activate gateway.

To Start Sensor
	-On opening Sensor app on sensor device, click on "Start Sensor" button to activate sensor.

Module2: Filter Server, Registry Repository Security Severs

Filter Server

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



