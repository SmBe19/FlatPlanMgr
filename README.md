# FlatPlanMgr
FlatPlan manager for reveil.ch

Allows to create flat plans for publications. Consists of an offline editor and an online viewer. The editor can be used to view and edit the flat plan while the online viewer is only able to display it.

## Features Offline Editor

 - Display the flat plan
 - Display the list of authors
 - Add pages
 - Remove pages
 - Change page properties
 - Rearange pages
 - Add authors
 - Remove authors
 - Edit authors

## Features Online Viewer

 - Display the flat plan
 - User login
 - if logged in:
	- Display the list of authors
	- Send E-Mail to author(s)

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
