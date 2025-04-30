package org.sspoid.sspoid.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sspoid.sspoid.api.dto.BookmarkRequest;
import org.sspoid.sspoid.api.service.BookmarkService;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    //북마크 설정
    @PostMapping("/api/bookmarks")
    public ResponseEntity<Void> createBookmark(@RequestBody BookmarkRequest request) {
        bookmarkService.createBookmark(request.sessionId());
        return ResponseEntity.ok().build();
    }

    //북마크 해제
    @DeleteMapping("/api/bookmarks/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable("id") Long sessionId) {
        bookmarkService.deleteBookmark(sessionId);
        return ResponseEntity.noContent().build();
    }

}
