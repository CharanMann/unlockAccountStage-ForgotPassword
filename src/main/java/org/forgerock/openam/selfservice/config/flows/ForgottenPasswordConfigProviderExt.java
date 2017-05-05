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

package org.forgerock.openam.selfservice.config.flows;

import org.forgerock.openam.selfservice.KeyStoreJwtTokenConfig;
import org.forgerock.openam.selfservice.config.ServiceConfigProvider;
import org.forgerock.openam.selfservice.config.beans.ForgottenPasswordConsoleConfig;
import org.forgerock.openam.selfservice.stages.unlock.UnlockAccountStageConfig;
import org.forgerock.selfservice.core.StorageType;
import org.forgerock.selfservice.core.config.ProcessInstanceConfig;
import org.forgerock.selfservice.core.config.StageConfig;
import org.forgerock.selfservice.stages.captcha.CaptchaStageConfig;
import org.forgerock.selfservice.stages.email.VerifyEmailAccountConfig;
import org.forgerock.selfservice.stages.kba.KbaConfig;
import org.forgerock.selfservice.stages.kba.SecurityAnswerVerificationConfig;
import org.forgerock.selfservice.stages.reset.ResetStageConfig;
import org.forgerock.selfservice.stages.user.UserQueryConfig;
import org.forgerock.services.context.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension of {@link org.forgerock.openam.selfservice.config.flows.ForgottenPasswordConfigProvider}
 */
public final class ForgottenPasswordConfigProviderExt
        implements ServiceConfigProvider<ForgottenPasswordConsoleConfig> {

    @Override
    public boolean isServiceEnabled(ForgottenPasswordConsoleConfig config) {
        return config.isEnabled();
    }

    @Override
    public ProcessInstanceConfig getServiceConfig(
            ForgottenPasswordConsoleConfig config, Context context, String realm) {

        List<StageConfig> stages = new ArrayList<>();

        if (config.isCaptchaEnabled()) {
            stages.add(new CaptchaStageConfig()
                    .setRecaptchaSiteKey(config.getCaptchaSiteKey())
                    .setRecaptchaSecretKey(config.getCaptchaSecretKey())
                    .setRecaptchaUri(config.getCaptchaVerificationUrl()));
        }

        stages.add(new UserQueryConfig()
                .setValidQueryFields(config.getValidQueryAttributes())
                .setIdentityIdField("/username")
                .setIdentityUsernameField("/username")
                .setIdentityEmailField("/" + config.getEmailAttributeName() + "/0")
                .setIdentityServiceUrl("/users"));

        if (config.isEmailEnabled()) {
            String serverUrl = config.getEmailVerificationUrl() + "&realm=" + realm;
            stages.add(new VerifyEmailAccountConfig()
                    .setEmailServiceUrl("/email")
                    .setIdentityEmailField(config.getEmailAttributeName())
                    .setSubjectTranslations(config.getSubjectTranslations())
                    .setMessageTranslations(config.getMessageTranslations())
                    .setMimeType("text/html")
                    .setVerificationLinkToken("%link%")
                    .setVerificationLink(serverUrl));
        }

        if (config.isKbaEnabled()) {
            stages.add(new SecurityAnswerVerificationConfig(new KbaConfig())
                    .setQuestions(config.getSecurityQuestions())
                    .setKbaPropertyName("kbaInfo")
                    .setNumberOfQuestionsUserMustAnswer(config.getMinimumAnswersToVerify())
                    .setIdentityServiceUrl("/users"));
        }

        stages.add(new ResetStageConfig()
                .setIdentityServiceUrl("/users")
                .setIdentityPasswordField("userPassword"));

        // Custom stage to unlock user account
        stages.add(new UnlockAccountStageConfig()
                .setIdentityServiceUrl("/users")
                .setUserAccountLockField("sunAMAuthInvalidAttemptsData"));

        KeyStoreJwtTokenConfig extendedJwtTokenConfig = new KeyStoreJwtTokenConfig()
                .withEncryptionKeyPairAlias(config.getEncryptionKeyPairAlias())
                .withSigningSecretKeyAlias(config.getSigningSecretKeyAlias())
                .withTokenLifeTimeInSeconds(config.getTokenExpiry());

        return new ProcessInstanceConfig()
                .setStageConfigs(stages)
                .setSnapshotTokenConfig(extendedJwtTokenConfig)
                .setStorageType(StorageType.STATELESS);
    }

}

