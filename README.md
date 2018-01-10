# OpenAM-Unlock Account Stage for Forgot password flow

* When OpenAM Account lock policies are used then OpenAM forgot password flow doesn't unlock user account (https://bugster.forgerock.org/jira/browse/OPENAM-7776#)<br />
* This custom stage unlocks user's account in OpenAM.
    
Disclaimer of Liability :
=========================
This is an unsupported code made available by ForgeRock for community development subject to the license for specific
language governing permission and limitations under the license.

The code is provided on an "as is" basis, without warranty of any kind, to the fullest extent permitted by law. 

ForgeRock does not warrant or guarantee the individual success developers may have in implementing the code on their 
development platforms or in production configurations.

ForgeRock does not warrant, guarantee or make any representations regarding the use, results of use, accuracy, timeliness 
or completeness of any data or information relating to the alpha release of unsupported code. ForgeRock disclaims all 
warranties, expressed or implied, and in particular, disclaims all warranties of merchantability, and warranties related 
to the code, or any service or software related thereto.

ForgeRock shall not be liable for any direct, indirect or consequential damages or costs of any type arising out of any 
action taken by you or others related to the code.

Pre-requisites :
================
* Versions used for this project: OpenAM 13.5, OpenDJ 3.5 
1. OpenAM has been installed and configured.
2. Maven has been installed and configured.

OpenAM Configuration:
=====================
1. Build the custom stage by using maven. Copy the custom jar under AM_HOME/webapps/openam/WEB-INF/lib directory 
2. Delete all instances of User Self-Service from all realms. 
3. Remove existing selfService
```
./ssoadm delete-svc --adminid amadmin --password-file /tmp/pwd.txt -s selfService
```
4. Restart OpenAM
5. Register custom selfService
```
./ssoadm create-svc --adminid amadmin --password-file /tmp/pwd.txt --xmlfile ~/softwares/selfServiceExt.xml
```
6. Restart OpenAM
7. Add User Self-Service to specified realm and enable forgot password flow. 
  
Testing:
======== 
* Unlock user
1. Lock user by authenticating using wrong password till user is locked out.
```
curl --request POST \
  --url http://openam135.sample.com:8080/openam/json/authenticate \
  --header 'content-type: application/json' \
  --header 'x-openam-password: wrongPassword' \
  --header 'x-openam-username: test1'
  
{
  "code": 401,
  "reason": "Unauthorized",
  "message": "Authentication Failed"
}   
```
2. Try authentication after user is locked
```
curl --request POST \
  --url http://openam135.sample.com:8080/openam/json/authenticate \
  --header 'content-type: application/json' \
  --header 'x-openam-password: wrongPassword' \
  --header 'x-openam-username: test1'
  
{
  "code": 401,
  "reason": "Unauthorized",
  "message": "Your account has been locked."
}  
``` 
3. Follow forgot password flow to reset password and unlock account. 
4. Try authenticating again with new password. This should succeed. 


* * *

Copyright © 2017 ForgeRock, AS.

The contents of this file are subject to the terms of the Common Development and
Distribution License (the License). You may not use this file except in compliance with the
License.

You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
specific language governing permission and limitations under the License.

When distributing Covered Software, include this CDDL Header Notice in each file and include
the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
Header, with the fields enclosed by brackets [] replaced by your own identifying
information: "Portions copyright [year] [name of copyright owner]".

Portions Copyrighted 2017 Charan Mann
