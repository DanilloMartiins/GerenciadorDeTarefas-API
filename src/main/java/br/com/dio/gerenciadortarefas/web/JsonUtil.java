package br.com.dio.gerenciadortarefas.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonUtil() {
    }

    public static <T> T fromBody(InputStream body, Class<T> targetClass) throws IOException {
        byte[] bytes = body.readAllBytes();
        if (bytes.length == 0) {
            return null;
        }
        return MAPPER.readValue(bytes, targetClass);
    }

    public static byte[] toJsonBytes(Object content) throws JsonProcessingException {
        String json = MAPPER.writeValueAsString(content);
        return json.getBytes(StandardCharsets.UTF_8);
    }
}
