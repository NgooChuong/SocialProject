package com.social.postService.mapper;

import com.social.postService.dto.response.UserReactionResponse;
import com.social.postService.entity.Reaction;
import com.social.postService.entity.UserPostInteraction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserPostReactionMapper {
    UserPostReactionMapper INSTANCE = Mappers.getMapper(UserPostReactionMapper.class);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "reaction.id", target = "id")
    @Mapping(source = "reaction", target = "reaction", qualifiedByName = "mapReactionToReactionString")
    @Mapping(source = "created_at", target = "created_at")
    @Mapping(source = "updated_at", target = "updated_at")
    UserReactionResponse toUserReaction (UserPostInteraction userPostInteraction);

    @Named("mapReactionToReactionString")
    static String mapReactionToReactionString(Reaction reaction) {
        return reaction.getIconName().toString();
    }
}
