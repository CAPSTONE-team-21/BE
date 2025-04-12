package org.sspoid.sspoid.api.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.sspoid.sspoid.api.dto.ChatMessageRequest;
import org.sspoid.sspoid.api.dto.ChatMessageResponse;
import org.sspoid.sspoid.api.dto.ChatSessionResponse;

import java.util.List;
import java.util.Map;

@Tag(name = "ChatBot", description = "채팅 관련 API")
public interface ChatBotSpecification {

    // 1. 세션 생성
    @Operation(summary = "Session 생성",
        description = "채팅방을 생성하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "세션 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ChatSessionResponse> createSessions();

    //2. 세션 제목 수정
    @Operation(summary = "Session title 수정",
            description = "채팅방의 제목을 수정하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "세션 제목 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 세션이 존재하지 않음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ChatSessionResponse> updateSessionTitle(@PathVariable Long id, @RequestBody Map<String, String> request);

    //3. 메세지 전송
    @Operation(summary = "Chatting Message 전송 API",
            description = "채팅창에 메세지를 전송하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 전송 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 세션 ID"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<ChatMessageResponse>> sendMessage(@PathVariable Long id, @RequestBody ChatMessageRequest message);

    // 4. 특정 세션에 저장되어있는 메시지 리스트 조회
    @Operation(summary = "Message List 조회 API",
            description = "채팅창에 다시 접속했을 때 이전 채팅 리스트를 불러오는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 세션 ID"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<ChatMessageResponse>> getMessage(@PathVariable Long id);

    // 5. 세션 목록 조회 (필터 가능)
    @Operation(summary = "Session 목록 조회 API",
            description = "전체 채팅창 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "세션 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<ChatSessionResponse>> getSessions();
}
