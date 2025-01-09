package com.social.identityservice.repository.httpclient;

import com.social.identityservice.configuration.AuthenticationRequestInterceptor;
import com.social.identityservice.dto.request.UserUpdateRequest;
import com.social.identityservice.dto.response.InformationResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name="UserQuanLyBaiDoXe", url = "${app.services.common}",
        configuration = { AuthenticationRequestInterceptor.class })
public interface UserClient {
    @PutMapping(value = "/internal/users/{user_id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    InformationResponse updateUser(@PathVariable String user_id, @ModelAttribute @Valid UserUpdateRequest request);
}
