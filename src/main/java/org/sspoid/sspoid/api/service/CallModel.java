package org.sspoid.sspoid.api.service;

import lombok.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class CallModel {

    // 임시
    public String generateResponse(String prompt) {
        List<String> dummyResponses = List.of(
                "건성 피부에게 적합한 제품이에요!",
                "지성 피부는 이 제품을 피하는 게 좋아요.",
                "민감성 피부라면 전성분을 꼭 확인하세요.",
                "복합성 피부엔 부위별로 다르게 써보는 것도 좋아요."
        );
        return "🤖 " + dummyResponses.get(new Random().nextInt(dummyResponses.size()));
    }

    //public String generateSummary(String conversation) {}

//    @Value("${model.api.url}")
//    private String modelApiUrl;
//
//    private final RestTemplate restTemplate;
//
//    public CallModel(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//
//    public String generateResponse(String prompt) {
//        // 실제 모델 API 요청
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Map<String, String> body = new HashMap<>();
//        body.put("prompt", prompt);
//
//        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
//        ResponseEntity<String> response = restTemplate.postForEntity(modelApiUrl, request, String.class);
//
//        return response.getBody();
//
//    }
}
