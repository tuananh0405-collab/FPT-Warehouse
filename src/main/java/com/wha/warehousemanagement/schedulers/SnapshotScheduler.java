package com.wha.warehousemanagement.schedulers;

import com.wha.warehousemanagement.services.InventorySnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SnapshotScheduler {
    private final InventorySnapshotService inventorySnapshotService;

    @Scheduled(cron = "0 0 0 28 * ?") // Chạy vào ngày 28 hàng tháng
    public void scheduleMonthlySnapshot() {
        inventorySnapshotService.createMonthlySnapshot();
    }
}
