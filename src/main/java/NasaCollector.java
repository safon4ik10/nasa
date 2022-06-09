import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NasaCollector {

    private String token;
    private static final String NASA_URL = "https://api.nasa.gov/planetary/apod?api_key=";

    public NasaCollector(String token) {
        this.token = token;
    }

    public NasaCollector() {
    }

    void setToken(String token) {
        this.token = token;
    }

    List<Nasa> collectInfo() {
        List<Nasa> nasaList;
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        String body = getBody(httpClient);

        if (body != null) {
            try {
                nasaList = new ObjectMapper().readValue(body, new TypeReference<List<Nasa>>() {
                });
                return nasaList;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    String getBody(CloseableHttpClient httpClient) {
        HttpGet request = new HttpGet(NASA_URL + token);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
