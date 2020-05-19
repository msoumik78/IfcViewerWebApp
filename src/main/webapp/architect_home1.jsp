<!DOCTYPE html>
<html lang="en">

<head>
    <title>Welcome to your Ifc file management system</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
    <script src="js/xbim-viewer.debug.bundle.js"></script>
    <link rel="stylesheet" href="css/style.css">
    <link rel="icon" href="images/icon.png">

    <script type="text/javascript">
        var fileName = undefined;
        $(document).ready(function() {
            <% String stateOfSecondTab = (String)request.getAttribute("Collapse2");
          if ("true".equals(stateOfSecondTab)) {  %>
            $("#collapseLink2").trigger("click");
            $("#viewer").attr("height", "300");
            $("#viewer").attr("width", "300");

            var viewer = new xViewer('viewer');
            viewer.load('<%= (String)request.getAttribute("TransformedFileLocation") %>');
            viewer.start();
            <%
            }
           %>

            $("#ifcFileUploadBtn").click(function(event) {

                //stop submit the form, we will post it manually.
                event.preventDefault();

                console.log("here 1 after upload button is clicked");
                /*
                 *Transfer Validation Start
                 */
                if ($("#inputState").val() == "Choose Customer" || $("#inputState").val().length == 0) {
                    $("#validationMessage").html("Please Choose Customer");
                    return;
                }
                console.log("here 2 after upload button is clicked");
                if (typeof(fileName) != "undefined") {
                    let fileExtention = fileName.split(".");
                    if (fileExtention[fileExtention.length - 1].toLowerCase() != "ifc") {
                        $("#validationMessage").html("Please Choose .ifc File");
                        return;
                    }
                } else {
                    $("#validationMessage").html("Please Choose .ifc File");
                    return;
                }

                console.log("here 3 after upload button is clicked");
                $("#validationMessage").html("");
                /*
                 *Transfer Validation End
                 */

                $("#formData").hide();
                $("#transferPercentage").show();

                console.log("here 4 after upload button is clicked");
                // Get form
                var form = $('#ifcFileUploadFrm')[0];

                // Create an FormData object
                var data = new FormData(form);

                // If you want to add an extra field for the FormData
                data.append("CustomField", "This is some extra data, testing");

                console.log("here 5 after upload button is clicked");
                // disabled the submit button
                $("#ifcFileUploadBtn").prop("disabled", true);

                console.log("here 6 after upload button is clicked");

                $.ajax({
                    type: "POST",
                    enctype: 'multipart/form-data',
                    url: "UploadIfcFile",
                    data: data,
                    processData: false,
                    contentType: false,
                    cache: false,
                    timeout: 600000,

                    success: function(data) {
                        console.log("here 8 after upload button is clicked");

                        $("#uploadResult").text(data);
                        console.log("SUCCESS : ", data);
                        $("#ifcFileUploadBtn").prop("disabled", false);

                        $("#transferPercentage").hide();
                        $("#doneBox").show();

                    },
                    error: function(e) {
                        console.log("here 9 after upload button is clicked");

                        $("#uploadResult").text(e.responseText);
                        console.log("ERROR : ", e);
                        $("#ifcFileUploadBtn").prop("disabled", false);

                    }
                });

            });

        });

        function goback() {
            $("#displayFileName").html("");
            document.getElementById("messageDetails").value = "";
            $("#formData").show();
            $("#doneBox").hide();
        }

        function fileSelect(me) {
            let filePath = me.value.split("\\");
            fileName = filePath[filePath.length - 1];
            $("#displayFileName").html(fileName);
        }
    </script>

</head>

<%

		java.util.List<String> customerList = (java.util.List<String>) session.getAttribute("ClientList");
	    java.util.List<vo.IfcFile> ifcFileList = (java.util.List<vo.IfcFile>) request.getAttribute("IfcFileList");
	    java.util.List<vo.Message> messageList = (java.util.List<vo.Message>) request.getAttribute("MessageList");
	    String architectName=(String)session.getAttribute("UserName");
	    String selectedCustomer =(String)request.getAttribute("CustomerSelected");
	    String selectedPicture =(String)request.getAttribute("PictureIdSelected");
	    int pictureIdSelected =-1;
	    if (null != selectedPicture) {
	        pictureIdSelected = Integer.parseInt(selectedPicture);
	    }

