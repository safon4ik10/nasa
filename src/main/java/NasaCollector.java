import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NasaCollector {

    private String token;
    private static final String NASA_URL = "https://api.nasa.gov/planetary/apod?api_key=";
    private static final Pattern pattern = Pattern.compile("\\w+.jpg");

    public NasaCollector(String token) {
        this.token = token;
    }

    public NasaCollector() {
    }

    void setToken(String token) {
        this.token = token;
    }


    private CloseableHttpClient getClient() {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        return httpClient;
    }

    private String getBody(CloseableHttpClient httpClient) {
        HttpGet request = new HttpGet(NASA_URL + token);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Nasa getNasaObject() {
        String body = getBody(getClient());
        if (body != null) {
            try {
                return new ObjectMapper().readValue(body, new TypeReference<Nasa>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void saveImg(Nasa nasa, boolean isHDFile) {
        CloseableHttpClient httpClient = getClient();
        URL imgUrl = isHDFile ? nasa.getHdurl() : nasa.getUrl();

        try (InputStream in = imgUrl.openStream()) {
            String fileName = imgUrl.getFile();
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find()) {
                String urlFilename = matcher.group();
                try {
                    Files.copy(in, Paths.get(urlFilename));
                } catch (FileAlreadyExistsException e){
                    System.out.println("Изображение: " + e.getMessage() + " уже существует");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
