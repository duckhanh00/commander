package com.commander.common.exception;

public record BusinessErrorCode(int code, String message, int httpStatus) {
}
