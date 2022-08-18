package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import ru.skillbox.searcher.service.search.response.SearchResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class JsonUtils {

    public static <T> T mapFromMvc(MvcResult mvcResult, Class<T> clazz) throws IOException {
        String content = mvcResult.getResponse().getContentAsString();
        return JsonUtils.mapFromJson(content, clazz);
    }

    private static <T> T mapFromJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }
}
