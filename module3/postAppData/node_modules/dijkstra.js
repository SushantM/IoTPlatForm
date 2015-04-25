Dijkstra = function(graph) {
	this.graph=graph;
};


Dijkstra.prototype.getShortestPath=function(source, destination){
          queue={};
	  var dist = {}, prev = {};
	  dist[source] = 0;
	  queue[source]=0;
	  this.graph.getVertices().forEach(function(vertex) {
		
	    if (vertex!=source) {
	      dist[vertex] = Infinity;
	      prev[vertex] = null;
		queue[vertex]=dist[vertex];
	    }
	  });

	  while (Object.keys(queue).length != 0) {
		// find element in queue with minimum distance
		nextkey=null;
		minval=Infinity;
		Object.keys(queue).forEach(function(key){
			if(queue[key]<minval)
			{
				nextkey=key;
				minval=queue[key];
			}
		});
		delete queue[nextkey];
	    	var neighbors = this.graph.getNeighbors(nextkey);
	   	neighbors.forEach(function(neighborkey) {
	      		var alt = dist[nextkey] + this.graph.distance(nextkey, neighborkey);
	     		if (alt < dist[neighborkey]) {
				dist[neighborkey] = alt;
				prev[neighborkey] = nextkey;
				queue[neighborkey]= alt;
	      		}
	    	});
	
		flag=true;
		Object.keys(queue).forEach(function(key){
			if(queue[key]<Infinity)
				flag=false;
		});
		if(flag==true)
			break;
	  }

	tempnode=destination;
	finalpath=[];
	while(1)
	{
		finalpath.push(prev[tempnode]);
		tempnode=prev[tempnode];
		if(tempnode==source)
			break;
	}
	if(finalpath.length>1)
		return finalpath[finalpath.length-2];
	else if(finalpath.length==1)
		return destination;
};

exports.Dijkstra = Dijkstra;
