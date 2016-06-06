<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Prog.kiev.ua</title>
    <script>  
		window.onload = function(){ 
			var serverURL = "/ChatServerEE/chat";

			document.getElementById("loginBtn").onclick = function() {
// 				var data = new FormData();
// 				data.append('type', 'login');
// 				data.append('operation', 'enter');
// 				data.append('login', document.getElementById("loginTxt").value);
// 				data.append('password', document.getElementById("passwordTxt").value);

				var param = "type=login&operation=enter&login=" + document.getElementById("loginTxt").value + "&password=" + document.getElementById("passwordTxt").value;
				
				var xhttp = new XMLHttpRequest();	        
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						location.reload();
					}
					if (xhttp.readyState == 4 && xhttp.status == 400) {
						document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}
	     
			document.getElementById("registerBtn").onclick = function() {
// 				var data = new FormData();
// 				data.append('type', 'login');
// 				data.append('operation', 'register');
// 				data.append('login', document.getElementById("loginTxt").value);
// 				data.append('password', document.getElementById("passwordTxt").value);

				var param = "type=login&operation=register&login=" + document.getElementById("loginTxt").value + "&password=" + document.getElementById("passwordTxt").value;
				
				var xhttp = new XMLHttpRequest();
				xhttp.onreadystatechange = function() {
					if (xhttp.readyState == 4 && xhttp.status == 200) {
						document.getElementById("info").innerHTML = xhttp.responseText;
					}
				};
				
				xhttp.open("POST", serverURL, true);
 				xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
				xhttp.send(param);
			}
		};
    	     

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
	        	<td><input type="button" id="loginBtn" value="Log in"></td>
	            <td><input type="button" id="registerBtn" value="Register"></td>
            </tr>
        </table>
    <% } else { %>
        <h1>You are logged in as: <%= login %></h1>
        <br>Click this link to <a href="/ChatServerEE/chat?type=login&operation=exit">logout</a>
    <% } %>
    <div id="info">
    </div>
  </body>
</html>
