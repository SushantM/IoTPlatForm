var jade = require('jade');
// Compile a function
var fn = jade.compile('string of jade', options);
// Render the function
var html = fn(locals);

//document.write(html.sensorList);	
//document.write(#{html.sensorList});
//document.write(#{sensorList});
document.write(title);
var table = document.createElement("TABLE");
table.border = "1";
var columncount = 4;
var row = table.insertRow(-1);
for(var i=0; i<columncount; i++){
	var headercell = document.createElement("TH");
	headerCell.innerHTML = sensorList[0][i];
	row.appendChild(headercell);
}

for(var i=1;i<sensorList.length;i++){
	row = table.inserRow(-1);
	for(var j=0;j<columncount;j++){
	  	var cell = row.insertcell(-1);
	  	cell.innerHTML = sensorList[i][j];
	}
}
var dvTable = document.getElementById("dvTable");
dvTable.innerHtml = "";
dvTable.appendChild(table);

