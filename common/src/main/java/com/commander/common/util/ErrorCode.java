package com.commander.common.util;

import lombok.extern.log4j.Log4j2;
import com.commander.common.exception.BusinessErrorCode;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;

@Log4j2
public class ErrorCode {
    public static final BusinessErrorCode INTERNAL_SERVER_ERROR =
            new BusinessErrorCode(5000, "Internal server error", 500);
    public static final BusinessErrorCode INVALID_PARAMETERS =
            new BusinessErrorCode(4000, "Invalid parameters", 400);
    public static final BusinessErrorCode UNAUTHORIZED =
            new BusinessErrorCode(4001, "You need to login to to access this resource", 401);
    public static final BusinessErrorCode FORBIDDEN =
            new BusinessErrorCode(4002, "You don't have permission to to access this resource", 403);

    static {
        var codes = new HashSet<Integer>();
        var duplications = Arrays.stream(ErrorCode.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()) && f.getType().equals(BusinessErrorCode.class))
                .map(f -> {
                    try {
                        return ((BusinessErrorCode) f.get(null)).code();
                    } catch (IllegalAccessException e) {
                        log.error("Can't load error code into map", e);
                        throw new RuntimeException(e);
                    }
                })
                .filter(c -> !codes.add(c))
                .toList();
        if (!duplications.isEmpty()) {
            throw new RuntimeException("Found error code duplication: " + duplications);
        }
    }
}
