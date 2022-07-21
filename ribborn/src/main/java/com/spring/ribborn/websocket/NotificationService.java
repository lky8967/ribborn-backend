//package com.spring.ribborn.websocket;
//
////import com.spring.ribborn.websocket.chatDto.NotificationDto;
//import com.spring.ribborn.exception.CustomException;
//import com.spring.ribborn.security.UserDetailsImpl;
//import com.spring.ribborn.websocket.chat.ChatRoomRepository;
////import com.spring.ribborn.security.UserDetailsImpl;
//import com.spring.ribborn.websocket.chatDto.NotificationDto;
//import lombok.RequiredArgsConstructor;
////import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
////import java.util.ArrayList;
////import java.util.List;
//
////import static com.spring.ribborn.websocket.chatDto.NotificationType.*;
//import java.util.List;
//
//import static com.spring.ribborn.exception.ErrorCode.*;
//
//@Service
//@RequiredArgsConstructor
//public class NotificationService {
//
//    private final NotificationRepository notificationRepository;
//    private final ChatRoomRepository roomRepository;
////    private final BarterRepository barterRepository;
////    private final ItemRepository itemRepository;
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
//    @Transactional
//    public void setRead(Long notificationId){
//        Notification notification = notificationRepository
//                .findById(notificationId)
//                .orElseThrow( () -> new CustomException(NOT_FOUND_NOTIFICATION));
//
//        notification.setRead();
//    }
//
//
//}