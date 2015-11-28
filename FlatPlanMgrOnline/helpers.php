<?php require("session_start.php"); ?>
<?php require("settings.php"); ?>
<?php
function logout(){
  global $FPM_SETTINGS;

  session_unset();
  session_destroy();
  $_SESSION["fpm_logged_in"] = false;
}

function login($username, $password){
  global $FPM_SETTINGS;

  if(!isset($username) || !isset($password)){
    return false;
  }
  $user_list = file_get_contents($FPM_SETTINGS["users_file"]);
  foreach(explode("\n", str_replace("\r\n", "\n", $user_list)) as $user_line){
    $parts = explode(":", $user_line);
    if($username == $parts[0] && crypt($password, $FPM_SETTINGS["login_salt"]) == $parts[1]){
      $_SESSION["fpm_logged_in"] = true;
      $_SESSION["fpm_username"] = $parts[0];
      return true;
    }
  }
  return false;
}

function get_file_name_from_hash($hash){
  global $FPM_SETTINGS;

  $files = scandir($FPM_SETTINGS["root_dir"]);
  foreach($files as $file){
    if(md5($file) == $hash && is_file($FPM_SETTINGS["root_dir"]."/".$file)){
      $plan_file = $FPM_SETTINGS["root_dir"]."/".$file;
      return $plan_file;
    }
  }
  return false;
}

function get_authors_file($plan_file, $check_is_file = true){
  global $FPM_SETTINGS;

  $file_ext = substr($plan_file, strrpos($plan_file, "."));
  $file_name = substr($plan_file, 0, strlen($plan_file) - strlen($file_ext));
  $plan_name = substr($file_name, strrpos($file_name, "/") + 1);
  $authors_file = $file_name.".authors".$file_ext;
  if($check_is_file && !is_file($authors_file)){
    return false;
  }
  return $authors_file;
}
?>
