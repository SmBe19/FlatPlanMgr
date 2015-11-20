<?php include("settings.php"); ?>
<?php
if(isset($_GET["plan"])){
  $files = scandir($FPM_SETTINGS["root_dir"]);
  foreach($files as $file){
    if(md5($file) == $_GET["plan"] && is_file($FPM_SETTINGS["root_dir"]."/".$file)){
      $plan_file = $FPM_SETTINGS["root_dir"]."/".$file;
      break;
    }
   }

   if(isset($plan_file)){
   $plan_raw = file_get_contents($plan_file);
   $lines = split("\n", $plan_raw);
   $plan = [];
   foreach($lines as $line){
   $story = [];
   $parts = split(";", $line);
   
   $story["first"] = $parts[0];
   $story["length"] = $parts[1];
   $story["title"] = $parts[2];
   $story["author"] = $parts[3];
   $story["category"] = $parts[4];
   $story["format"] = $parts[5];
   $story["status"] = $parts[6];

   for($i = $story["first"]; $i < $story["first"] + $story["length"]; $i++){
				  $plan[$i] = $story;
				  }
   }
   }
}
?>

<!DOCTYPE html>
<html lang="en">
  <head>
    <title><?=$FPM_SETTINGS["title"]?></title>
    <link href="fpm_main.css" rel="stylesheet"></style>
  </head>
  <body>
    <h1><?=$FPM_SETTINGS["title"]?></h1>
    <div class="fp_plan">
      <?php
	 if(isset($plan_file)){
	 foreach($plan as $story){
	 ?>
      <div class="fp_page">
	<p class="fp_title"><?=$story["title"]?></p>
	<p class="fp_author"><?=$story["author"]?></p>
	<p class="fp_category"><?=$story["category"]?></p>
	<p class="fp_status fp_status_<?=$story["status"]?>"><?=$story["status"]?></p>
      </div>
      <?php
	 }
	 } else { ?>
      <p>File not found!</p>
      <?php } ?>
    </div>
    <p><?=md5("test_layout.csv");?></p>
  </body>
</html>
