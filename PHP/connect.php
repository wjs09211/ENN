<?php
header("Content-Type:text/html; charset=utf-8");
$servername = "127.4.160.2";
$username = "adminBjUSn1z";
$password = "i37y69aMqf_j";
$dbname = "sleep";
//127.4.160.2:3306 
// Create connection
@$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
$conn->query('SET NAMES "UTF8"');
?>