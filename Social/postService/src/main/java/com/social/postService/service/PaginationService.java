package com.social.postService.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaginationService {
    /**
     * Generic pagination method.
     *
     * @param items   Full list of items (entities)
     * @param pageable Pageable object with pageNumber, pageSize
     * @param mapper Function to map entity -> DTO (eg. Post -> PostResponse)
     * @return Page of mapped DTOs
     */
    public <T, R> Page<R> paginate(List<T> items, Pageable pageable, Function<T, R> mapper) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int start = Math.min(currentPage * pageSize, items.size());
        int end = Math.min(start + pageSize, items.size());

        List<R> pagedItems = items.subList(start, end).stream()
                .map(mapper)
                .collect(Collectors.toList());

        return new PageImpl<>(pagedItems, pageable, items.size());
    }
}
