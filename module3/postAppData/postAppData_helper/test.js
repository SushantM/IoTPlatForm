var Graph = require('./graph.js').Graph;
var Dijkstra = require('./dijkstra.js').Dijkstra;


myfunction=function(callback){
	graph=new Graph('new graph');
	dj=new Dijkstra(graph);
	graph.addVertex('1');
	graph.addVertex('2');
	graph.addVertex('3');
	graph.addVertex('4');
	graph.addVertex('5');

	graph.addEdge('1','2',7);
	graph.addEdge('3','2',6);
	graph.addEdge('3','4',1);
	graph.addEdge('1','4',8);
	graph.addEdge('4','5',5);
	graph.addEdge('2','5',15);
	graph.addEdge('1','3',6);

	console.log(dj.getShortestPath('4', '5'));
	callback('done');
};

myfunction(function(res){});
