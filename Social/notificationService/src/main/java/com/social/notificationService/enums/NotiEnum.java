package com.social.notificationService.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum NotiEnum {

    FriendEnum("Đây là một lời mời kết bạn", "Một người đã gửi lời mời kết bạn chọ bạn"),
    FriendAcceptedEnum("Kết bạn thành công", "Hai người đã trở thành bạn bè"),
    PostEnum("Đây là một thông báo đăng bài", "Một người bạn của bạn đã đăng bài, hãy tương tác");

    NotiEnum(String title, String content) {
        this.title = title;
        this.content = content;
    }

    String title;
    String content;
}
