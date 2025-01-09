//package com.qlbx.identityservice.service.messageconsumer;
//
//import com.qlbx.identityservice.dto.request.message.UseridMessage;
//import com.qlbx.identityservice.entity.User;
//import com.qlbx.identityservice.repository.UserRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//@Service
//public class UserIdMessageConsumer {
//    UserRepository userRepository;
//
//    @RabbitListener(queues = "userIdQueue")
//    public void UserIdReceive(UseridMessage useridMessage) {
//        User u = userRepository.findByUsername(useridMessage.getEmail()).orElse(null);
//        if (u != null) {
//            u.setUserid(useridMessage.getUserid());
//            userRepository.save(u);
//        }
//    }
//}
