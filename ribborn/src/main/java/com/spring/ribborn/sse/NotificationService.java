package com.spring.ribborn.sse;


import com.spring.ribborn.exception.CustomException;
import com.spring.ribborn.exception.ErrorCode;
import com.spring.ribborn.security.UserDetailsImpl;
import com.spring.ribborn.model.User;
import lombok.RequiredArgsConstructor;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/*
    구독 -> Spring 에서 제공하는 Emitter를 생성 후 저장
    -> 구독자가 생성한 Emitter를 불러와 이벤트에 대한 응답을 전송
    -> 어떤 회원이 어떤 Emitter를 사용하느지에 대한 구분이 필요
 */

// No activity within 45000 milliseconds. 59 chars received. Reconnecting.
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private final NotificationRepository notificationRepository;


    public SseEmitter subscribe(Long userId, String lastEventId) {
        //emitter 하나하나 에 고유의 값을 주기 위해
        String emitterId = makeTimeIncludeId(userId);

        System.out.println("seseseseseseseseseseseseseseseseseseseiiiiiiiiiiiiiiiiiiiiid = " + emitterId);


        Long timeout = 60L * 1000L * 60L; // 1시간
        // 생성된 emiiterId를 기반으로 emitter를 저장
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(timeout));

        System.out.println("emitteremitteremitteremitteremitteremitteremitteremitteremitter = " + emitter);
        //emitter의 시간이 만료된 후 레포에서 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위해 처음 연결 진행 시 더미 데이터를 전달
        String eventId = makeTimeIncludeId(userId);

        System.out.println("eventIdeventIdeventIdeventIdeventIdeventIdeventIdeventIdeventId = " + eventId);
        // 수 많은 이벤트 들을 구분하기 위해 이벤트 ID에 시간을 통해 구분을 해줌
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userId, emitterId, emitter);
        }

        return emitter;
    }


    // SseEmitter를 구분 -> 구분자로 시간을 사용함 ,
    // 시간을 붙혀주는 이유 -> 브라우저에서 여러개의 구독을 진행 시
    //탭 마다 SssEmitter 구분을 위해 시간을 붙여 구분하기 위해 아래와 같이 진행
    private String makeTimeIncludeId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }

    // 유효시간이 다 지난다면 503 에러가 발생하기 때문에 더미데이터를 발행
    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }
    // Last - event - id 가 존재한다는 것은 받지 못한 데이터가 있다는 것이다.
    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    // 받지못한 데이터가 있다면 last - event - id를 기준으로 그 뒤의 데이터를 추출해 알림을 보내주면 된다.
    private void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }
    // =============================================
    /*
        : 실제 다른 사용자가 알림을 보낼 수 있는 기능이 필요
        알림을 구성 후 해당 알림에 대한 이벤트를 발생
        -> 어떤 회원에게 알림을 보낼지에 대해 찾고 알림을
        받을 회원의 emitter들을 모두 찾아 해당 emitter를 Send
     */

//    @Async
//    public void send(User receiver) {
//        System.out.println("receiver service   receiverreceiverreceiverreceiverreceiverreceiver = " + receiver);
//        Notification notification = notificationRepository.save(createNotification(receiver));
//
//        String receiverId = String.valueOf(receiver.getId());
//        String eventId = receiverId + "_" + System.currentTimeMillis();
//        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);
//        emitters.forEach(
//                (key, emitter) -> {
//                    emitterRepository.saveEventCache(key, notification);
//                    sendNotification(emitter, eventId, key, NotificationDto.create(notification));
//                }
//        );
//    }

    @Async
    public void send(User receiver){

        Notification notification = notificationRepository.save(createNotification(receiver));
        System.out.println("notificationnotificationnotificationnotificationnotificationnotificationnotificationnotificationnotification = " + notification);
        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();
        System.out.println("eventIdeventIdeventIdeventIdeventIdeventIdeventIdeventIdeventIdeventIdeventIdeventIdeventIdeventIdeventId = " + eventId);
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationDto.create(notification));
                }
        );
    }

    private Notification createNotification(User receiver) {
        return Notification.builder()
                .receiver(receiver)
//                .notificationType(notificationType)
//                .content(content)
//                .url(url)
                .isRead(false) // 현재 읽음상태
                .build();
    }

    @Transactional
    public List<NotificationDto> findAllNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserId(userId);
        return notifications.stream()
                .map(NotificationDto::create)
                .collect(Collectors.toList());
    }


    public NotificationCountDto countUnReadNotifications(Long userId) {
        //유저의 알람리스트에서 ->isRead(false)인 갯수를 측정 ,
        Long count = notificationRepository.countUnReadNotifications(userId);
        return NotificationCountDto.builder()
                .count(count)
                .build();
    }

    @Transactional
    public void readNotification(Long notificationId) {
        //알림을 받은 사람의 id 와 알림의 id 를 받아와서 해당 알림을 찾는다.
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        Notification checkNotification = notification.orElseThrow(()-> new CustomException(ErrorCode.NOT_EXIST_NOTIFICATION));
        checkNotification.read(); // 읽음처리

    }

    @Transactional
    public void deleteAllByNotifications(UserDetailsImpl userDetails) {
        Long receiverId = userDetails.getUser().getId();
        notificationRepository.deleteAllByReceiverId(receiverId);

    }
    @Transactional
    public void deleteByNotifications(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}

