<?php
   if(count($argv) > 1){
parse_str($argv[1], $_GET);
}
if(count($argv) > 2){
parse_str($argv[2], $_POST);
}
include("index.php");
?>
