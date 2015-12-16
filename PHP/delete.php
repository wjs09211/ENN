<?php
require 'connect.php';
$sql = "SELECT * FROM data WHERE id=". $_REQUEST['id'];
$result = $conn->query($sql) or die("error");
if ( $result->num_rows == 1 ){
    $row = $result->fetch_assoc() or die("error");
}
?>
<!DOCTYPE html>
<html>
    <head>
        <title>delete</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    </head>
    <body>
    <center>
        <?php if($_SERVER['REQUEST_METHOD'] != "POST" ){                
        ?>
        <table border="1"cellpadding="5" cellspacing="0" >
            <tr>
                <td> id </td> <td><?php echo $row['id'] ?></td>
            </tr>
            <tr>
                <td> password </td> <td><?php echo $row['password'] ?></td>
            </tr>
            <tr>
                <td> email </td> <td><?php echo $row['email'] ?></td>
            </tr>
            <tr>
                <td> phone </td> <td><?php echo $row['phone'] ?></td>
            </tr>
        </table>
        <form action="<?php echo "delete.php?id=".$_REQUEST[id] ?>" method="post" name="form1">
            <input type="submit" value="刪除" />
            <input type="button" value="取消" onclick="location.href = 'index.php'"/>
        </form>
        <?php }else{
                  $stmt = $conn->prepare("DELETE FROM data WHERE id=?");
                  $stmt->bind_param("i", $_REQUEST[id]);                     
                  $stmt->execute();
                  header('Location: index.php');
              } 
        ?>
    </center>
    </body>
</html>
<?php
$conn->close();
?>