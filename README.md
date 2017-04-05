# OpenAM-Unlock Account Self-service stage

*  <br />
* Displays Email and/or Phone number where HOTP has been sent 
    
Pre-requisites :
================
1. OpenAM has been installed and configured.
2. Maven has been installed and configured.

OpenAM Configuration:
=====================
1. Build the custom auth module by using maven. 
2. Deploy the custom auth module. Refer instructions: *[Building and Installing Custom Authentication Modules](https://backstage.forgerock.com/docs/openam/13.5/dev-guide#build-config-sample-auth-module)*
```
Register service and module: Note that for OpenAM v12 use amAuthHOTPExt-12.xml
forgerock@openam1-12:~/openam1/tools/openam/bin$ ./ssoadm create-svc --adminid amadmin --password-file /tmp/pwd.txt --xmlfile ~/softwares/amAuthHOTPExt.xml
forgerock@openam1-12:~/openam1/tools/openam/bin$ ./ssoadm register-auth-module --adminid amadmin --password-file /tmp/pwd.txt --authmodule com.sun.identity.authentication.modules.hotp.HOTPExt

UnRegister service and module (in case module needs to be uninstalled) : 
./ssoadm unregister-auth-module --adminid amadmin --password-file /tmp/pwd.txt --authmodule com.sun.identity.authentication.modules.hotp.HOTPExt
./ssoadm delete-svc --adminid amadmin --password-file /tmp/pwd.txt -s sunAMAuthHOTPExtService
```
3. Configure the custom auth module. Refer instructions: *[Configuring and Testing Custom Authentication Modules](https://backstage.forgerock.com/docs/openam/13.5/dev-guide#configuring-testing-sample-auth-module)*
4. Configure HOTPExt module with required SMTP server. Enable both SMS and EMail.
5. Create a chain(otpChain) with (LDAP:Required, HOTPExt:Required). Set this chain as default for "Organization Authentication"
  
Testing:
======== 
* Authentication testing for otpChain:
```
curl -X POST -H "Content-Type: application/json" -H "X-OpenAM-Username: testUser1" -H "X-OpenAM-Password: password" "http://openam.sample.com/openam/json/employees/authenticate"

{
  "authId": "eyAid..",
  "template": "",
  "stage": "HOTPExt2",
  "header": "Please enter your One Time Password sent at Email testUser1@gmail.com and Phone: 2223334444",
  "callbacks": [
    {
      "type": "PasswordCallback",
      "output": [
        {
          "name": "prompt",
          "value": "Enter OTP"
        }
      ],
      "input": [
        {
          "name": "IDToken1",
          "value": ""
        }
      ]
    },
    {
      "type": "ConfirmationCallback",
      "output": [
        {
          "name": "prompt",
          "value": ""
        },
        {
          "name": "messageType",
          "value": 0
        },
        {
          "name": "options",
          "value": [
            "Submit OTP",
            "Request OTP"
          ]
        },
        {
          "name": "optionType",
          "value": -1
        },
        {
          "name": "defaultOption",
          "value": 0
        }
      ],
      "input": [
        {
          "name": "IDToken2",
          "value": 0
        }
      ]
    }
  ]
}
```



* * *

Copyright © 2017 ForgeRock, AS.

This is unsupported code made available by ForgeRock for community development subject to the license detailed below. The code is provided on an "as is" basis, without warranty of any kind, to the fullest extent permitted by law. 

ForgeRock does not warrant or guarantee the individual success developers may have in implementing the code on their development platforms or in production configurations.

ForgeRock does not warrant, guarantee or make any representations regarding the use, results of use, accuracy, timeliness or completeness of any data or information relating to the alpha release of unsupported code. ForgeRock disclaims all warranties, expressed or implied, and in particular, disclaims all warranties of merchantability, and warranties related to the code, or any service or software related thereto.

ForgeRock shall not be liable for any direct, indirect or consequential damages or costs of any type arising out of any action taken by you or others related to the code.

The contents of this file are subject to the terms of the Common Development and Distribution License (the License). You may not use this file except in compliance with the License.

You can obtain a copy of the License at https://forgerock.org/cddlv1-0/. See the License for the specific language governing permission and limitations under the License.

Portions Copyrighted 2017 Charan Mann
