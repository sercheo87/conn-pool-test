<html>
<body>
	<h2>Connection Pool Test Application</h2>
	<form action="test" method="post">
		JNDI DataSource: 
		<input type="text" name="jndiDS" value="jdbc/CTS_BDD_MF"><br>		
        Query
        <br>
         <input type="radio" name="operation" value="select" checked>SELECT<br>
		<input type="radio" name="operation" value="exec">EXEC<br>
		<br>
		max rows
		<input type="text" name="rows" value="0"><br>		
        <br>
        
        Columns
		<input type="text" name="cols" value="1"><br>		
        <br>
        
        <textarea name="script" rows="20" cols="50"></textarea>
          <input type="submit" value="Exec Script">
          <br>
        RESP:
        <br>  
        <textarea name="scriptResp" rows="20" cols="50"><%= request.getAttribute("resp") %></textarea> 
    </form>
</body>
</html>
