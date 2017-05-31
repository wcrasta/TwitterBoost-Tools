<!DOCTYPE html>
<%@page import="com.sl.pojo.UserPojo"%>
<html>
<head>
<link href="social-buttons.css" rel="stylesheet"/>
<link href="bootstrap.min.css" rel="stylesheet">
<style>
.demoDiv {
  margin:auto;
  width:500px;
}
</style>
<meta charset="ISO-8859-1">
<title>Post Status With Twitter Using Java</title>
</head>
<body>
<div style="width:730px;margin:auto">
<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
<!-- demos -->
<ins class="adsbygoogle"
     style="display:inline-block;width:728px;height:90px"
     data-ad-client="ca-pub-4537529140156254"
     data-ad-slot="4444050329"></ins>
<script>
(adsbygoogle = window.adsbygoogle || []).push({});
</script>
</div>
<br/>
<div class="demoDiv">
<%
UserPojo user = (UserPojo)request.getAttribute("user");
%>
<b>User ID</b> : <span style="font-weight:bold;color:red"><%=user.getUser_id() %></span>  (Only one user id is there for one twitter account)<br/>
<b>Twitter User ID</b> : <%=user.getTwitter_user_id() %> <br/>
<b>Screen Name</b> : <%=user.getTwitter_screen_name() %> <br/>
<b><a href="logout">Logout</a></b><br/>
    <form action="postStatus" method="post">
	  <div class="form-group">
	    <label for="exampleInputEmail1">Enter Status Here</label>
	    <textarea name="text" class="form-control" rows="3">Hi @SodhanaLibrary, This is sample status from Java</textarea>   
	  </div>
	  <button type="submit" class="btn btn-default">Submit</button>
	</form>
</div>
</body>
</html>