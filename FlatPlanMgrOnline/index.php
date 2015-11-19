<?php include("settings.php"); ?>
<?php
if(isset($_GET["plan"])){
  $files = scandir($FPM_SETTINGS["root_dir"]);
  foreach($files as $file){
    if(md5($file) == $_GET["plan"] && is_file($FPM_SETTINGS["root_dir"]."/".$file)){
      $plan_file = $file;
      break;
    }
  }
}
?>

<!DOCTYPE html>
<html lang="en">
  <head>
  </head>
  <body>
    <h1><?=$FPM_SETTINGS["title"]?></h1>
    <p><?=isset($plan_file) ? $plan_file : "File not found!"?></p>
    <p><?=md5("test_layout.csv");?></p>
  </body>
</html>
