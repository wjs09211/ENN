<?php
require 'connect.php';
?>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Show</title>
</head>
<body>
	<center>            
        <table border="1" cellpadding="5" cellspacing="0" align="center">
            <tr>
                <th>ID</th><th>account</th><th>image</th><th>location</th><th>reg_time</th><th>經度</th><th>緯度</th>
            </tr>
<?php
	$sql = 'SELECT * FROM android_accident_record ORDER BY ID DESC;';
    $result = $conn->query($sql);
    $num  = $result->num_rows;
    if( $num > 0 ){
        while ( $row = $result->fetch_assoc() ){                    
            echo '<tr>';
            foreach( $row as $name => $value ){
            	if( $name == 'image_path' ){
            		echo '<td><img src="' . $value . '" height="150"></td>';
            	}
            	else{
                	echo '<td>' . urldecode($value) . '</td>';
            	}
            }
            echo '</tr>';
        }
    }
?>
        </table>
    </center> 
</body>
</html>