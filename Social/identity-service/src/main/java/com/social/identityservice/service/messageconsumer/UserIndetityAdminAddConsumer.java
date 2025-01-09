package com.social.identityservice.service.messageconsumer;

import com.social.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class UserIndetityAdminAddConsumer {
    UserRepository userRepository;
//    FaceAuthenticationClient faceAuthenticationClient;

//    @RabbitListener(queues = "userIdentityAdminAddQueue")
//    public void UserReceive(User user) {
//        log.info("Received UserMessage: {}", user.getUsername());
//        log.info("Received UserMessage: {}", user.getUserid());
//        User u = userRepository.findByUserid(user.getUserid()).orElse(null);
//        if (u != null) {
//            u.setUsername(user.getUsername());
//            u.setPassword(user.getPassword());
//            u.setActive(user.getActive());
//            u.setRole(user.getRole());
//            u.setUserid(user.getUserid());
//            userRepository.save(u);
//        }
//        else{
//            userRepository.save(user);
//        }
//
//    }

//    @RabbitListener(queues = "userFaceAdminAddQueue")
//    public void UserFaceReceive(UserFaceRequest user) {
//        log.info("Received UserFaceMessage: {}", user.getUsername());
//        log.info("Received UserFaceMessage: {}", user.getUserid());
//        faceAuthenticationClient.SaveUser(user);
//    }
}
