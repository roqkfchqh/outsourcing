package com.example.outsourcing.domain.common.exception;

import com.example.outsourcing.domain.common.exception.base.BaseException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;

public class InvalidRequestException extends BaseException {

    public InvalidRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getStatus());
    }
}
