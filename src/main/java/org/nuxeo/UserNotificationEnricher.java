package org.nuxeo;
/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nuxeo
 */


import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.context.RenderingContext;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.ecm.platform.notification.api.NotificationManager;
import org.nuxeo.ecm.platform.ec.notification.SubscriptionAdapter;

@Setup(mode = SINGLETON, priority = REFERENCE)
public class UserNotificationEnricher extends AbstractJsonEnricher<NuxeoPrincipal> {

    public static final String NAME = "usernotif";

    public UserNotificationEnricher() {
        super(NAME);
    }

    @Override
    public void write(JsonGenerator jg, NuxeoPrincipal nuxeoPrincipal) throws IOException {

        String prefixedUserName = NuxeoPrincipal.PREFIX + nuxeoPrincipal.getName();
        NotificationManager nm = Framework.getService(NotificationManager.class);
        List<DocumentModel> subscribedDocs = nm.getSubscribedDocuments(prefixedUserName);

        try (RenderingContext.SessionWrapper sw = ctx.getSession(null)) {

            jg.writeFieldName("subscribedDocs");
            if (subscribedDocs == null) {
                jg.writeNull();
            } else {
                try {
                    jg.writeStartObject();
                    for (DocumentModel doc : subscribedDocs) {
                        SubscriptionAdapter sa = doc.getAdapter(SubscriptionAdapter.class);
                        List<String> notifications = sa.getUserSubscriptions(prefixedUserName);
                        for (String notification : notifications) {
                            jg.writeStringField(doc.getId(), notification);
                        }
                    }
                } finally {
                    jg.writeEndObject();
                }
            }
        }
    }
}
