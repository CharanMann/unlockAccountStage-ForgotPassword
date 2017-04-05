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

import org.forgerock.json.JsonValue;
import org.forgerock.json.resource.*;
import org.forgerock.selfservice.core.ProcessContext;
import org.forgerock.selfservice.core.ProgressStage;
import org.forgerock.selfservice.core.StageResponse;
import org.forgerock.selfservice.core.annotations.SelfService;
import org.forgerock.selfservice.core.util.RequirementsBuilder;
import org.forgerock.services.context.Context;
import org.forgerock.util.Reject;

import javax.inject.Inject;

import static org.forgerock.json.JsonValue.*;
import static org.forgerock.selfservice.stages.CommonStateFields.USER_ID_FIELD;

/**
 * The unlock account stage.
 *
 * @since 0.1.0
 */
public final class UnlockAccountStage implements ProgressStage<UnlockAccountStageConfig> {

    private final ConnectionFactory connectionFactory;

    /**
     * Constructs a new reset stage.
     *
     * @param connectionFactory the CREST connection factory
     */
    @Inject
    public UnlockAccountStage(@SelfService ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public JsonValue gatherInitialRequirements(ProcessContext context,
                                               UnlockAccountStageConfig config) throws ResourceException {
        Reject.ifFalse(context.containsState(USER_ID_FIELD), "Unlock  account stage expects userId in the context");
        return RequirementsBuilder.newEmptyRequirements();
    }

    @Override
    public StageResponse advance(ProcessContext context, UnlockAccountStageConfig config) throws ResourceException {
        String userId = context
                .getState(USER_ID_FIELD)
                .asString();

        ResourceResponse response = readUser(context.getRequestContext(), userId, config);
        JsonValue value = response.getContent().get(config.getUserAccountLockField());

        //If account has some invalid login attempts
        if (null != value.getObject()) {
            String sunAMAuthInvalidAttemptsData = value.getObject().toString();
            //If account is locked
            if (sunAMAuthInvalidAttemptsData.contains("<LockedoutAt>") && !sunAMAuthInvalidAttemptsData.contains("<LockedoutAt>0</LockedoutAt>")) {

                // Set sunAMAuthInvalidAttemptsData to []
                JsonValue jsonValue = json(object(field(config.getUserAccountLockField(), array())));

                // Finally unlock the account
                unlockUser(context.getRequestContext(), userId, config, jsonValue);
            }
        }

        return StageResponse
                .newBuilder()
                .build();
    }

    private ResourceResponse readUser(Context requestContext, String userId, UnlockAccountStageConfig config) throws ResourceException {
        try (Connection connection = connectionFactory.getConnection()) {
            ReadRequest request = Requests.newReadRequest(config.getIdentityServiceUrl(), userId);
            ResourceResponse response = connection.read(requestContext, request);
            return response;
        }
    }

    private void unlockUser(Context requestContext, String userId,
                            UnlockAccountStageConfig config, JsonValue jsonValue) throws ResourceException {
        try (Connection connection = connectionFactory.getConnection()) {
            UpdateRequest request = Requests.newUpdateRequest(config.getIdentityServiceUrl(), userId, jsonValue);
            connection.update(requestContext, request);
        }
    }

}
