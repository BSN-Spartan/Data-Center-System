package com.spartan.dc.core.exception;

import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;



@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    private Object exceptionHandler(Exception e) {
        ResultInfo resultInfo = null;
        if (e instanceof GlobalException) {
            GlobalException globalException = (GlobalException) e;
            resultInfo = ResultInfoUtil.errorResult(globalException.getCode(), globalException.getMessage());

        } else {
            resultInfo = ResultInfoUtil.errorResult(e.getMessage());
        }
        logger.error("response = 【{}】", resultInfo == null ? "result null" : resultInfo.toString());
        return resultInfo;
    }


    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResultInfo validatedBindException(BindException e) {
        List<ObjectError> fieldError = e.getAllErrors();
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (ObjectError error : fieldError) {
            sb.append(error.getDefaultMessage() + " | ");
        }
        sb.deleteCharAt(sb.toString().length() - 2);
        sb.append(" ]");
        return ResultInfoUtil.errorResult(sb.toString());
    }

}
