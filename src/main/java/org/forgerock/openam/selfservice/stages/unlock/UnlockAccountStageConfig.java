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


package org.forgerock.openam.selfservice.stages.unlock;

import org.forgerock.selfservice.core.config.StageConfig;

import java.util.Objects;

/**
 * Configuration for the unlock account reset stage.
 *
 * @since 0.1.0
 */
public final class UnlockAccountStageConfig implements StageConfig {

    /**
     * Name of the stage configuration.
     */
    public static final String NAME = "unlockAccountStage";

    private String identityServiceUrl;

    private String userAccountLockField;

    /**
     * Gets the URL for the identity service.
     *
     * @return the identity service URL
     */
    public String getIdentityServiceUrl() {
        return identityServiceUrl;
    }

    /**
     * Sets the URL for the identity service.
     *
     * @param identityServiceUrl the identity service URL
     * @return this config instance
     */
    public UnlockAccountStageConfig setIdentityServiceUrl(String identityServiceUrl) {
        this.identityServiceUrl = identityServiceUrl;
        return this;
    }

    /**
     * Get User account lock field
     *
     * @return
     */
    public String getUserAccountLockField() {
        return userAccountLockField;
    }

    /**
     * Sets user account lock field
     *
     * @param userAccountLockField
     */
    public UnlockAccountStageConfig setUserAccountLockField(String userAccountLockField) {
        this.userAccountLockField = userAccountLockField;
        return this;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getProgressStageClassName() {
        return UnlockAccountStage.class.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof UnlockAccountStageConfig)) {
            return false;
        }

        UnlockAccountStageConfig that = (UnlockAccountStageConfig) o;
        return Objects.equals(getName(), that.getName())
                && Objects.equals(getProgressStageClassName(), that.getProgressStageClassName())
                && Objects.equals(identityServiceUrl, that.identityServiceUrl)
                && Objects.equals(userAccountLockField, that.userAccountLockField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getProgressStageClassName(),
                identityServiceUrl, userAccountLockField);
    }

}
