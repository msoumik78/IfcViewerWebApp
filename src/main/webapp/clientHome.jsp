<!DOCTYPE html>
<html lang="en">
<head>
    <title>Home Architect</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
    <script src="js/xbim-viewer.debug.bundle.js"></script>
    <link rel="icon" href="images/icon.png">

    <style>
        .navbar-inverse {
            background-color: #3BB9FF;
            border-color: #3BB9FF;
        }
        /* CSS talk bubble */
        .talk-bubble-right {
            margin: 20px;
            display: inline-block;
            position: relative;
            width: 70%;
            height: auto;
            background-color: #3BB9FF;
            color: white;
        }
        .talk-bubble-left {
            margin: 20px;
            margin-left: 65px;
            display: inline-block;
            position: relative;
            width: 70%;
            height: auto;
            background-color: gray;
            color: white;
        }
        .border-right{
            border: 8px solid #3BB9FF;
        }
        .border-left{
            border: 8px solid gray;
        }
        .round{
            border-radius: 30px;
            -webkit-border-radius: 30px;
            -moz-border-radius: 30px;

        }
        /* Right triangle placed top right flush. */
        .tri-right.border-right.right-top:before {
            content: ' ';
            position: absolute;
            width: 0;
            height: 0;
            left: auto;
            right: -40px;
            top: -8px;
            bottom: auto;
            border: 32px solid;
            border-color: #3BB9FF transparent transparent transparent;
        }
        .tri-right.right-top:after{
            content: ' ';
            position: absolute;
            width: 0;
            height: 0;
            left: auto;
            right: -20px;
            top: 0px;
            bottom: auto;
            border: 20px solid;
            border-color: #3BB9FF transparent transparent transparent;
        }
        /* left triangle placed top left flush. */
        .tri-left.border-left.left-top:before {
            content: ' ';
            position: absolute;
            width: 0;
            height: 0;
            righr: auto;
            left: -40px;
            top: -8px;
            bottom: auto;
            border: 32px solid;
            border-color: gray transparent transparent transparent;
        }
        .tri-left.left-top:after{
            content: ' ';
            position: absolute;
            width: 0;
            height: 0;
            right: auto;
            left: -20px;
            top: 0px;
            bottom: auto;
            border: 20px solid;
            border-color: gray transparent transparent transparent;
        }
        /* talk bubble contents */
        .talktext{
            padding: 1em;
            text-align: left;
            line-height: 1.5em;
        }
        .talktext p{
            /* remove webkit p margins */
            -webkit-margin-before: 0em;
            -webkit-margin-after: 0em;
        }

        /* width */
        ::-webkit-scrollbar {
            width: 5px;
        }

        /* Track */
        ::-webkit-scrollbar-track {
            box-shadow: inset 0 0 5px grey; 
            border-radius: 10px;
        }

        /* Handle */
        ::-webkit-scrollbar-thumb {
            background: #3BB9FF; 
            border-radius: 10px;
        }
    </style>
	
	<%

		java.util.List<vo.Message> messageList = (java.util.List<vo.Message>) request.getAttribute("MessageList");
		String architectName = (String) session.getAttribute("ArchitectNameForClient");
        String clientName=(String)session.getAttribute("UserName");
		int pictureIdSelected = Integer.parseInt((String)request.getAttribute("PictureIdSelected"));
	%>	
	
