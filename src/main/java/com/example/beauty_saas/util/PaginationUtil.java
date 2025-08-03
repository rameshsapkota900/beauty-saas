package com.example.beauty_saas.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil \{

    private PaginationUtil() \{
        // Private constructor to prevent instantiation
    \}

    /**
     * Creates a Pageable object with default sorting by creation date descending.
     *
     * @param pageNo   The page number (0-indexed).
     * @param pageSize The size of the page.
     * @return A Pageable object.
     */
    public static Pageable createPageable(int pageNo, int pageSize) \{
        return PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
    \}

    /**
     * Creates a Pageable object with custom sorting.
     *
     * @param pageNo   The page number (0-indexed).
     * @param pageSize The size of the page.
     * @param sortBy   The field to sort by.
     * @param sortDir  The sort direction ("asc" or "desc").
     * @return A Pageable object.
     */
    public static Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) \{
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    \}
\}
