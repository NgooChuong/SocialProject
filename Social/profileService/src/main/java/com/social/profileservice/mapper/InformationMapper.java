package com.social.profileservice.mapper;

import com.social.profileservice.dto.request.InformationCreateRequest;
import com.social.profileservice.dto.response.InformationResponse;
import com.social.profileservice.entity.Information;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InformationMapper {
    Information toInformation(InformationCreateRequest request);

    InformationResponse toInformationResponse(Information entity);

}