</head>
<body>

       <form name="headerForm" id="headerForm" action="Logout" method="POST" >
        <nav class="navbar navbar-inverse">
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" href="#" style="color: white">Ifc File management system</a>
                </div>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="javascript:void(0)" style="color: white"><span class="glyphicon glyphicon-user"></span> Hello <%= clientName %></a></li>
                    <li><a href="javascript:void(0)" style="color: white" id="logoutId"><span class="glyphicon glyphicon-log-in"></span> Logout</a></li>
                </ul>
            </div>
        </nav>
        </form>
    <form action="GetClientHome" method="POST" >
    <div class="container">

	<%
		java.util.List<vo.IfcFile> ifcFileList = (java.util.List<vo.IfcFile>) session.getAttribute("IfcFileList");
		if ((null != ifcFileList) && (ifcFileList.size() > 0) ) {
	%>
            Choose the file sent to you :  &nbsp;&nbsp; <select id="ifcFile" name="pictureId" onchange="this.form.submit()">
            <%
                for (vo.IfcFile ifcFile: ifcFileList) {
                  String selected="";
                  int pictureIdFromDropdown = ifcFile.getPictureId();
                  if (pictureIdSelected == pictureIdFromDropdown) {
                        selected="selected";
                   }
            %>
                  <option value="<%= ifcFile.getPictureId() %>" <%= selected %>> "<%= ifcFile.getOriginalFileName() %>" </option>
            <%
                }
             %>
            </select>
    <%
        }
    %>
        <p> <p>
        <div class="row">
        <%  if (pictureIdSelected > 0)  {  %>
            <div class="col-md-8">
                <canvas id="viewer" width="500" height="300"></canvas>
            </div>
            <script type="text/javascript">
                var viewer = new xViewer('viewer');
                viewer.load('<%= (String)request.getAttribute("TransformedFileLocation") %>');
                viewer.start();
            </script>
        <% }  %>

            <div class="col-md-4" style="border: 1px solid #3BB9FF;">
                <div class="row" style="height: 482px; overflow-y: auto" id="messageBox">
					<% if (null != messageList) {
						for (vo.Message message : messageList) {
							if (architectName.equals(message.getMessageFrom())) {
						%>								
							<div class="talk-bubble-right tri-right round border-right right-top">
								<div class="talktext">
									<p><%= message.getMessageText() %></p>
								</div>
								<div style="text-align: right; border-top: 1px solid #95B9C7"><%= message.getMessageTime() %></div>
							</div>
                        <% 
							} else {
						%>
							<div class="talk-bubble-left tri-left round border-left left-top">
								<div class="talktext">
									<p><%= message.getMessageText() %></p>
								</div>
								<div style="text-align: left; border-top: 1px solid #C0C0C0"><%= message.getMessageTime() %></div>
							</div>
                    <%
							}
						}
					}	
					%>
                </div>
                <div class="row">
                    <div class="input-group">
                        <textarea class="form-control custom-control" rows="3" placeholder="Type your message" style="resize:none" id="message"></textarea>     
                        <span class="input-group-addon" style="background-color: #3BB9FF; color: white; cursor: pointer" onclick="sendMessageToBackend()">Send</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</form>
</body>

<script>

     function sendMessageToBackend(){
        $.post('SendMessage', { sender: '<%= (String)session.getAttribute("UserName") %>', receiver : '<%= (String)session.getAttribute("ArchitectNameForClient") %>', messageText:  document.getElementById("message").value, pictureId:  document.getElementById("ifcFile").value},
            function(returnedData){
                                console.log("success from ajax call...", returnedData);
                                        var leftBubble = '<div class="talk-bubble-left tri-left round border-left left-top">'+
                                            '<div class="talktext">'+
                                                '<p>'+document.getElementById("message").value+'</p>'+
                                            '</div>'+
                                            '<div style="text-align: right; border-top: 1px solid #C0C0C0">'+new Date().toLocaleTimeString()+' <span>&#10003;</span></div>'+
                                        '</div>';
                                         $("#messageBox").append(leftBubble);
                                         $('#messageBox').animate({scrollTop: document.body.scrollHeight},"fast");
                                         document.getElementById("message").value = "";
        }).fail(function(){
              console.log("error occurred in ajax call");
        });
    }

        $("#logoutId").click(function(e){
            e.preventDefault();//this will prevent the link trying to navigate to another page
            $("#headerForm").submit();
        });

</script>
</html>
