package com.social.notificationService.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PagedNotiResponse {
    private final List<NotiResponse> content;
    private final int page;
    private final int size;
    private final long total;
    private final int totalPages;
    private final boolean isFirst;
    private final boolean isLast;

    public PagedNotiResponse(List<NotiResponse> content, int page, int size, long total) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) ((total + size - 1) / size); // Làm tròn lên
        this.isFirst = page == 0;
        this.isLast = page == totalPages - 1;
    }

}