%>

    <body>
        <form name="headerForm" id="headerForm" action="Logout" method="POST">
            <nav class="navbar navbar-inverse">
                <div class="container-fluid">
                    <div class="navbar-header">
                        <a class="navbar-brand" href="#" style="color: white">Ifc File management system</a>
                    </div>
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="javascript:void(0)" style="color: white"><span class="glyphicon glyphicon-user"></span> Hello <%= architectName %></a></li>
                        <li><a href="javascript:void(0)" style="color: white" id="logoutId"><span class="glyphicon glyphicon-log-in"></span> Logout</a></li>
                    </ul>
                </div>
            </nav>
        </form>
        <div class="container">
            <div class="panel-group" id="accordion">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" data-parent="#accordion" href="#collapse1">Want to upload a new Ifc file for your customer ? Click here !</a>
                        </h4>
                    </div>
                    <div id="collapse1" class="panel-collapse collapse">
                        <div class="panel-body">
                            <div class="col-md-3" id="formData" style="border-radius: 15%; background-color: white; padding-bottom:20px; box-shadow: 5px 10px 21px 5px #888888;">
                                <form name="ifcFileUploadFrm" id="ifcFileUploadFrm" action="UploadIfcFile" enctype="multipart/form-data" method="POST" style="padding:10px">
                                    <div class="row" style="padding-top: 65px; padding-left: 10px;">
                                        <div class="col-md-3">
                                            <div class="image-upload">
                                                <label for="file-input">
                                                <img src="https://goo.gl/pB9rpQ"/>
                                            </label>
                                                <input id="file-input" type="file" name="ifcFileUpload" onchange="fileSelect(this)" />
                                            </div>
                                        </div>
                                        <div class="col-md-9" style="padding-left: 25px">
                                            <div class="row" style="font-weight: bold; font-size: 17px; padding-top:10px">Add your design files</div>
                                            <div class="row" style="font-weight: bold; color: #488AC7; font-size: 9px; padding-top:10px" id="displayFileName"></div>
                                        </div>
                                    </div>

                                    <div class="row" style="padding-top: 40px; padding-left:20px; padding-right:20px;">
                                        <select class="form-control" name="customerName" id="inputState">
                                        <option selected>Choose Customer</option>
                                            <%
                                                if (null != customerList) {
                                                    for (String customer : customerList) {   %>
                                                        <option value="<%= customer %>"><%= customer %></option>
                                            <%	    }
                                                }
                                            %>
                                    </select>
                                    </div>

                                    <div class="row" style="padding-top: 20px">
                                        <!--input type="text" class="form-control" placeholder="Message" style="height:50px; box-shadow: 0px 0px 0px -7px #ebebeb, 0px 10px 0px -7px #ebebeb;"/-->
                                        <textarea placeholder="Message" name="messageDetails" id="messageDetails"></textarea>
                                    </div>
                                    <div class="row" style="padding-top: 20px; text-align: center">
                                        <input type="submit" id="ifcFileUploadBtn" value="Transfer" class="btn-default" />
                                    </div>
                                    <div class="row" style="padding-top: 5px; text-align: center; color: red" id="validationMessage"></div>
                                </form>
                            </div>

                            <div class="col-md-3" id="transferPercentage" style="border-radius: 15%; background-color: white; padding-bottom:20px; display: none; box-shadow: 5px 10px 21px 5px #888888;">
                                <div class="containerBox">
                                    <img src="images/waiting.gif" style="width: 100%">
                                </div>
                                <!--<div class="row" style="text-align: center">
                                    <input type="button" class="btn-default" value="Cancel" style="background-color: white; border: 2px solid #3BB9FF; color: #3BB9FF" />
                                </div>-->
                            </div>

                            <div class="col-md-3" id="doneBox" style="border-radius: 15%; background-color: white; padding-bottom:20px; display: none; box-shadow: 5px 10px 21px 5px #888888;">
                                <div class="row" style="text-align: center; padding-top:15px">
                                    <img src="images/done.png" height="130" />
                                </div>
                                <div class="row" style="text-align: center; font-weight: bold; font-size: 17px">You're done!</div>
                                <div class="row" style="text-align: center; font-size: 14px; padding-top:150px"></div>
                                <div class="row" style="text-align: center;">
                                    <input type="button" class="btn-default" value="Go Back" onclick="goback()" style="background-color: white; border: 2px solid #3BB9FF; color: #3BB9FF" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" data-parent="#accordion" href="#collapse2" id="collapseLink2">Review already sent files and the corresponding messages with your customers? Click here!</a>
                        </h4>
                    </div>
                    <div id="collapse2" class="panel-collapse collapse">
                        <div class="panel-body">
                            <div class="col-md-4" style="border-radius: 4%; background-color: white; padding:27px; box-shadow: 5px 10px 21px 5px #888888;">
                                <form name="getRelevantDataFrm" id="getRelevantDataFrm" action="GetArchitectHome" method="POST">
                                    <div class="row">
                                        <%
                                    if (null != customerList) {
                                %>
                                           Choose your customer :  &nbsp;&nbsp;  <select class="form-control" onchange="chooseCustomer()" name="chosenClient">
                                                        <option selected>Choose Customer</option>
                                                        <%
                                                            for (String customer : customerList) {
                                                                    String selected="";
                                                                    if ((null != selectedCustomer) && (selectedCustomer.equals(customer)) ) {
                                                                        selected="selected";
                                                                    }
                                                            %>
                                                                    <option value="<%= customer %>" <%= selected %>><%= customer %></option>
                                                        <%
                                                            }
                                                        %>
                                                    </select>
                                            <%
                                    } else {
                                %>
                                                Sorry No customer added yet !
                                                <%
                                    }
                                %>
                                    </div>



                                    <div class="row" style="padding-top:15px;">
                                        <div class="col-md-12">
                                            <%
                                    if (null != ifcFileList) {
                                %>

                                            Choose the file you sent to your customer:  &nbsp;&nbsp;     <select class="form-control" onchange="chooseImage()" id="imageListView" name="chosenPicture">
                                            <%
                                                for (vo.IfcFile ifcFile : ifcFileList) {
                                                        String selected="";
                                                        if ((null != selectedPicture) && (selectedPicture.equals(String.valueOf(ifcFile.getPictureId()))) ) {
                                                            selected="selected";
                                                        }
                                                %>
                                                        <option value="<%= ifcFile.getPictureId() %>" <%= selected %>><%= ifcFile.getOriginalFileName() %></option>

                                            <%
                                                }
                                            %>
                                        </select>
                                                <%
                                                }
                                            %>

                                        </div>


                                    </div>
                                    <div class="row" style="padding-top:15px;">
                                        <div class="col-md-12">
                                            <canvas id="viewer" height="200" width="200"></canvas>
                                        </div>
                                    </div>

                                    <input type="hidden" name="pageLoadReason" id="pageLoadReason">
                                </form>
                            </div>

                            <div class="col-md-2"></div>

                            <div class="col-md-4" style="border-radius: 4%; background-color: white; padding:27px; box-shadow: 5px 10px 21px 5px #888888;">
                                <% if (null != messageList) { %>
                                    <div class="row" style="height: 400px; overflow-y: auto" id="messageBox">
                                        <%    for (vo.Message message : messageList) {
                                            if (!(architectName.equals(message.getMessageFrom()))) {
                                        %>
                                            <div class="talk-bubble-right tri-right round border-right right-top">
                                                <div class="talktext">
                                                    <p>
                                                        <%= message.getMessageText() %>
                                                    </p>
                                                </div>
                                                <div style="text-align: right; border-top: 1px solid #95B9C7"><%= message.getMessageTime() %></div>
                                            </div>
                                            <%
                                            } else {
                                        %>
                                                <div class="talk-bubble-left tri-left round border-left left-top">
                                                    <div class="talktext">
                                                        <p>
                                                            <%= message.getMessageText() %>
                                                        </p>
                                                    </div>
                                                    <div style="text-align: left; border-top: 1px solid #C0C0C0"><%= message.getMessageTime() %></div>
                                                </div>
                                                <%
                                            }
                                        }
                                    %>
                                    </div>
                                    <div class="row">
                                        <div class="input-group">
                                            <textarea class="form-control custom-control" rows="3" placeholder="Type your message" id="message" style="width:97%"></textarea>
                                            <span class="input-group-addon" style="background-color: #3BB9FF; color: white; cursor: pointer" onclick="sendMessageToBackend()">Send</span>
                                        </div>
                                    </div>
                                    <%  }   %>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" data-parent="#accordion" href="#collapse3" id="collapseLink2">Want to add new customers ? Click here!</a>
                        </h4>
                    </div>
                    <div id="collapse3" class="panel-collapse collapse">
                          <span class="input-group-addon" style="background-color: #3BB9FF; color: white; cursor: pointer" onclick="checkCustomerFromBackend()">Send</span>
                    </div>
               </div>
            </div>
        </div>
    </body>

    <script>
        function transferIFC() {
            $("#formData").hide();
            $("#transferPercentage").show();
        }
    </script>

    <script>
        function chooseCustomer() {
            $("#pageLoadReason").val("changeCustomer");
            $("#getRelevantDataFrm").submit();
        }

        function chooseImage() {
            $("#pageLoadReason").val("changePicture");
            $("#getRelevantDataFrm").submit();
        }

        $("#logoutId").click(function(e) {
            e.preventDefault(); //this will prevent the link trying to navigate to another page
            $("#headerForm").submit();
        });
    </script>

    <script>
        function sendMessageToBackend() {

            $.post('SendMessage', {
                    sender: '<%= architectName %>',
                    receiver: '<%= selectedCustomer %>',
                    messageText: document.getElementById("message").value,
                    pictureId: document.getElementById("imageListView").value
                },
                function(returnedData) {
                    console.log("success from ajax call...", returnedData);
                    var leftBubble = '<div class="talk-bubble-left tri-left round border-left left-top">' +
                        '<div class="talktext">' +
                        '<p>' + document.getElementById("message").value + '</p>' +
                        '</div>' +
                        '<div style="text-align: right; border-top: 1px solid #C0C0C0">' + new Date().toLocaleTimeString() + ' <span>&#10003;</span></div>' +
                        '</div>';
                    $("#messageBox").append(leftBubble);
                     $('#messageBox').animate({scrollTop: document.body.scrollHeight},"fast");
                     document.getElementById("message").value = "";
                }).fail(function() {
                console.log("error occurred in ajax call");
            });

        }

        function checkCustomerFromBackend() {
            $.post('CustomerCRUD', {
                    customerNameToBeChecked: 'testClient14'
                },
                function(returnedData) {
                    console.log("success from ajax call...", returnedData);
                }).fail(function() {
                console.log("error occurred in ajax call");
            });
        }

    </script>

</html>