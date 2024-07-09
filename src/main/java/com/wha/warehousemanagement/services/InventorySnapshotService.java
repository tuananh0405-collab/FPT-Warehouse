package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.responses.InventorySnapshotResponse;
import com.wha.warehousemanagement.dtos.responses.ZoneResponse;
import com.wha.warehousemanagement.mappers.InventorySnapshotMapper;
import com.wha.warehousemanagement.models.Inventory;
import com.wha.warehousemanagement.models.InventorySnapshot;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.InventoryRepository;
import com.wha.warehousemanagement.repositories.InventorySnapshotRepository;
import com.wha.warehousemanagement.services.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventorySnapshotService {
    private final InventorySnapshotRepository inventorySnapshotRepository;
    private final InventorySnapshotMapper inventorySnapshotMapper;
    private final InventoryRepository inventoryRepository;
    private final ZoneService zoneService;

    public List<InventorySnapshotResponse> getSnapshotsByMonth(int year, int month, int warehouseId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        Date startDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = calendar.getTime();

        List<Integer> zoneIds = getZoneIdsByWarehouseId(warehouseId);

        List<InventorySnapshot> snapshots = inventorySnapshotRepository.findBySnapshotDateBetweenAndZoneIdIn(startDate, endDate, zoneIds);
        return snapshots.stream()
                .map(inventorySnapshotMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllWarehouseSnapshots() {
        List<Map<String, Object>> reportData = new ArrayList<>();
        List<InventorySnapshot> snapshots = inventorySnapshotRepository.findAll();

        for (InventorySnapshot snapshot : snapshots) {
            Map<String, Object> data = new HashMap<>();
            data.put("warehouse", snapshot.getZone().getWarehouse().getName());
            data.put("product", snapshot.getProduct().getName());
            data.put("zone", snapshot.getZone().getName());
            data.put("quantity", snapshot.getQuantity());
            data.put("snapshotDate", formatDate(snapshot.getSnapshotDate()));
            reportData.add(data);
        }

        return reportData;
    }

    public List<Map<String, Object>> getMultiMonthSnapshots(int startYear, int startMonth, int endYear, int endMonth, int warehouseId) {
        List<Map<String, Object>> reportData = new ArrayList<>();

        Calendar startCal = Calendar.getInstance();
        startCal.set(startYear, startMonth - 1, 1);

        Calendar endCal = Calendar.getInstance();
        endCal.set(endYear, endMonth - 1, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        List<Integer> zoneIds = getZoneIdsByWarehouseId(warehouseId);

        while (!startCal.after(endCal)) {
            int year = startCal.get(Calendar.YEAR);
            int month = startCal.get(Calendar.MONTH) + 1;

            Date startDate = startCal.getTime();
            startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = startCal.getTime();
            startCal.set(Calendar.DAY_OF_MONTH, 1);

            List<InventorySnapshot> snapshots = inventorySnapshotRepository.findBySnapshotDateBetweenAndZoneIdIn(
                    startDate,
                    endDate,
                    zoneIds
            );

            for (InventorySnapshot snapshot : snapshots) {
                Map<String, Object> data = new HashMap<>();
                data.put("year", year);
                data.put("month", month);
                data.put("product", snapshot.getProduct().getName());
                data.put("zone", snapshot.getZone().getName());
                data.put("quantity", snapshot.getQuantity());
                data.put("snapshotDate", formatDate(snapshot.getSnapshotDate()));
                reportData.add(data);
            }

            startCal.add(Calendar.MONTH, 1);
        }

        return reportData;
    }

    public void createMonthlySnapshot() {
        createSnapshotForMonth(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1);
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

    public void createSnapshotForMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
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

    private List<Integer> getZoneIdsByWarehouseId(int warehouseId) {
        ResponseObject<List<ZoneResponse>> responseObject = (ResponseObject<List<ZoneResponse>>) zoneService.getZonesByWarehouseId(warehouseId);
        List<ZoneResponse> zoneResponses = responseObject.getData();
        if (zoneResponses == null) {
            throw new RuntimeException("Failed to fetch zones for warehouse ID: " + warehouseId);
        }
        return zoneResponses.stream()
                .map(ZoneResponse::getId)
                .collect(Collectors.toList());
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }
}
