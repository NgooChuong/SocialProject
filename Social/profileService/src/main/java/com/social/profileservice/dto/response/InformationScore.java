package com.social.profileservice.dto.response;

import com.social.profileservice.entity.Information;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InformationScore {
    Information information;
    double score;
}
