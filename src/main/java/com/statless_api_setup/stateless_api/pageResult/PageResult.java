package com.statless_api_setup.stateless_api.pageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

// Stable wrapper
public class PageResult<T> {
    public List<T> items;
    public Meta meta;

    public static class Meta {
        public int page;            // zero-based
        public int size;
        public long totalElements;
        public int totalPages;
        public boolean first;
        public boolean last;
        public int numberOfElements;
        public String sort;         // e.g. "createdAt: DESC"
    }

    public static <T> PageResult<T> from(Page<T> p, Pageable pageable) {
        PageResult<T> r = new PageResult<>();
        r.items = p.getContent();
        r.meta = new Meta();
        r.meta.page = p.getNumber();
        r.meta.size = p.getSize();
        r.meta.totalElements = p.getTotalElements();
        r.meta.totalPages = p.getTotalPages();
        r.meta.first = p.isFirst();
        r.meta.last = p.isLast();
        r.meta.numberOfElements = p.getNumberOfElements();
        r.meta.sort = pageable.getSort().isSorted() ? pageable.getSort().toString() : "";
        return r;
    }
}

