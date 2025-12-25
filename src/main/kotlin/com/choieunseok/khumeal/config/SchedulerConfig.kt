package com.choieunseok.khumeal.config

import com.choieunseok.khumeal.service.MenuSyncService
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class SchedulerConfig(private val menuSyncService: MenuSyncService) {

    @Scheduled(cron = "0 0 * * * SAT,SUN")
    fun autoMenuSync() {
        menuSyncService.syncAllMenus()
    }
}