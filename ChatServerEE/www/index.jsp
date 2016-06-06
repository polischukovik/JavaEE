<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Prog.kiev.ua</title>
    <script>  
		//window.onload = function(){ 
			var serverURL = "/ChatServerEE/chat";

			function loginAction() {

				var param = "type=login&operation=enter&login=" + document.getElementById("loginTxt").value + "&password=" + document.getElementById("passwordTxt").value;
				
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						location.reload();
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}
	     
			function registerAction() {

				var param = "type=login&operation=register&login=" + document.getElementById("loginTxt").value + "&password=" + document.getElementById("passwordTxt").value;
				
				var xhttp = new XMLHttpRequest();
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						location.reload();
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", serverURL, true);
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
				
				xhttp.open("GET", serverURL + "?" + param, true);
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
							content = content + "<tr><th>" + item.name + "</th><td>" + item.status + "</td></tr>"
							});
						document.getElementById("users").innerHTML = "<table>" + content + "</table>";
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("GET", serverURL + "?" + param, true);
 				xhttp.setRequestHeader('Content-Type', 'application/json')
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
							content = content + "<tr><th>" + item.name + "</th></tr>"
							});
						document.getElementById("rooms").innerHTML = "<table>" + content + "</table>";
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						//document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("GET", serverURL + "?" + param, true);
 				xhttp.setRequestHeader('Content-Type', 'application/json')
				xhttp.send(param);
			}

			function loadMessages() {

				var param = "type=rooms&operation=queryMsg&name=Alex created room&n=0";
				
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
				
				xhttp.open("GET", serverURL + "?" + param, true);
 				xhttp.setRequestHeader('Content-Type', 'application/json')
				xhttp.send(param);
			}
		//};
    	     

 	 </script> 
  </head>  
  <body>
    <% String login = (String)session.getAttribute("user_login"); %>

    <% if (login == null || "".equals(login)) { %>
        <table id="loginForm" >
	        <tr>
	        	<th>Login:</th><td><input type="text" id="loginTxt"></td>
	        </tr>
	        <tr>
	        	<th>Password:</th><td><input type="password" id="passwordTxt"></td>
	        </tr>
	        <tr>
	        	<td><input type="button" id="loginBtn" value="Log in" onclick="loginAction()"></td>
	            <td><input type="button" id="registerBtn" value="Register" onclick="registerAction()"></td>
            </tr>
        </table>
    <% } else { %>
        <h1>You are logged in as: <%= login %></h1>
        <br><input type="button" id="exitBtn" value="Exit" onclick="exitAction()"><br>
        
        <div id="users-container">
        	<h2>Users</h2>
        	<input type="button" id="loadUsers" value="loadUsers" onclick="loadUsers()">
	        <div id="users">
		    </div>        
	    </div>
	    <div id="rooms-container">
        	<h2>Rooms</h2>
        	<input type="button" id="loadRooms" value="loadRooms" onclick="loadRooms()">
		    <div id="rooms">
		    </div>
		</div>
		<div id="messages-container">
			<h2>Messages</h2>
			<input type="button" id="loadMessages" value="loadMessages" onclick="loadMessages()">
	    	<div id="messages">
	    	</div>
	    </div>
	    <div id="info">
	    </div>
  </body>
    <% } %>
    
</html>
