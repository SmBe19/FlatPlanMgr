<?php require("session_start.php"); ?>
<?php require("settings.php"); ?>
<?php require("helpers.php"); ?>
<?php
if(isset($_REQUEST["v"]) && $_REQUEST["v"] == 1){
  if(!isset($_REQUEST["action"])){
    $_REQUEST["action"] = "";
  }
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
        header("HTTP/1.1 401 Missing login");
        echo "401 Missing login";
        break;
      }
      if(isset($_REQUEST["plan"])){
        $plan_file = get_file_name_from_hash($_REQUEST["plan"]);
        if($plan_file === false){
          header("HTTP/1.1 404 Not Found");
          echo "404 not found";
          break;
        }
        header("Content-Type: text/plain; charset=utf-8");
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
        header("HTTP/1.1 401 Missing login");
        echo "401 Missing login";
        break;
      }
      if(isset($_REQUEST["plan"]) && isset($_FILES["fileUploadPlan"])){
        $plan_file = $_REQUEST["plan"];

        if(pathinfo($plan_file, PATHINFO_BASENAME) !== $plan_file){
          header("HTTP/1.1 400 Bad Request (invalid argument value for `plan`)");
          echo "400 Bad Request (invalid argument value for `plan`)";
          break;
        }

        $plan_base_file = $plan_file;
        $plan_file = $FPM_SETTINGS["root_dir"]."/".$plan_base_file;
        $authors_file = get_authors_file($plan_file, false);

        move_uploaded_file($_FILES["fileUploadPlan"]["tmp_name"], $plan_file);
        if(isset($_FILES["fileUploadAuthors"]["tmp_name"])){
          move_uploaded_file($_FILES["fileUploadAuthors"]["tmp_name"], $authors_file);
        }

        header("Content-Type: text/plain");
        echo get_hash_from_file_name($plan_base_file);
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
