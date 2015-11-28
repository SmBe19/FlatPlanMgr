<?php require("session_start.php"); ?>
<?php require("settings.php"); ?>
<?php require("helpers.php"); ?>
<?php
if(isset($_GET["action"])){
  if($_GET["action"] == "logout"){
    logout();
  } else if($_GET["action"] == "login"){
    if(isset($_POST["username"]) && isset($_POST["password"])){
      login($_POST["username"], $_POST["password"]);
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
