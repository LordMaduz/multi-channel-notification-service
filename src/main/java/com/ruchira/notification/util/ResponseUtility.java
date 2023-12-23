package com.ruchira.notification.util;

import com.ruchira.notification.vo.ResponseVo;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtility {

    public <T> ResponseVo<T> successResponse(final String message, final T result ) {
        final ResponseVo<T> responseVo = new ResponseVo<>();
        responseVo.setResult(result);
        responseVo.setMessage(message);

        return responseVo;
    }

    public <T> ResponseVo<T> failureResponse(final String error) {
        final ResponseVo<T> responseVo = new ResponseVo<>();
        responseVo.setError(error);

        return responseVo;
    }
}
