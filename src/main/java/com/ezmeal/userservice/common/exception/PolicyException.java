package com.ezmeal.userservice.common.exception;

import com.ezmeal.common.exception.CustomException;
import com.ezmeal.userservice.common.exception.code.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PolicyException extends CustomException {

    public PolicyException(ResponseCode code) {super(code);}
}
