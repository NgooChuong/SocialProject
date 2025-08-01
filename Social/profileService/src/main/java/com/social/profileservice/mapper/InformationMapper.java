package com.social.profileservice.mapper;

import com.social.profileservice.dto.request.InformationCreateRequest;
import com.social.profileservice.dto.response.InformationResponse;
import com.social.profileservice.entity.Information;
import com.social.profileservice.entity.UserInterest;
import com.social.profileservice.enums.InterestType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InformationMapper {
    Information toInformation(InformationCreateRequest request);
    @Mapping(source = "userInterests", target = "interests", qualifiedByName = "mapInterestNames")
    InformationResponse toInformationResponse(Information entity);
    @Named("mapInterestNames")
    static List<InterestType> mapUserInterests(Set<UserInterest> userInterests) {
        if (userInterests == null) return Collections.emptyList();
        return userInterests.stream()
                .map(ui -> ui.getInterest().getName())
                .collect(Collectors.toList());
    }
}
