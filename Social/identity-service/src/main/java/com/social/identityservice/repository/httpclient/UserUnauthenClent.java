package com.social.identityservice.repository.httpclient;


import com.social.identityservice.dto.request.UserCreationRequest;
import com.social.identityservice.dto.response.InformationResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="UnAuthUserQuanLyBaiDoXe", url = "${app.services.common}")
public interface UserUnauthenClent {
    @PostMapping(value = "/internal/users",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    InformationResponse createUser(@ModelAttribute @Valid UserCreationRequest request);

    @PostMapping(value = "/internal/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    String GetUsername(@RequestBody String email);
}
