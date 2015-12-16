<?php
require 'connect.php';
$sql = "SELECT * FROM android_account_info WHERE account='". $_REQUEST['account'] . "'";
$result = $conn->query($sql);
if ( $result->num_rows == 1 ){
    $row = $result->fetch_assoc();
}
?>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
    </head>
    <body>
    <center>
        <table border="1"cellpadding="5" cellspacing="0" >
            <tr>
                <td> account </td> <td><?php echo $row['account'] ?></td>
            </tr>
            <tr>
                <td> name </td> <td><?php echo $row['name'] ?></td>
            </tr>
            <tr>
                <td> phoneNumber </td> <td><?php echo $row['phone_number'] ?></td>
            </tr>
        </table>
    </center>
    </body>
</html>
<?php
$conn->close();
?>