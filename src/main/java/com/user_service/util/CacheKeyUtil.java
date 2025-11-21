package com.user_service.util;

public class CacheKeyUtil {

    public static String searchKey(String bloodGroup, String location, int page, int size) {
        return String.format("search:%s:%s:%d:%d",
                bloodGroup,
                location == null ? "null" : location,
                page,
                size);
    }
}

