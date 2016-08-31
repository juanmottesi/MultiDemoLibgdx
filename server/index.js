var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];

server.listen(8080, function(){
	log("Server is new running...");
});

io.on('connection', function(socket){
	socket.emit('getPlayers', players);
	socket.broadcast.emit("newPlayer", { id: socket.id });
	socket.on('playerMoved', function(data){
	    data.id = socket.id;
	    socket.broadcast.emit('playerMoved', data);
        updatePlayer(data);
	});
	socket.on('disconnect', function(){
		socket.broadcast.emit('playerDisconnected', { id: socket.id });
		removePlayer(socket.id);
	});
	players.push(new player(socket.id, 0,0));
});

function player(id, x, y){
    this.id = id;
    this.x = x;
    this.y = y;
};

function log(comment){
    console.log(comment);
};

function updatePlayer(data){
    for(var i = 0; i < players.length; i++){
        if(players[i].id == data.id){
            players[i].x = data.x;
            players[i].y = data.y;
        };
    };
};

function removePlayer(playerID){
    for(var i = 0; i < players.length; i++){
        if(players[i].id == playerID){
            players.splice(i, 1);
        };
    };
};
