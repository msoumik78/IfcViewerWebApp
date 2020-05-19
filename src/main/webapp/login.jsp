<!DOCTYPE html>
<html lang="en">

<head>
    <title>Login</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="css/style.css">
    <link rel="icon" href="images/icon.png">

</head>

<body>
    <div class="container">
        <div class="row" style="padding-top: 100px">
            <form action="Authenticate" method="post">

                <div class="col-md-3" id="formData" style="border-radius: 15%; background-color: white; padding-bottom:20px; padding-top:60px; box-shadow: 5px 10px 21px 5px #888888;">
                    <%
                        String error = (String)request.getAttribute("AuthenticationError");
                        if ("Exists".equals(error)) {
                    %>
                        <div class="row" style="text-align:center">
                            <font color="red">Some problems with your credentials!!</font>
                        </div>
                        <%
                        }
                    %>
                            <div class="row" style="text-align: center">
                                <img src="images/logo.png">
                            </div>
                            <div class="row" style="padding-top: 40px">
                                <input type="text" class="form-control" placeholder="Enter your userId" required="required" name="<%= servlet.login.LoginConstants.LoginPageUsername %>" />
                            </div>
                            <div class="row" style="padding-top: 20px">
                                <input type="password" class="form-control" placeholder="Enter your password" required="required" name="<%= servlet.login.LoginConstants.LoginPagePassword %>" />
                            </div>
                            <div class="row" style="padding-top: 20px; text-align: center">
                                <input type="submit" value="Login" class="btn-default" />
                            </div>
                </div>
            </form>
        </div>
    </div>
</body>

</html>