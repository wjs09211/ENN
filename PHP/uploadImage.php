<?php
require 'connect.php';
?>
<?php
if($_SERVER['REQUEST_METHOD'] == "POST" ){
	$account = $_POST['account'];
    $location = $_POST['location'];
    
	//判斷資料夾是否存在 
	$Upload_folder = "uploads/" . $account . "/";
    if (!is_dir($Upload_folder)) {      //檢察upload資料夾是否存在
        if (!mkdir($Upload_folder))  //不存在的話就創建upload資料夾
            die ("上傳目錄不存在，並且創建失敗");
 
    }
    //存圖片
    //http://sleep-sleepsleep.rhcloud.com/test/uploads/account/date("YmdHis")_image
    $file_path = $Upload_folder . date("YmdHis") . "_" . basename( $_FILES['uploaded_file']['name']);
    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
        echo "success";
    } else{
		echo "fail";
    }

    $image_path = 'http://sleep-sleepsleep.rhcloud.com/test/' . $file_path;
    $stmt = $conn->prepare("INSERT INTO android_accident_record (account, image_path, map_location) VALUES (?, ?, ?);");
    $stmt->bind_param("sss", $account, $image_path, $location);
    $stmt->execute();
}
?>