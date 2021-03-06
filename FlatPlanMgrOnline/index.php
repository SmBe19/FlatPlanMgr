<?php require("session_start.php"); ?>
<?php require("settings.php"); ?>
<?php require("helpers.php"); ?>
<?php
$plan_file = false;
$authors_file = false;
if(isset($_GET["plan"])){
  $plan_file = get_file_name_from_hash($_GET["plan"]);
  if($plan_file !== false){
    $authors_file = get_authors_file($plan_file);
  }

  $plan = [];
  if($plan_file !== false){
    $plan_name = pathinfo($plan_file, PATHINFO_FILENAME);
    $plan_raw = file_get_contents($plan_file);
    $lines = explode("\n", str_replace("\r\n", "\n", $plan_raw));
    foreach($lines as $line){
      $story = [];
      $parts = explode(";", $line);

      if(count($parts) < 7){
        continue;
      }

      $story["first"] = $parts[0];
      $story["length"] = $parts[1];
      $story["title"] = $parts[2];
      $story["author"] = $parts[3];
      $story["category"] = $parts[4];
      $story["format"] = $parts[5];
      $story["status"] = $parts[6];

      for($i = $story["first"]; $i < $story["first"] + $story["length"]; $i++){
        if(isset($plan[$i])){
          if(isset($_GET["highlight_author"])){
            if($_GET["highlight_author"] === $plan[$i]["author"]){
              $story = $plan[$i];
            }
          } else {
            if($plan[$i]["status"] < $story["status"]){
              $story = $plan[$i];
            }
            $story["title"] = $story["title"]." / ".$plan[$i]["title"];
          }
        }
        $plan[$i] = $story;
      }
    }
  }

  $authors = [];
  if($authors_file !== false){
    $authors_raw = file_get_contents($authors_file);
    $lines = explode("\n", str_replace("\r\n", "\n", $authors_raw));
    foreach($lines as $line){
      $author = [];
      $parts = explode(";", $line);

      if(count($parts) < 4){
        continue;
      }

      $author["firstname"] = $parts[0];
      $author["lastname"] = $parts[1];
      $author["fullname"] = $parts[0].(strlen($parts[0]) > 0 ? " " : "").$parts[1];
      $author["role"] = $parts[2];
      $author["email"] = $parts[3];

      $authors[$author["fullname"]] = $author;
    }
  }

  foreach($plan as $page){
    if(!isset($authors[$page["author"]])){
      $author = [];
      if(strpos($page["author"], " ") === false){
        $author["lastname"] = $page["author"];
        $author["firstname"] = "";
      } else {
        $author["lastname"] = substr($page["author"], strpos($page["author"], " ") + 1);
        $author["firstname"] = substr($page["author"], 0, strlen($page["author"]) - strlen($author["lastname"]) - 1);
      }
      $author["fullname"] = $page["author"];
      $author["role"] = "";
      $author["email"] = "";
      $authors[$author["fullname"]] = $author;
    }
  }

  $status_list = ["", "missing", "Received", "Received (no autolayout)", "layout in progress", "layout finished"];
  unset($status_list[0]);
}
?>

<!DOCTYPE html>
<html lang="en">
  <head>
    <title><?=$FPM_SETTINGS["title"]?></title>
    <link href="fpm_main.css" rel="stylesheet"></style>
  </head>
  <body>
    <h1 class="fp_index_title"><?=$FPM_SETTINGS["title"]?></h1>
    <?php
    if($plan_file !== false){
      ?>
      <div class="fp_plan">
        <h2 class="fp_plan_name"><?=$plan_name?></h2>
        <?php
        foreach($plan as $a_page => $story){
          $author_highlight = (isset ($_GET["highlight_author"]) && $_GET["highlight_author"] === $authors[$story["author"]]["fullname"]);
          ?>
          <div class="fp_page fp_page_status_<?=$story["status"]?> fp_page_<?=($a_page % 2 == 1) ? "right" : "left"?><?=$author_highlight ? " fp_page_author_highlight" : ""?>">
            <p class="fp_page_number"><?=$a_page?></p>
            <p class="fp_category"><?=$story["category"]?></p>
            <p class="fp_title"><?=$story["title"]?></p>
            <p class="fp_author<?=$author_highlight ? " fp_author_highlight" : ""?>">
              <?php
              if ($_SESSION["fpm_logged_in"] === true && strlen($authors[$story["author"]]["email"]) > 0){
                ?>
                <a href="mailto:<?=$authors[$story["author"]]["email"]?>"><?=$story["author"];?></a>
                <?php
              } else {
                echo $story["author"];
              }
              ?>
            </p>
            <p class="fp_status fp_status_<?=$story["status"]?>"><?=$story["status"]?></p>
          </div>
          <?php
        }
        ?>
      </div>
      <div class="fp_authors_list">
        <h2>Authors</h2>
        <?php
        if(isset($_GET["highlight_author"])){
          ?>
          <p class="fp_unhighlight_link"><a href="index.php?plan=<?=htmlspecialchars($_GET["plan"])?>">remove highlight</a></p>
          <?php
        }
        ?>
        <table class="fp_authors_table">
          <tr class="fp_authors_table_header">
            <th>H</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Role</th>
            <?php
            if($_SESSION["fpm_logged_in"] === true){
              ?>
              <th>E-Mail</th>
              <?php
            }
            ?>
          </tr>
          <tbody>
            <?php
            foreach($authors as $author){
              ?>
              <tr>
                <td><a href="index.php?plan=<?=htmlspecialchars($_GET["plan"])?>&amp;highlight_author=<?=$author["fullname"]?>">X</a></td>
                <td><?=$author["firstname"]?></td>
                <td><?=$author["lastname"]?></td>
                <td><?=$author["role"]?></td>
                <?php
                if($_SESSION["fpm_logged_in"] === true){
                  ?>
                  <td><a href="mailto:<?=$author["email"]?>"><?=$author["email"]?></a></td>
                  <?php
                }
                ?>
              </tr>
              <?php
            }
            ?>
          </tbody>
        </table>
      </div>
      <div class="fp_legend">
        <h2>Legend</h2>
        <?php
        foreach($status_list as $key => $name){
          ?>
          <div class="fp_legend_entry fp_page_status_<?=$key?>">
            <p class="fp_legend_name"><?=$name?></p>
          </div>
          <?php
        }
        ?>
      </div>
      <?php
    } else {
      ?>
      <p class="file_not_found">File not found!</p>
      <?php
    }
    ?>
  </body>
</html>
