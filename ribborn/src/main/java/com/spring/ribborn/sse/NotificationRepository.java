package com.spring.ribborn.sse;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    @Query("select n from Notification n where n.receiver.id = :userId order by n.id desc")
    List<Notification> findAllByUserId(@Param("userId") Long userId);

    @Query("select count(n) from Notification n where n.receiver.id = :userId and n.isRead = false")
    Long countUnReadNotifications(@Param("userId") Long userId);

    Optional<Notification> findById(Long NotificationsId);

    void deleteAllByReceiverId(Long receiverId);
    void deleteById(Long notificationId);
}
//
//import io.lettuce.core.dynamic.annotation.Param;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
////public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationQuerydsl {
//@Repository
//public interface NotificationRepository extends JpaRepository<Notification, Long>{
//
//    List<Notification> findAllByUserIdOrderByIdDesc(Long userId);
//
//    int countNotificationByUserIdAndIsReadIsFalse(String username);
//
//
//
////    @Query("select n from Notification n where n.receiver.id = :userId order by n.id desc")
////    List<Notification> findAllByUserId(@Param("userId") Long userId);
////
////    @Query("select count(n) from Notification n where n.receiver.id = :userId and n.isRead = false")
////    Long countUnReadNotifications(@Param("userId") Long userId);
////
////    Optional<Notification> findById(Long NotificationsId);
////
////    void deleteAllByReceiverId(Long receiverId);
////    void deleteById(Long notificationId);
//
//
//}
