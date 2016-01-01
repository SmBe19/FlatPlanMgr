<?php require("session_start.php"); ?>
<?php require("settings.php"); ?>
<?php require("helpers.php"); ?>
<?php
if($_SESSION["fpm_logged_in"] === true){
  $files = scandir($FPM_SETTINGS["root_dir"]);
  $file_list = [];
  foreach($files as $file){
    if(is_file($FPM_SETTINGS["root_dir"]."/".$file)){
      array_push($file_list, $file);
    }
  }
}
?>

<!DOCTYPE html>
<html lang="en">
  <head>
    <title><?=$FPM_SETTINGS["admin_title"]?></title>
    <link href="fpm_main.css" rel="stylesheet"></style>
  </head>
  <body>
    <?php
    if($_SESSION["fpm_logged_in"] === true){
      ?>
      <h1 class="admin_title"><?=$FPM_SETTINGS["admin_title"]?></h1>
      <p><a href="login.php?action=logout">Logout</a></p>
      <div class="admin_file_list_container">
        <h2>List of files</h2>
        <ul class="admin_file_list">
          <?php
          foreach($file_list as $file){
            ?>
            <li class="admin_file_item"><?=$file?>: <a href="index.php?plan=<?=get_hash_from_file_name($file)?>"><?=get_hash_from_file_name($file)?></a></li>
            <?php
          }
          ?>
        </ul>
      </div>
      <?php
    } else {
      ?>
      <h1 class="admin_not_logged_in">Not logged in!</h1>
      <p><a href="login.php">Login</a></p>
      <?php
    }
    ?>
  </body>
</html>