//
////import com.spring.ribborn.websocket.chatDto.NotificationDto;
//import com.spring.ribborn.websocket.chatroom.ChatRoomRepository;
////import com.spring.ribborn.security.UserDetailsImpl;
//import lombok.RequiredArgsConstructor;
////import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
////import java.util.ArrayList;
////import java.util.List;
//
////import static com.spring.ribborn.websocket.chatDto.NotificationType.*;
//import java.io.IOException;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//
//public class NotificationService {
//
//    private static final Long DEFAULT_TIMEOUT = 60L * 1000;
//
//    private final NotificationRepository notificationRepository;
//    private final ChatRoomRepository roomRepository;
//    private final EmitterRepository emitterRepository;
//
//    public SseEmitter subscribe(Long userId, String lastEventId) {
//        // 1
//        String id = userId + "_" + System.currentTimeMillis();
//        System.out.println("System.currentTimeMillis()System.currentTimeMillis()System.currentTimeMillis() = " + System.currentTimeMillis());
//        System.out.println("serviceserviceserviceserviceserviceserviceserviceserviceserviceserviceserviceserviceserviceservice id = " + id);
//
//        // 2
////        SseEmitter emitter = emitterRepository.save(id, new SseEmitter());
//        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
//        SseDto sseDto = new SseDto();
//        System.out.println("sseDto = " + sseDto);
//
//        System.out.println("emitteremitteremitteremitteremitteremitteremitteremitteremitteremitteremitteremitteremitteremitteremitter emitter = " + emitter);
//        emitter.onCompletion(() -> emitterRepository.deleteById(id));
//        emitter.onTimeout(() -> emitterRepository.deleteById(id));
//
//        // 3
//        // 503 에러를 방지하기 위한 더미 이벤트 전송
//        sendToClient(emitter, id, "EventStream Created. [userId=" + userId + "]");
//        System.out.println(" 더미 데이터 전송 ");
//        // 4
//        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
//        // 현재 이쪽 에러
//
//        if (!lastEventId.isEmpty()) {
//            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
//            System.out.println("eventseventseventseventseventseventseventseventseventseventseventseventseventseventseventseventsevents = " + events);
//            events.entrySet().stream()
//                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
//                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
////                    .forEach(entry -> sendToClient(entry.getKey(), entry.getValue()));
//            System.out.println("클라이언트가 미수신 목록이 존재할 경우 재전송");
//        }
//
//        System.out.println(" 여긴 마지막 리턴전인데 오니?? " + emitter );
//        return emitter ;
//    }
//
////    private void sendToClient( String id, Object data) {
////    @Async
//
//    public void sendToClient(SseEmitter emitter, String id, Object data) {
//        System.out.println("센트투클라이언트 진입");
//        try {
//            emitter.send(SseEmitter.event()
//                    .id(id)
//                    .name("sse")
//                    .data(data));
//            System.out.println("클라이언트에게 전송");
//        } catch (IOException exception) {
//            emitterRepository.deleteById(id);
//            throw new RuntimeException("연결 오류!");
//        }
//    }
//
//
//
//
//    // 알림 전체 목록
////    public List<NotificationDto> getNotification(UserDetailsImpl userDetails) {
////
////        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByIdDesc(userDetails.getUserId());
////        List<NotificationDto> dtos = new ArrayList<>();
////        for (Notification notification : notifications){
////            switch ( notification.getType() ){
////                case CHAT:
////                    ChatRoom chatRoom = roomRepository.findByIdFetch(notification.getChangeId())
////                            .orElse( null );
////                    if ( chatRoom != null ) {
////                        if ( chatRoom.getAcceptor().getId().equals(userDetails.getUserId()) ) {
////                            dtos.add(NotificationDto.createOf(notification, chatRoom.getRequester()));
////                        } else if ( chatRoom.getRequester().getId().equals(userDetails.getUserId()) ) {
////                            dtos.add(NotificationDto.createOf(notification, chatRoom.getAcceptor()));
////                        }
////                    }
////                    break;
////                case BARTER:
////                    Barter barter = barterRepository.findById(notification.getChangeId())
////                            .orElse(null);
////                    if ( barter != null ) {
////
////                        String[] barterIds = barter.getBarter().split(";");
////                        String[] buyerItemIdList = barterIds[0].split(",");
////
////                        Long[] ids = new Long[buyerItemIdList.length];
////                        for ( int i = 0 ; i < buyerItemIdList.length ; i ++ ){
////                            ids[i] = Long.parseLong(buyerItemIdList[i]);
////                        }
////
////                        List<Item> items = itemRepository.findAllByItemIds(ids);
////                        List<ItemStarDto> starDtos = new ArrayList<>();
////
////                        for ( Item item : items ){
////                            starDtos.add(ItemStarDto.createFrom(item));
////                        }
////                        dtos.add(NotificationDto.createFrom(notification, starDtos));
////                    }
////                    break;
////
////                default: dtos.add(NotificationDto.createFrom(notification)); break;
////            }
////        }
////        return dtos;
////        }
//
//    // 읽음 상태 업데이트
////    @Transactional
////    public void setRead(Long notificationId){
////        Notification notification = notificationRepository
////                .findById(notificationId)
////                .orElseThrow( () -> new CustomException(NOT_FOUND_NOTIFICATION));
////
////        notification.setRead();
////    }
//
//}