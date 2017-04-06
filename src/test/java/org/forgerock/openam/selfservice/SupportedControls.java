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

import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.LdapException;
import org.forgerock.opendj.ldap.RootDSE;

import java.util.Collection;

public class SupportedControls {
    /**
     * Controls supported by the LDAP server.
     */
    private static Collection<String> controls;

    /**
     * Populate the list of supported LDAP control OIDs.
     *
     * @param connection Active connection to the LDAP server.
     * @throws org.forgerock.opendj.ldap.LdapException Failed to get list of controls.
     */
    static void loadSupportedControls(Connection connection)
            throws LdapException {
        controls = RootDSE.readRootDSE(connection).getSupportedControls();
    }

    /**
     * Check whether a control is supported. Call {@code loadSupportedControls}
     * first.
     *
     * @param control Check support for this control, provided by OID.
     * @return True if the control is supported.
     */
    static boolean isSupported(final String control) {
        if (controls != null && !controls.isEmpty()) {
            return controls.contains(control);
        }
        return false;
    }
}
