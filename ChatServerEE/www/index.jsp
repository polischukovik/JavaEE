<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Prog.kiev.ua</title>
    <link rel="stylesheet" href="/ChatServerEE/css/style.css">
    <script src="/ChatServerEE/js/main.js"></script>
  </head>  
  <body>
  <div id="cont">
  	<div id="header">
  		<h1>Chat with JavaEE</h1>
  	</div>
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
      	<div id="nav">
      		<div id="nav-info">      	
  				You are logged in as: <%= login %>
  			</div>
  			<div id="status">
	  			<div class="wrapper">
			        <div class="content">
			            <ul>
			                <li><a href="#" onclick="setStatusAction('ACTIVE');return false;">ACTIVE</a></li>
			                <li><a href="#" onclick="setStatusAction('AWAY');return false;">AWAY</a></li>
			                <li><a href="#" onclick="setStatusAction('INVISIBLE');return false;">INVISIBLE</a></li>
			            </ul>
				    </div>
				    <div id="nav-status" class="parent">dummy</div>
			    </div>
	   		 </div>
  			<a href="#" class="btn" id="exit-button" onclick="exitAction();return false;">Exit</a>
  		</div>		
  		
        <div id="left-panel">
	        
		    <div id="rooms-container">
	        	<h2>Rooms</h2>
			    <div id="rooms">
			    </div>
			    <div id="create-room">
			    <input id="room-input-txt" type="text" width="150px">
			    <a href="#" class="btn" id="add-room-button" onclick="addRooms();return false;">Create</a>
			    </div>
			</div>
			
			<div id="private-container">
				<h2>Privates</h2>
		    	<div id="privates">
		    	</div>		    	
	   		</div>			
			
		</div>
		
		<div id="right-panel">
			<div id="users-container">
	        	<h2>Users</h2>
		        <div id="users">
			    </div>        
		    </div>
		</div>
		
		<div id="messages-container">
			<h2>Messages</h2>
	    	<div id="messages">
	    	</div>
	    	<div id="messages-input">
	    		<textarea id="msg-input-txt" cols="45" rows="5"></textarea>
	    		<a href="#" class="btn" id="send-button" onclick="sendMessage();return false;">Send</a>
	    	</div>
	    </div>


	    <div id="footer">
			Copyright © Polischuk Oleksii
		</div>
	 </div>
  </body>
    <% } %>
    
</html>
