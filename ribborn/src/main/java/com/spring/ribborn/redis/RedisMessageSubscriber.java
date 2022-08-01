package com.spring.ribborn.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.ribborn.websocket.chatDto.MessageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final RedisTemplate redisTemplate;
//    private final RedisTemplate<String,Object> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;


    /*
        redis에 메시지가 발행 될 때 까지 대기 하였다가 ,
        메시지가 발행되면 해당 메시지를 읽어 처리하는 리스너
        */
    @Override // 리스너를 상속받아 onMessage를 재정의
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!들어옴?" );
        //메시지를 받아와 ChatMessage로 변환 -> messaging Template를 이용하여 채팅방의 모든 웹소켓 클라이언트에게 전달.
        try {
            //레디스에서 발행된 데이터를 받아와 [역직렬화 ] -> 연속적인 데이터를 다시 객체 형태 복원
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!publishMessage = " + publishMessage);
            // MessageDto 객체로 매핑
            MessageRequestDto chatMessage = objectMapper.readValue(publishMessage, MessageRequestDto.class);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!chatMessage = " + chatMessage);
            // 웹 소켓 구독자에게 채팅 메시지 send
            messagingTemplate.convertAndSend("/sub/chat/room/"+chatMessage.getRoomId(), chatMessage);  // /sub/ewr2132dsfds  [메시지 ]
            messagingTemplate.convertAndSend("/sub/chat/rooms/"+chatMessage.getRoomId(), chatMessage);
            messagingTemplate.convertAndSend("/sub/"+chatMessage.getRoomId(), chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
