<html>
<body>
	<h2>Connection Pool Test Application</h2>
	<form action="test" method="post">
		JNDI DataSource: 
		<input type="text" name="jndiDS" value="jdbc/CTS_BDD_MF"><br>		
        Threads:
        <input type="text" name="nroThreads" value="5"><br>
        Time before close [seconds]:
        <input type="text" name="tbc" value="0"><br>
        Time after close  [seconds]:
        <input type="text" name="tac" value="0"><br><br>
        Query
        <br>
        <textarea name="script">Enter text here...</textarea>
        <br>
        <input type="submit" value="Run">
        <input type="submit" value="Exec Script">
    </form>
</body>
</html>
