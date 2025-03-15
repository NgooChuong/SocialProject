package com.social.identityservice.mapper;

import com.social.identityservice.dto.request.ClientCreationRequest;
import com.social.identityservice.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface ClientMapper {
    ClientCreationRequest toClientCreationRequest(UserCreationRequest request);
}
