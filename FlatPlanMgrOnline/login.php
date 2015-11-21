<?php session_start(); ?>
<?php include("settings.php"); ?>

<?php
if(isset($_GET["action"])){
  if($_GET["action"] == "logout"){
    session_unset();
    session_destroy();
  } else if($_GET["action"] == "login"){
    if(isset($_POST["login_submit"]) && isset($_POST["username"]) && isset($_POST["password"])){
      $user_list = file_get_contents($FPM_SETTINGS["users_file"]);
      foreach(explode("\n", str_replace("\r\n", "\n", $user_list)) as $user_line){
        $parts = explode(":", $user_line);
        if($_POST["username"] == $parts[0] && crypt($_POST["password"], $FPM_SETTINGS["login_salt"]) == $parts[1]){
          $_SESSION["fpm_logged_in"] = true;
          $_SESSION["fpm_username"] = $parts[0];
          break;
        }
      }
    }
  }
}
?>

<!DOCTYPE html>
<html lang="en">
  <head>
    <title><?=$FPM_SETTINGS["login_title"]?></title>
    <link href="fpm_main.css" rel="stylesheet"></style>
  </head>
  <body>
    <h1 class="login_title"><?=$FPM_SETTINGS["login_title"]?></h1>
    <?php
    if($_SESSION["fpm_logged_in"] === true){
      ?>
      <ul class="login_link_list">
        <li class="login_link_item"><a href="admin.php">Admin Panel</a></li>
        <li class="login_link_item"><a href="login.php?action=logout">Logout</a></li>
      </ul>
      <?php
    } else {
      ?>
      <form action="login.php?action=login" method="POST" class="login_form">
        <p><input type="text" name="username" placeholder="name"/></p>
        <p><input type="password" name="password" placeholder="password"/></p>
        <p><input type="submit" name="login_submit" value="Login"/></p>
      </form>
      <?php
    }?>
  </body>
</html>
