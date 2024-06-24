import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { Table, Button, Modal, Select, InputNumber, message } from "antd";
import { PlusOutlined } from "@ant-design/icons";
import { useGetAllExportsQuery } from "../../redux/api/exportApiSlice";
import { useGetAllCustomersQuery } from "../../redux/api/customersApiSlice";
import { useGetAllExportDetailsQuery } from "../../redux/api/exportDetailApiSlice";
import { useGetZoneByWarehouseIdQuery } from "../../redux/api/zoneApiSlice";
import { useAddImportMutation } from "../../redux/api/importApiSlice.js";
import { useCreateImportDetailsMutation } from "../../redux/api/importDetailApiSlice";
import Loading from "../../utils/Loading";
import Error500 from "../../utils/Error500";

const { Option } = Select;

const StaffImport = () => {
  const userInfo = useSelector((state) => state.auth);
  const authToken = userInfo?.userInfo?.data?.token;
  const currentWarehouseId = userInfo?.userInfo?.data?.warehouseId;
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [sortOrder, setSortOrder] = useState({
    columnKey: null,
    order: null,
  });
  const [filteredData, setFilteredData] = useState([]);
  const [paginatedData, setPaginatedData] = useState([]);
  const [total, setTotal] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportModalOpen, setIsImportModalOpen] = useState(false);
  const [selectedExportDetails, setSelectedExportDetails] = useState([]);
  const [zoneAllocations, setZoneAllocations] = useState([]);
  const [formData, setFormData] = useState({
    description: "",
    status: "PENDING",
    exportDate: "",
    exportType: "",
    warehouseIdFrom: null,
    warehouseIdTo: currentWarehouseId,
    customerId: null,
  });

  // const [addImport, { isLoading: isAddingImport }] = useAddImportMutation();
  // const [createImportDetailsApi, { isLoading: isCreatingImportDetails }] =
  //   useCreateImportDetailsMutation();
  const [addImport, { isLoading: isImportCreating }] = useAddImportMutation();
  const [createImportDetails, { isLoading: isImportDetailsCreating }] =
    useCreateImportDetailsMutation();
  const {
    data: exportsData,
    isFetching: isFetchingExports,
    error: exportsError,
  } = useGetAllExportsQuery(authToken);

  const {
    data: customersData,
    isFetching: isFetchingCustomers,
    error: customersError,
  } = useGetAllCustomersQuery(authToken);

  const {
    data: exportDetailsData,
    isFetching: isFetchingExportDetails,
    error: exportDetailsError,
  } = useGetAllExportDetailsQuery(authToken);

  const {
    data: zonesData,
    isFetching: isFetchingZones,
    error: zonesError,
  } = useGetZoneByWarehouseIdQuery({ id: currentWarehouseId, authToken });

  useEffect(() => {
    if (
      exportsData &&
      customersData &&
      exportDetailsData &&
      currentWarehouseId
    ) {
      const exports =
        exportsData.data?.filter(
          (exp) => exp.warehouseTo && exp.warehouseTo.id === currentWarehouseId
        ) || [];
      const customers = customersData.data || [];

      const exportsWithCustomerNames = exports.map((exp) => {
        const customer = customers.find((cust) => cust.id === exp.customer.id);
        return {
          ...exp,
          customerName: customer ? customer.name : "Unknown",
        };
      });

      setFilteredData(exportsWithCustomerNames);
      setTotal(exportsWithCustomerNames.length);
    }
  }, [exportsData, customersData, exportDetailsData, currentWarehouseId]);

  useEffect(() => {
    const start = (currentPage - 1) * pageSize;
    const end = start + pageSize;
    let sortedData = [...filteredData];

    if (sortOrder.columnKey) {
      sortedData.sort((a, b) => {
        if (a[sortOrder.columnKey] < b[sortOrder.columnKey])
          return sortOrder.order === "ascend" ? -1 : 1;
        if (a[sortOrder.columnKey] > b[sortOrder.columnKey])
          return sortOrder.order === "ascend" ? 1 : -1;
        return 0;
      });
    }

    setPaginatedData(sortedData.slice(start, end));
  }, [filteredData, currentPage, pageSize, sortOrder]);

  const handleTableChange = (pagination, filters, sorter) => {
    setSortOrder({
      columnKey: sorter.field,
      order: sorter.order,
    });
    setCurrentPage(pagination.current);
    setPageSize(pagination.pageSize);
  };

  const handleViewDetail = (exportId) => {
    const exportDetails = exportDetailsData.data.filter(
      (detail) => detail.export.id === exportId
    );
    const exportData = exportsData.data.find((exp) => exp.id === exportId);
    setSelectedExportDetails(exportDetails);
    setFormData({
      description: exportData.description || "Xuất hàng điện tử 2",
      status: exportData.status || "SUCCEED",
      importType: exportData.exportType || "WAREHOUSE",
      transferKey: exportData.transferKey || "",
      warehouseIdFrom: exportData.warehouseFrom?.id || null,
      warehouseIdTo: currentWarehouseId, // lấy từ state của userInfo
      customerId: exportData.customer.id, // lấy từ đối tượng export được chọn
    });
    setZoneAllocations(
      exportDetails.map((detail) => ({
        productId: detail.product.id,
        zones: [{ zoneId: null, quantity: 0 }],
      }))
    );
    setIsModalOpen(true);
  };

  const handleZoneAllocationChange = (productId, index, field, value) => {
    setZoneAllocations((prevAllocations) =>
      prevAllocations.map((allocation) =>
        allocation.productId === productId
          ? {
              ...allocation,
              zones: allocation.zones.map((zone, zoneIndex) =>
                zoneIndex === index ? { ...zone, [field]: value } : zone
              ),
            }
          : allocation
      )
    );
  };

  const handleAddZone = (productId) => {
    setZoneAllocations((prevAllocations) =>
      prevAllocations.map((allocation) =>
        allocation.productId === productId
          ? {
              ...allocation,
              zones: [...allocation.zones, { zoneId: null, quantity: 0 }],
            }
          : allocation
      )
    );
  };

  const validateZoneAllocations = () => {
    for (let detail of selectedExportDetails) {
      const allocation = zoneAllocations.find(
        (allocation) => allocation.productId === detail.product.id
      );
      const totalAllocated = allocation.zones.reduce(
        (total, zone) => total + zone.quantity,
        0
      );
      if (totalAllocated !== detail.quantity) {
        message.error(
          `Total allocated quantity for product ${detail.product.name} does not match its quantity.`
        );
        return false;
      }
    }
    return true;
  };

  const handleCreateImport = async () => {
    try {
      let importData = {
        description: formData.description,
        status: formData.status,
        importType: formData.importType,
        transferKey: formData.transferKey,
        warehouseIdFrom: formData.warehouseIdFrom,
        warehouseIdTo: formData.warehouseIdTo,
        customerId: formData.customerId,
      };

      console.log("Import data to be sent:", importData);
      const response = await addImport({
        data: importData,
        authToken,
      }).unwrap();

      if (response && response.data && response.data.id) {
        message.success("Import created successfully!");
        console.log("Import data:", response.data);
        const importId = response.data.id;
        await handleCreateImportDetails(importId);
      } else {
        throw new Error("Invalid response from server");
      }
    } catch (error) {
      console.error("Error creating import:", error);
      message.error("Failed to create import. Please try again.");
    }
  };

  const handleCreateImportDetails = async (importId = null) => {
    try {
      const importDetails = zoneAllocations
        .map((allocation) =>
          allocation.zones.map((zone) => ({
            productId: allocation.productId,
            importId: importId,
            quantity: zone.quantity,
            zoneId: zone.zoneId,
            expiredAt: selectedExportDetails.find(
              (detail) => detail.product.id === allocation.productId
            ).expiredAt,
          }))
        )
        .flat();

      console.log("Import details to be sent:", importDetails);
      const detailsResponse = await createImportDetails({
        data: importDetails,
        authToken,
      }).unwrap();
      console.log("Import details created:", detailsResponse);
      message.success("Import details created successfully!");
      setSelectedExportDetails([]);
    } catch (error) {
      console.error("Error creating import details:", error);
      message.error("Failed to create import details. Please try again.");
    }
  };

  const handleImport = async () => {
    if (validateZoneAllocations()) {
      await handleCreateImport();
    }
  };

  const columns = [
    {
      title: "Export ID",
      dataIndex: "id",
      key: "id",
      sorter: true,
    },
    {
      title: "Description",
      dataIndex: "description",
      key: "description",
      sorter: true,
    },
    {
      title: "Export Date",
      dataIndex: "exportDate",
      key: "exportDate",
      sorter: true,
    },
    {
      title: "Customer Name",
      dataIndex: "customerName",
      key: "customerName",
      sorter: true,
    },
    {
      title: "View Detail",
      key: "viewDetail",
      render: (text, record) => (
        <Button onClick={() => handleViewDetail(record.id)}>View Detail</Button>
      ),
    },
  ];

  if (
    isFetchingExports ||
    isFetchingCustomers ||
    isFetchingExportDetails ||
    isFetchingZones
    // isAddingImport ||
    // isCreatingImportDetails
  )
    return <Loading />;
  if (exportsError || customersError || exportDetailsError || zonesError)
    return <Error500 />;

  return (
    <>
      <h1>Staff Import</h1>
      <Table
        dataSource={paginatedData}
        columns={columns}
        rowKey="id"
        pagination={{ current: currentPage, pageSize, total }}
        onChange={handleTableChange}
      />
      <Modal
        title="Export Details"
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        footer={[
          <Button key="close" onClick={() => setIsModalOpen(false)}>
            Close
          </Button>,
          <Button
            key="import"
            type="primary"
            onClick={() => setIsImportModalOpen(true)}
          >
            Import
          </Button>,
        ]}
      >
        <Table
          dataSource={selectedExportDetails}
          columns={[
            {
              title: "Product Name",
              dataIndex: ["product", "name"],
              key: "productName",
            },
            { title: "Quantity", dataIndex: "quantity", key: "quantity" },
            { title: "Expired At", dataIndex: "expiredAt", key: "expiredAt" },
          ]}
          rowKey="id"
          pagination={false}
          expandable={{
            expandedRowRender: (record) => (
              <>
                {zoneAllocations
                  .find(
                    (allocation) => allocation.productId === record.product.id
                  )
                  .zones.map((zone, index) => (
                    <div
                      key={index}
                      style={{ display: "flex", marginBottom: "8px" }}
                    >
                      <Select
                        style={{ width: "60%" }}
                        placeholder="Select a zone"
                        onChange={(value) =>
                          handleZoneAllocationChange(
                            record.product.id,
                            index,
                            "zoneId",
                            value
                          )
                        }
                      >
                        {zonesData.data.map((zone) => (
                          <Option key={zone.id} value={zone.id}>
                            {zone.name}
                          </Option>
                        ))}
                      </Select>
                      <InputNumber
                        min={0}
                        max={record.quantity}
                        value={zone.quantity}
                        onChange={(value) =>
                          handleZoneAllocationChange(
                            record.product.id,
                            index,
                            "quantity",
                            value
                          )
                        }
                        style={{ marginLeft: "8px", width: "30%" }}
                      />
                    </div>
                  ))}
                <Button
                  icon={<PlusOutlined />}
                  onClick={() => handleAddZone(record.product.id)}
                >
                  Add Zone
                </Button>
              </>
            ),
          }}
        />
      </Modal>
      <Modal
        title="Import Products"
        open={isImportModalOpen}
        onCancel={() => setIsImportModalOpen(false)}
        footer={[
          <Button key="cancel" onClick={() => setIsImportModalOpen(false)}>
            Cancel
          </Button>,
          <Button key="submit" type="primary" onClick={handleImport}>
            Submit
          </Button>,
        ]}
        width={800} // Increase the width of the modal
      >
        <div>
          <h3>Products</h3>
          <Table
            dataSource={selectedExportDetails}
            columns={[
              {
                title: "Product Name",
                dataIndex: ["product", "name"],
                key: "productName",
              },
              { title: "Quantity", dataIndex: "quantity", key: "quantity" },
              {
                title: "Expired At",
                dataIndex: "expiredAt",
                key: "expiredAt",
              },
              {
                title: "Zones",
                key: "zones",
                render: (text, record) => (
                  <>
                    {zoneAllocations
                      .find(
                        (allocation) =>
                          allocation.productId === record.product.id
                      )
                      .zones.map((zone, index) => (
                        <div
                          key={index}
                          style={{ display: "flex", marginBottom: "8px" }}
                        >
                          <Select
                            style={{ width: "60%" }}
                            placeholder="Select a zone"
                            onChange={(value) =>
                              handleZoneAllocationChange(
                                record.product.id,
                                index,
                                "zoneId",
                                value
                              )
                            }
                          >
                            {zonesData.data.map((zone) => (
                              <Option key={zone.id} value={zone.id}>
                                {zone.name}
                              </Option>
                            ))}
                          </Select>
                          <InputNumber
                            min={0}
                            max={record.quantity}
                            value={zone.quantity}
                            onChange={(value) =>
                              handleZoneAllocationChange(
                                record.product.id,
                                index,
                                "quantity",
                                value
                              )
                            }
                            style={{ marginLeft: "8px", width: "30%" }}
                          />
                        </div>
                      ))}
                    <Button
                      icon={<PlusOutlined />}
                      onClick={() => handleAddZone(record.product.id)}
                    >
                      Add Zone
                    </Button>
                  </>
                ),
              },
            ]}
            rowKey="id"
            pagination={false}
          />
        </div>
      </Modal>
    </>
  );
};

export default StaffImport;
