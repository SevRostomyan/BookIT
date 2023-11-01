package com.bookit.bookit.repository.notifications;

import com.bookit.bookit.entity.notifications.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications,Integer> {

}
