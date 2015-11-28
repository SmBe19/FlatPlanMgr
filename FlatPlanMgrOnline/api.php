<?php require("session_start.php"); ?>
<?php require("settings.php"); ?>
<?php require("helpers.php"); ?>
<?php
if($_REQUEST["v"] == 1){
  switch ($_REQUEST["action"]) {

    case "logout":
      logout();
      header("HTTP/1.1 200 OK");
      break;

    case "login":
      if(isset($_REQUEST["username"]) && isset($_REQUEST["password"])){
        if(login($_REQUEST["username"], $_REQUEST["password"])){
          header("HTTP/1.1 200 OK");
        } else {
          header("HTTP/1.1 401 Invalid login credentials");
          echo "401 Invalid login credentials";
        }
      } else {
        header("HTTP/1.1 400 Bad Request (missing arguments)");
        echo "400 Bad Request (missing arguments)";
      }
      break;

    case "download_plan":
      if($_SESSION["fpm_logged_in"] !== true){
        break;
      }
      if(isset($_REQUEST["plan"])){
        $plan_file = get_file_name_from_hash($_REQUEST["plan"]);
        if($plan_file === false){
          header("HTTP/1.1 404 Not Found");
          echo "404 not found";
          break;
        }
        header("Content-Type: text/plain");
        readfile($plan_file);
        echo "---"."\r\n";
        $authors_file = get_authors_file($plan_file);
        if($authors_file !== false){
          readfile($authors_file);
        }
      } else {
        header("HTTP/1.1 400 Bad Request (missing arguments)");
        echo "400 Bad Request (missing arguments)";
      }
      break;

    case "upload_plan":
      if($_SESSION["fpm_logged_in"] !== true){
        break;
      }
      if(isset($_REQUEST["plan"]) && isset($_FILES["fileUpload"])){
        $plan_raw = file_get_contents($_FILES["fileUpload"]["tmp_name"]);
        unlink($_FILES["fileUpload"]["tmp_name"]);

        $plan_raw = str_replace("\r\n", "\n", $plan_raw);
        $parts = explode("---\n", $plan_raw);
        $plan_file = $_REQUEST["plan"].".csv";

        if(pathinfo($plan_file, PATHINFO_BASENAME) !== $plan_file){
          header("HTTP/1.1 400 Bad Request (invalid argument value for `plan`)");
          echo "400 Bad Request (invalid argument value for `plan`)";
          break;
        }

        $plan_base_file = $plan_file;
        $plan_file = $FPM_SETTINGS["root_dir"]."/".$plan_base_file;
        $authors_file = get_authors_file($plan_file, false);

        file_put_contents($plan_file, $parts[0]);
        if(strlen($parts[1]) > 0){
          file_put_contents($authors_file, $parts[1]);
        }

        header("Content-Type: text/plain");
        echo md5($plan_base_file);
      } else {
        header("HTTP/1.1 400 Bad Request (missing arguments)");
        echo "400 Bad Request (missing arguments)";
      }
      break;

    default:
      header("HTTP/1.1 400 Bad Request (unknown action)");
      echo  "400 Bad Request (unknown action)";
      break;
  }
}
?>
