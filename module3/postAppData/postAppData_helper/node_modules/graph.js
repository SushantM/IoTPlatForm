Graph = function(name) {
	this.name=name;
	this.edges={};
	this.vertices=[];
};


Graph.prototype.getVertices=function(){
	return this.vertices;
};

Graph.prototype.addVertex= function(vertex) {
	this.vertices.push(vertex);
	this.edges[vertex] = {};
	//callback('NODE ADDED');
  };

Graph.prototype.addEdge= function(u, v, distance) {
    //var ukey = stringify(u);
    //var vkey = stringify(v);
    this.edges[u][v] = distance;
    //this.edges[vkey][ukey] = distance;
  };

Graph.prototype.distance= function(u, v) {
    //var ukey = stringify(u);
    //var vkey = stringify(v);
    return this.edges[u][v];
  },

Graph.prototype.getNeighbors= function(vertex) {
	neighbors=this.edges[vertex];
	return Object.keys(neighbors);
  };

exports.Graph = Graph;
