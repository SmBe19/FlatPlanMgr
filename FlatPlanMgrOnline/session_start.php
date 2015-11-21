<?php
session_start();
if(!isset($_SESSION["fpm_logged_in"])){
  $_SESSION["fpm_logged_in"] = false;
}
?>
