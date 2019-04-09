##### **************************************
##### USE CASE:  Android SDK Use Cases 
##### DESCRIPTION: 
##### VERSION:    1.00
##### CREATED BY: Ben Lu
##### CREATED AT: 4/9/2019
##### **************************************

#### Actors
Authentication Endpoint
#### Description
Client application should provide the company id and authentication key (provided by Parabit when the client is allowed to use the SDK), the SDK will perform authentication request from the AWS API through the authentication endpoint in order to get beacon info and unlock endpoint info

#### Usage
First time using the SDK in the scope of every life cycle of client application.

#### Steps
##### Step 1
get the following info from client application
* authentication_key
* company_id

##### Step 2
Try to get the previous beacon info and unlock endpoint info form storage
* if this info is still valid to use (updated from AWS API less than 24 hours), the SDK will do the lazy authentication. It will not call the AWS authentication end point. Instead, SDK will keep using this info and return an authentication success message to client application.
* if this info is not valid (updated from AWS API more than 24 hours), process to step 3

##### Step 3
get device id
* if the SDK fail to get the READ_PHONE_STATE permission, return a status code to client application
* if successfully get device id, process to Step 4

##### Step 4
SDK will call the AWS authentication end point to process authentication.
* If authentication is successful, SDK will save the access info to storage, and return an authentication success message to client application
* If authentication fail, the SDK will return a status code to client application

#### Actors
Beacon Info Endpoint
#### Description
Client should provide the beacon serial number.  The SDK will get the beacon info base on the serial number from AWS API endpoint and return the info back to client
#### Usage
After authentication and want to get info of a specific beacon serial number
#### Step
##### Step 1
get the following info from client application
* beacon_id (6 digits baecon serial number)

##### Step 2
SDK get beacon info endpoint and access key from storage. 
* If no info in storage, SDK will return a status code to client application to indicated that authentication is needed
* If successfully get info from storage, process to step 3

##### Step 3
get device id
* if the SDK fail to get the READ_PHONE_STATE permission, return a status code to client application
* if successfully get device id, process to Step 4

##### Step 4
Require beacon info from BeaconInfo Endpoint
* If success, return back the beacon info to client application
* If fail, return back a status code to client application

#### Actors
Door Unlock Endpoint
#### Description
Client application should provide beacon authentication key, serial number, open duration, bank id and client id to the SDK, SDK will require AWS to unlock the door with this serial number
#### Usage
After authentication and want to remote unlock a certain MMR door

#### Steps
##### Step 1
get the following info from client application
* beacon_id (6 digits baecon serial number)
* bank_id
* customer_id (client_id)
* duration

##### Step 2
SDL get unlock endpoint, access key and TOTP token from storage
* If no info in storage, SDK will return a status code to client application to indicated that authention is needed
* If successfully gather info from storage, process to step 3

##### Step 3
get device id
* if the SDK fail to get the READ_PHONE_STATE permission, return a status code to client application
* if successfully get device id, process to Step 4

##### Step 4
SDK generate a random totp_password base on the totp_token, and send this password as well as all the other required information to AWS Unlock Endpoint to require certain door unlock
* If success, return back the door unlock “true” to client
* If fail, return back door unlock “fail” as well as a status code to client








