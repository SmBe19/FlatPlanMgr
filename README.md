# FlatPlanMgr
FlatPlan manager for reveil.ch

Allows to create flat plans for publications. Consists of an offline editor and an online viewer. The editor can be used to view and edit the flat plan while the online viewer is only able to display it.

## Features Offline Editor

 - Display the flat plan
 - Display the list of authors
 - Add pages
 - Remove pages
 - Change page properties
 - Rearrange pages
 - Add authors
 - Remove authors
 - Edit authors
 - Upload / Download to central server

## Features Online Viewer

 - Display the flat plan
 - User login
 - if logged in:
	- Display the list of authors
	- Send E-Mail to author(s)
  - Display all available plans

## File format Offline config
For the Offline Editor you can create a config file containing informations about the server. Place the file called `fpm_config.txt` next to the .jar file. The file should contain the following informations in a `key=value` format:

    base_url=http://localhost/api.php?v=1
    username=test
    password=test


## File format FlatPlan
FPMgr uses csv as it's file format. It contains one table with the following columns, one article per line:

 - First Page
 - Length
 - Title
 - Author
 - Category
 - File format
 - Status

The status can be one of the following:
 1. Missing
 2. Received
 3. Received (no autolayout)
 4. Layout in progress
 5. Layout finished

## File format AuthorList
The author list is a csv file as well. It contains one author per line with the following columns:

- First Name
- Last Name
- Role
- E-Mail Address
