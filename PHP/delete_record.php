<?php
require 'connect.php';
?>
<?php

    //$stmt = $conn->prepare("DELETE FROM android_accident_record WHERE id=?");
	$stmt = $conn->prepare("UPDATE android_accident_record SET visible=0 WHERE id=?;");
    $stmt->bind_param("i", $_REQUEST[id]);                     
    $stmt->execute();
    header('Location: Show.php');
?>
<?php
$conn->close();
?>