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
 * TODO
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
            } catch (IOException e) {
                throw new RuntimeException(e);
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
