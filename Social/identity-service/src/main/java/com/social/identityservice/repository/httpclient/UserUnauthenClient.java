package com.social.identityservice.repository.httpclient;
import com.social.identityservice.dto.request.ClientCreationRequest;
import com.social.identityservice.dto.response.InformationResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@LoadBalancerClient(name="PROFILE")
@FeignClient(name="PROFILE", path = "/profile")
public interface UserUnauthenClient {
    @PostMapping(value = "/users",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    InformationResponse createUser(@ModelAttribute @Valid ClientCreationRequest request);
}
