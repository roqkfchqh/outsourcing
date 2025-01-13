package com.example.outsourcing.domain.common.exception;

import com.example.outsourcing.domain.common.exception.base.BaseException;
import com.example.outsourcing.domain.common.exception.base.ErrorCode;

public class ServerException extends BaseException {

    public ServerException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getStatus());
    }
}
