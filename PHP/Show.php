<?php
require 'connect.php';
?>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Show</title>

    <link href="bootstrap-3.3.5-dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="css/starter-template.css" rel="stylesheet">

</head>
<body>
	<center>
    <div class="wrapper">          
        <table class="table table-striped" border="1" cellpadding="5" cellspacing="0" align="center">
            <tr>
                <th>account</th><th>image</th><th>location</th><th>通報時間</th><th>刪除</th><th>聯絡人資料</th>
            </tr>
<?php
	$sql = 'SELECT * FROM android_accident_record ORDER BY ID DESC;';
    $result = $conn->query($sql);
    $num  = $result->num_rows;
    if( $num > 0 ){
        while ( $row = $result->fetch_assoc() ){
        	if( $row['visible'] == 1 ){
            echo '<tr>';
            	foreach( $row as $name => $value ){
            		if( $name == 'image_path' ){
            			echo '<td><img src="' . $value . '" height="150"></td>';
            		}
            		else if ($name == 'account' || $name == 'map_location' || $name == 'date'){
            	    	echo '<td style="padding-left: 5px;padding-right: 5px;">' . urldecode($value) . '</td>';
            		}
            	}
            	echo "<td> <input type='button' onclick=location.href='delete_record.php?id=".$row['id'] ."' value='delete'> </td>";
            	echo "<td> <input type='button' onclick=location.href='ShowUser.php?account=".$row['account'] ."' value='search'> </td>";
            	echo '</tr>';
        	}

        }
    }
?>
        </table>
        </div>  
    </center> 
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="bootstrap-3.3.5-dist/js/bootstrap.min.js"></script>
</body>
</html>