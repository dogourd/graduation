package top.ezttf.graduation.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author yuwen
 * @date 2018/12/23
 */
@Slf4j
public class JsonUtil {

    public static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.setDateFormat(new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US));
    }

    private JsonUtil() {
    }

    /**
     * 单类型json反序列化
     *
     * @param json  json字符擦混
     * @param clazz 对象类类型
     * @param <T>   泛型
     * @return
     * @throws IOException
     */
    @SuppressWarnings("all")
    public static <T> T str2Obj(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) json : MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.warn("Parse String to Object fail ", e);
            return null;
        }
    }

    /**
     * 多泛型json反序列化
     *
     * @param json          json字符串
     * @param typeReference typeReference, 可匿名内部类方式引用
     * @param <T>           泛型
     * @return
     * @throws IOException
     */
    @SuppressWarnings("all")
    public static <T> T str2Obj(String json, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(json) || typeReference == null) {
            return null;
        }
        try {
            return typeReference.getType().equals(String.class)
                    ? (T) json
                    : (T) MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            log.warn("Parse String to Object fail ", e);
            return null;
        }
    }

    /**
     * 多泛型 json 反序列化
     *
     * @param json            json字符串
     * @param collectionClazz 集合类型
     * @param elementClazz    集合泛型
     * @param <T>             泛型
     * @return 反序列化对象
     * @throws IOException
     */
    public static <T> T str2Obj(String json, Class<?> collectionClazz, Class<?>... elementClazz) {
        if (StringUtils.isBlank(json) || collectionClazz == null || elementClazz == null) {
            return null;
        }
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(collectionClazz, elementClazz);
        try {
            return MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            log.warn("Parse String to Object fail ", e);
            return null;
        }
    }


    /**
     * Json 序列化
     *
     * @param obj 序列化对象
     * @param <T> 任意类型
     * @return
     */
    public static <T> String obj2Str(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String fail ", e);
            return null;
        }
    }

    @SuppressWarnings("all")
    public static <T> String obj2StrPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String
                    ? (String) obj
                    : MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String fail ", e);
            return null;
        }
    }
}
