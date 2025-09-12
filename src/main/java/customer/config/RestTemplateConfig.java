/*http请求*/
package customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

/*
发送请求示例
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
public class HttpService {
    private final RestTemplate restTemplate;

    public HttpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // GET请求
    public String getExample() {
        String url = "https://api.example.com/data";
        return restTemplate.getForObject(url, String.class);
    }

    // POST请求（带JSON体）
    public Response postExample(RequestObject request) {
        String url = "https://api.example.com/submit";
        return restTemplate.postForObject(url, request, Response.class);
    }

    // 带查询参数的GET
    public String getWithParams(String param) {
        URI uri = UriComponentsBuilder.fromUriString("https://api.example.com/search")
                .queryParam("key", param)
                .build()
                .toUri();
        return restTemplate.getForObject(uri, String.class);
    }
}*/
