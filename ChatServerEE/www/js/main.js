			window.serverURL = "/ChatServerEE/chat";
			window.login = "";
			window.status = "";
			window.isPrivate = "";
			window.selectedRoom = "GENERAL";


			function loginAction() {
				var param = "type=login&operation=enter&login=" + document.getElementById("loginTxt").value + "&password=" + document.getElementById("passwordTxt").value;
				
				var xhttp = new XMLHttpRequest();
				document.getElementById("loading").style.display="block";
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {						
						location.reload();
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", window.serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}
	     
			function registerAction() {
				var param = "type=login&operation=register&login=" + document.getElementById("loginTxt").value + "&password=" + document.getElementById("passwordTxt").value;
				
				var xhttp = new XMLHttpRequest();
				document.getElementById("loading").style.display="block";
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						location.reload();
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", window.serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}

			function exitAction() {
				var param = "type=login&operation=exit";
				
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						location.reload();
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("GET", window.serverURL + "?" + param, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}
			
			function getCurrentLogin() {
				var param = "type=user&operation=getLogin";
				
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						window.login = JSON.parse(xhttp.responseText);
						if(window.login){
							getStatusAction();
							loadUsers();
							loadRooms('GENERAL');
							loadMessages();
							loadPrivates()
							window.usersInterval = setInterval(loadUsers, 1000);
							window.roomsInterval = setInterval(loadRooms, 1000);

							window.privatesInterval = setInterval(loadPrivates, 1000);
							window.messagesInterval = setInterval(loadMessages, 1000);
						}						
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				xhttp.open("GET", window.serverURL + "?" + param, true);
 				xhttp.setRequestHeader('Content-Type', 'application/json')
				xhttp.send();
			}
			
			function getStatusAction() {
				var param = "type=user&operation=getStatus";
				
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						window.status = JSON.parse(xhttp.responseText);
						document.getElementById("nav-status").innerHTML = window.status;
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("GET", window.serverURL + "?" + param, true);
 				xhttp.setRequestHeader('Content-Type', 'application/json')
				xhttp.send(param);
			}
			
			function setStatusAction(newStatus) {
				var param = "type=user&operation=status&status=" + newStatus;
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						getStatusAction();
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", window.serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}

			function loadUsers() {
				var param = "type=user&operation=query";
				
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						var json = xhttp.responseText;
						var obj = JSON.parse(json);
						var content = "";
						obj.forEach(function(item, i, obj) {
								if(item.name != window.login){
									content = content + "<tr><td>" + item.name + "</td><td>" + item.status + "</td>" +
											"<td><a href=\"#\" onclick=\"createPrivate('" + item.name + "');return false;\"><img src=\"/ChatServerEE/img/write.png\" width=\"20px\" height=\"20px\"></a></td></tr>"
								}							
							});
						document.getElementById("users").innerHTML = "<table>" + content + "</table>";
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("GET", window.serverURL + "?" + param, true);
 				xhttp.setRequestHeader('Content-Type', 'application/json')
				xhttp.send(param);
			}
			
			function createPrivate(user){
				var param = "type=rooms&operation=addPrivate&user=" + user + "&initiator="+ window.login;
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						loadPrivates();
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", window.serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}

			function loadRooms() {
				var param = "type=rooms&operation=queryPublic";
				
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						var json = xhttp.responseText;
						var obj = JSON.parse(json);
						var content = "";
						obj.forEach(function(item, i, obj) {
							content = content + "<tr><td><a href=\"#\" onclick=\"changeRoomSelection('" + item.name + "');return false;\">" + item.name + "</a></td>"
							if(item.initiator == login){								 
								content = content + "<td><a href=\"#\" onclick=\"deleteRoom('" + item.name + "');return false;\"><img src=\"/ChatServerEE/img/cross.png\" width=\"20px\" height=\"20px\"></a>"
							}
							content = content + "<td></td></tr>";	
						});
						document.getElementById("rooms").innerHTML = "<table>" + content + "</table>";
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("GET", window.serverURL + "?" + param, true);
 				xhttp.setRequestHeader('Content-Type', 'application/json')
				xhttp.send(param);
			}
			
			function loadPrivates() {
				var param = "type=rooms&operation=queryPrivate&name=" + window.login;
				
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						var json = xhttp.responseText;
						var obj = JSON.parse(json);
						var content = "";
						obj.forEach(function(item, i, obj) {
							content = content + "<tr><td><a href=\"#\" onclick=\"changePrivateRoomSelection('" + item.name + "');return false;\">" + item.name + "</a></td>"
															 
							content = content + "<td><a href=\"#\" onclick=\"deletePrivateRoom('" + item.name + "');return false;\"><img src=\"/ChatServerEE/img/cross.png\" width=\"20px\" height=\"20px\"></a>"
							
							content = content + "<td></td></tr>";	
						});
						document.getElementById("privates").innerHTML = "<table>" + content + "</table>";
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("GET", window.serverURL + "?" + param, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}
			
			function changeRoomSelection(room) {
				window.isPrivate = "false";
				window.selectedRoom = room;
				clearInterval(refreshIntervalId);
				loadMessages();				
			}
			
			function changePrivateRoomSelection(room) {
				window.isPrivate = "true";
				window.selectedRoom = room;
				clearInterval(loadPrivates, 1000, true);
				loadMessages();				
			}
			
			function addRooms() {
				var param = "type=rooms&operation=addPublic&name=" + document.getElementById("room-input-txt").value + "&initiator="+ window.login;
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						loadRooms();
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", window.serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}
			
			function deleteRoom(roomName) {
				var param = "type=rooms&operation=remPublic&name=" + roomName + "&initiator=" + window.login;
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						loadRooms();
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", window.serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}	
			function deletePrivateRoom(roomName) {
				var param = "type=rooms&operation=remPrivate&user=" + roomName + "&initiator=" + window.login;

				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						loadRooms();
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", window.serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}	
			
			function sendMessage() {
				var msgtxt = document.getElementById("msg-input-txt").value;
				var d = new Date();
				var param = "";
				var message = {
						date: d.getTime(),
					    from: window.login,
					    text: msgtxt
					};
				
				
				if(window.isPrivate == "true"){
					param = "type=rooms&operation=addPrivateMsg&name=" + window.selectedRoom + "&user=" + window.login + "&message=" + JSON.stringify(message);
				}else{
					param = "type=rooms&operation=addMsg&roomName=" + window.selectedRoom + "&message=" + JSON.stringify(message);
				}
				 
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						loadMessages();
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", window.serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}		

			function loadMessages() {
				if(typeof window.selectedRoom == 'undefined'){
					window.selectedRoom = "GENERAL";
				}
				var param = "";
				if(window.isPrivate == "true"){
					param = "type=rooms&operation=queryPrivateMsg&user=" + window.login + "&name=" + window.selectedRoom + "&n=0";
				}else{
					param = "type=rooms&operation=queryMsg&name=" + window.selectedRoom + "&n=0";
				}
				
				
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						var json = xhttp.responseText;
						var obj = JSON.parse(json);
						var content = "";
						obj.forEach(function(item, i, obj) {
							content = content + "<tr><th>" + item.from + "</th><td>" + item.text + "</td><td>" + item.date+ "</td></tr>"
							});
						document.getElementById("messages").innerHTML = "<table>" + content + "</table>";
						//document.getElementById("info").innerHTML = json;
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("GET", window.serverURL + "?" + param, true);
 				xhttp.setRequestHeader('Content-Type', 'application/json')
				xhttp.send(param);
			}
			
			window.onload = function(){
				getCurrentLogin();
						
			}
