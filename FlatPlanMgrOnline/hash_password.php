<?php require("session_start.php"); ?>
<?php include("settings.php"); ?>

<?php
if(isset($_POST["login_submit"]) && isset($_POST["username"]) && isset($_POST["password"])){
  $hash["crypt"] = crypt($_POST["password"], $FPM_SETTINGS["login_salt"]);
  $hash["sha1"] = sha1($_POST["password"]);
  $hash["md5"] = md5($_POST["password"]);
}
?>

<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Generate hash</title>
    <link href="fpm_main.css" rel="stylesheet"></style>
  </head>
  <body>
    <form action="hash_password.php" method="POST">
      <p><input type="text" name="username"/></p>
      <p><input type="password" name="password"/></p>
      <p><input type="submit" name="login_submit" value="Generate Hash"/></p>
    </form>
    <?php
    if(isset($hash)){
      ?>
      <p class="hash_result">crypt: <?=$hash["crypt"]?></p>
      <p class="hash_result">sha1: <?=$hash["sha1"]?></p>
      <p class="hash_result">md5: <?=$hash["md5"]?></p>
      <?php
    }
    ?>
  </body>
</html>
