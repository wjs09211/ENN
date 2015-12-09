<?php
    require 'connect.php';
?>
<?php
    $sql = $_REQUEST['queryStr'];
    $result = $conn->query($sql);
    while($r = $result->fetch_assoc())
    	$output[] = $r;
	print(json_encode($output));
?>
