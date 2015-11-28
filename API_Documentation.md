# API Documentation
The API is reachable under `/api.php?v=1`. Perform GET or POST requests to interact. You have to be logged in to perform the actions.

## Available parameters

Use `action=id` to specify the desired action. Depending on the action there are different parameters. Available actions:

* `login`: Login to the system. Specify credentials with `username=name` and `password=pwd`.
* `logout`: Logout of the system.
* `download_plan`: Download the flat plan given by `plan=id`. Returns the plan_file followed by a line with "---" followed by the authors_file.
* `upload_plan`: Upload the flat plan to the name given by `plan=name`. Transfer the file using the parameter `fileUpload`. This call returns the id of the uploaded plan.
