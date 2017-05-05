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
