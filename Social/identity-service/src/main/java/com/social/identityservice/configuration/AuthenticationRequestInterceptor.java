package com.social.identityservice.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
// gui token chung voi lai các duong dan open feign khi gọi cac htpp yeu cau xac thuc
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    //cho phép can thiệp vào các yêu cầu HTTP trước khi chúng được gửi.
    @Override
    public void apply(RequestTemplate template) // template đại diện cho mẫu http gửi đi
    {
        // lấy ra các request hien tại thong qua RequestContextHolder
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // lấy value của header Authorization
        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");

        log.info("Header: {}", authHeader);
        // kiem tra  chuoi trong bien authheader hop le hay k
        if (StringUtils.hasText(authHeader))
            // gan them no vao template
            template.header("Authorization", authHeader);
    }
}
