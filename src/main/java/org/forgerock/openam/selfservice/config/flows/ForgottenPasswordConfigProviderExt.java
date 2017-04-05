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

        // Custom stage
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

