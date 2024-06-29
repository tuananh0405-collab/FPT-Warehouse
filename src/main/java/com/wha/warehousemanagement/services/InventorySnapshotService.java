package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.responses.InventorySnapshotResponse;
import com.wha.warehousemanagement.mappers.InventorySnapshotMapper;
import com.wha.warehousemanagement.models.Inventory;
import com.wha.warehousemanagement.models.InventorySnapshot;
import com.wha.warehousemanagement.repositories.InventoryRepository;
import com.wha.warehousemanagement.repositories.InventorySnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventorySnapshotService {
    private final InventorySnapshotRepository inventorySnapshotRepository;
    private final InventorySnapshotMapper inventorySnapshotMapper;
    private final InventoryRepository inventoryRepository;

    public List<InventorySnapshotResponse> getSnapshotsByMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1); // Đặt ngày là ngày đầu tiên của tháng
        Date startDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // Đặt ngày là ngày cuối cùng của tháng
        Date endDate = calendar.getTime();

        List<InventorySnapshot> snapshots = inventorySnapshotRepository.findBySnapshotDateBetween(startDate, endDate);
        return snapshots.stream()
                .map(inventorySnapshotMapper::toDto)
                .collect(Collectors.toList());
    }

    public void createMonthlySnapshot() {
        createSnapshotForMonth(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    public void createSnapshotForMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 28);
        Date snapshotDate = calendar.getTime();

        List<Inventory> inventories = inventoryRepository.findAll();
        List<InventorySnapshot> snapshots = inventories.stream()
                .map(inventory -> new InventorySnapshot(
                        null,
                        inventory.getProduct(),
                        inventory.getZone(),
                        snapshotDate,
                        inventory.getQuantity()
                ))
                .collect(Collectors.toList());

        inventorySnapshotRepository.saveAll(snapshots);
    }

    public void createSnapshotsForPeriod(int startYear, int startMonth, int endYear, int endMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(startYear, startMonth - 1, 1);
        Date startDate = calendar.getTime();

        calendar.set(endYear, endMonth - 1, 1);
        Date endDate = calendar.getTime();

        while (startDate.before(endDate) || startDate.equals(endDate)) {
            createSnapshotForMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
            calendar.add(Calendar.MONTH, 1);
            startDate = calendar.getTime();
        }
    }
}
