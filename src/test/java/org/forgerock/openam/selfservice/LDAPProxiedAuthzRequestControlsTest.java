/*
 * Copyright © 2017 ForgeRock, AS.
 *
 * This is unsupported code made available by ForgeRock for community development subject to the license detailed below.
 * The code is provided on an "as is" basis, without warranty of any kind, to the fullest extent permitted by law.
 *
 * ForgeRock does not warrant or guarantee the individual success developers may have in implementing the code on their
 * development platforms or in production configurations.
 *
 * ForgeRock does not warrant, guarantee or make any representations regarding the use, results of use, accuracy, timeliness
 * or completeness of any data or information relating to the alpha release of unsupported code. ForgeRock disclaims all
 * warranties, expressed or implied, and in particular, disclaims all warranties of merchantability, and warranties related
 * to the code, or any service or software related thereto.
 *
 * ForgeRock shall not be liable for any direct, indirect or consequential damages or costs of any type arising out of any
 * action taken by you or others related to the code.
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License (the License).
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the License at https://forgerock.org/cddlv1-0/. See the License for the specific language governing
 * permission and limitations under the License.
 *
 * Portions Copyrighted 2017 Charan Mann
 *
 * unlockAccountStage-ForgotPassword: Created by Charan Mann on 4/4/17 , 2:53 PM.
 */

package org.forgerock.openam.selfservice;

import org.forgerock.opendj.ldap.*;
import org.forgerock.opendj.ldap.controls.ProxiedAuthV2RequestControl;
import org.forgerock.opendj.ldap.requests.ModifyRequest;
import org.forgerock.opendj.ldap.requests.Requests;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.forgerock.opendj.ldif.LDIFEntryWriter;

class LDAPProxiedAuthzRequestControlsTest {
    public static void main(String[] args) {

        String host = "192.168.56.122";
        int port = 1389;
        String baseDN = "ou=people,ou=employees,dc=sample,dc=com";
        String targetUID = "test5";

        // OpenDJ Service account used to perform operations, Need to have ds-privilege-name:proxied-auth and ACI: allow (proxy)
        final String serviceAccountBindDN = "uid=openamAdmin,ou=people,ou=employees,dc=sample,dc=com";
        final String serviceAccountPassword = "password";

        Connection connection = null;
        final LDAPConnectionFactory factory = new LDAPConnectionFactory(host, port);

        try {
            connection = factory.getConnection();
            // No explicit bind yet so we remain anonymous for now.
            SearchResultEntry entry = connection.searchSingleEntry(baseDN,
                    SearchScope.WHOLE_SUBTREE, "(uid=" + targetUID + ")", "cn");

            String cn = entry.getAttribute("cn").firstValueAsString();
            System.out.println("Hello, " + cn + "!");
            DN targetDn = entry.getName();
            final String authzId = "dn:" + targetDn.toString();

            SupportedControls.loadSupportedControls(connection);
            // If ProxiedAuthV2RequestControl is supported by underlying store
            if (false) {
                // Set new password, note same user as targetDn and authzId
                final ModifyRequest request =
                        Requests.newModifyRequest(targetDn)
                                .addControl(ProxiedAuthV2RequestControl.newControl(authzId))
                                .addModification(ModificationType.REPLACE, "userPassword",
                                        "Password#123");

                connection.bind(serviceAccountBindDN, serviceAccountPassword.toCharArray());
                connection.modify(request);
            } else {
                // In case ProxiedAuthV2RequestControl is not supported
                final ModifyRequest request =
                        Requests.newModifyRequest(targetDn)
                                .addModification(ModificationType.REPLACE, "userPassword",
                                        "Password$123");

                connection.bind(serviceAccountBindDN, serviceAccountPassword.toCharArray());
                connection.modify(request);
            }

            entry = connection.readEntry(targetDn, "givenName");

            final LDIFEntryWriter writer = new LDIFEntryWriter(System.out);

            writer.writeEntry(entry);
            writer.close();

        } catch (final Exception e) {
            System.err.println(e.getMessage());
            return;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
