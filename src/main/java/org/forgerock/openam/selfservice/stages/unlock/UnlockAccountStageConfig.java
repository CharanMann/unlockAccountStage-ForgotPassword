/*
 * Copyright © 2017 ForgeRock, AS.
 *
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Portions Copyrighted 2017 Charan Mann
 *
 * unlockAccountStage-ForgotPassword: Created by Charan Mann on 4/5/17 , 9:26 AM.
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
