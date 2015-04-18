/*
 * Copyright 2015, Jakob Korherr
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
 */
package org.mascherl.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.mascherl.servlet.MascherlFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mascherl.MascherlConstants.MASCHERL_SESSION_REQUEST_ATTRIBUTE;

/**
 * The session of Mascherl, which can be used to store client specific values between requests.
 *
 * This session implementation is very much alike the session implementation of the Play framework.
 * Instead of relying on classical server-side session storage (which only stores the session ID on the client browser,
 * and keeps the real data on the processing server, possibly replicating it to the other servers in the cluster),
 * it serializes the actual session data into an AES encrypted cookie, which will be stored in the client browser.
 * On subsequent requests this cookie can be decrypted by any application server on the cluster that uses the same
 * application secret (org.mascherl.session.secret) as the original server, without any additional communication
 * between the servers.
 *
 * This session implementation uses Jackson in order to serialize the session data into a JSON string. Thus, any type
 * or object can be stored into the session, which can be serialized and deserialized by Jackson.
 *
 * Please note that the data, which can be stored in this session implementation is very limited, due to the HTTP cookie
 * size limitation of (approximately) 4 kilobytes, including cookie name and metadata. In addition, the AES encryption
 * adds some overhead to the cookie data, thus the maximum size of the serialized session data is 3039 characters
 * (@see org.mascherl.session.MascherlSessionStorage#MAX_DATA_SIZE).
 *
 * @author Jakob Korherr
 */
public class MascherlSession {

    public static MascherlSession getInstance() {
        return (MascherlSession) MascherlFilter.getRequest().getAttribute(MASCHERL_SESSION_REQUEST_ATTRIBUTE);
    }

    private final Map<String, Object> dataStorage = new HashMap<>();
    private final ObjectMapper objectMapper;
    private final ObjectNode jsonRootNode;
    private boolean modified = false;

    public MascherlSession(ObjectMapper objectMapper) {
        this(objectMapper, objectMapper.createObjectNode());
    }

    public MascherlSession(ObjectMapper objectMapper, String json) {
        this(objectMapper, parseJson(json, objectMapper));
    }

    protected MascherlSession(ObjectMapper objectMapper, ObjectNode jsonRootNode) {
        this.objectMapper = objectMapper;
        this.jsonRootNode = jsonRootNode;
    }

    public void remove(String key) {
        dataStorage.remove(key);
        jsonRootNode.remove(key);
        modified = true;
    }

    public void put(String key, Object data) {
        if (!objectMapper.canSerialize(data.getClass())
                || !objectMapper.canDeserialize(TypeFactory.defaultInstance().constructType(data.getClass()))) {
            throw new IllegalArgumentException("Data of this type cannot be serialized in the Mascherl session");
        }
        dataStorage.put(key, data);
        modified = true;
    }

    public Integer getInt(String key) {
        return get(key, Integer.class);
    }

    public String getString(String key) {
        return get(key, String.class);
    }

    public Date getDate(String key) {
        return get(key, Date.class);
    }

    public <T> T get(String key, Class<T> expectedType) {
        if (dataStorage.containsKey(key)) {
            return expectedType.cast(dataStorage.get(key));
        }
        if (jsonRootNode.has(key)) {
            try {
                return objectMapper.reader(expectedType).readValue(jsonRootNode.path(key));
            } catch (IOException | RuntimeException e) {
                return null;
            }
        }
        return null;
    }

    public boolean contains(String key) {
        return dataStorage.containsKey(key) || jsonRootNode.has(key);
    }

    public String serialize() {
        for (Map.Entry<String, Object> entry : dataStorage.entrySet()) {
            jsonRootNode.putPOJO(entry.getKey(), entry.getValue());
        }
        try {
            return objectMapper.writeValueAsString(jsonRootNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean wasModified() {
        return modified;
    }

    private static ObjectNode parseJson(String json, ObjectMapper objectMapper) {
        try {
            return (ObjectNode) objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
