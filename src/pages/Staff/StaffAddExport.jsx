import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Form,
  Input,
  Select,
  Button,
  Row,
  Col,
  message,
  Table,
  Radio,
  Checkbox,
  Pagination,
} from "antd";
import Breadcrumbs from "../../utils/Breadcumbs";
import Loading from "../../utils/Loading";
import Error500 from "../../utils/Error500";
import { useGetAllWarehousesQuery } from "../../redux/api/warehousesApiSlice";
import { useGetAllZonesQuery } from "../../redux/api/zoneApiSlice";
import { useGetAllInventoriesQuery } from "../../redux/api/inventoryApiSlice";
import { useGetAllProductsQuery } from "../../redux/api/productApiSlice";
import { useGetAllCustomersQuery } from "../../redux/api/customersApiSlice";
import { useSelector } from "react-redux";
import { useAddExportMutation } from "../../redux/api/exportApiSlice";
import { useAddCustomerMutation } from "../../redux/api/customersApiSlice";
import { useCreateExportDetailsMutation } from "../../redux/api/exportDetailApiSlice";

const { Option } = Select;

const StaffAddExport = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const userInfo = useSelector((state) => state.auth);
  const authToken = userInfo.userInfo.data.token;
  const [addExport, { isLoading: isExportCreating }] = useAddExportMutation();
  const [addCustomer, { isLoading: isCustomerCreating }] =
    useAddCustomerMutation();
  const [createExportDetails, { isLoading: isExportDetailsCreating }] =
    useCreateExportDetailsMutation();

  const {
    data: warehouses,
    isFetching: isWarehouseLoading,
    error: warehouseError,
  } = useGetAllWarehousesQuery(authToken);
  const {
    data: zones,
    isFetching: isZoneLoading,
    error: zoneError,
  } = useGetAllZonesQuery(authToken);
  const {
    data: products,
    isFetching: isProductLoading,
    error: productError,
  } = useGetAllProductsQuery(authToken);
  const {
    data: inventories,
    isFetching: isInventoryLoading,
    error: inventoryError,
  } = useGetAllInventoriesQuery(authToken);
  const {
    data: customers,
    isFetching: isCustomerLoading,
    error: customerError,
  } = useGetAllCustomersQuery(authToken);

  const [isPopupVisible, setIsPopupVisible] = useState(false);
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [filter, setFilter] = useState("all");
  const [zone, setZone] = useState(null);
  const [isVisible, setIsVisible] = useState(false);
  const [sortOrder, setSortOrder] = useState({
    columnKey: null,
    order: null,
  });
  const [searchText, setSearchText] = useState("");
  const [filterType, setFilterType] = useState("all");
  const [currentPage, setCurrentPage] = useState(1);

  const warehousesData = warehouses?.data || [];
  const zonesData = zones?.data || [];
  const productsData = products?.data || [];
  const inventoriesData = inventories?.data || [];
  const customersData = customers?.data || [];

  const [formData, setFormData] = useState({
    description: "",
    status: "PENDING",
    exportDate: "",
    exportType: "",
    warehouseIdFrom: userInfo.userInfo.data.warehouseId || null,
    warehouseIdTo: null,
    customerId: null,
  });

  const [customerData, setCustomerData] = useState({
    name: "",
    email: "",
    phone: "",
    address: "",
  });

  const handleCustomerChange = (e, field) => {
    setCustomerData((prevData) => ({
      ...prevData,
      [field]: e.target.value,
    }));
  };

  const handleTableChange = (pagination, filters, sorter) => {
    setSortOrder({
      columnKey: sorter.field,
      order: sorter.order,
    });
  };

  const handleProductSelectChange = (e, id) => {
    if (e.target.checked) {
      setSelectedProducts([...selectedProducts, { id, quantity: 0 }]);
    } else {
      setSelectedProducts(
        selectedProducts.filter((product) => product.id !== id)
      );
    }
  };

  const handleSelectChange = (value, name) => {
    setFormData((prevFormData) => ({
      ...prevFormData,
      [name]: value,
    }));
    if (name === "exportType" && value === "WASTE") {
      setIsVisible(true);
    } else {
      setIsVisible(false);
    }
  };

  const handleSelectAllChange = (e) => {
    if (e.target.checked) {
      const newSelectedProducts = filteredData.map((item) => ({
        id: item.id,
        quantity: 0,
      }));
      setSelectedProducts(newSelectedProducts);
    } else {
      setSelectedProducts([]);
    }
  };

  const handleQuantityChange = (e, id) => {
    const { value } = e.target;
    setSelectedProducts((prevSelectedProducts) =>
      prevSelectedProducts.map((product) =>
        product.id === id
          ? {
              ...product,
              quantity: Math.min(
                value,
                inventoriesData.find((item) => item.id === id).quantity
              ),
            }
          : product
      )
    );
  };

  const handleRemoveProduct = (id) => {
    setSelectedProducts(
      selectedProducts.filter((product) => product.id !== id)
    );
  };

  const handleDone = () => {
    for (let product of selectedProducts) {
      if (
        product.quantity === 0 ||
        product.quantity >
          inventoriesData.find((inv) => inv.id === product.id).quantity
      ) {
        message.error(
          "Please ensure all quantities are greater than 0 and less than or equal to the available quantity."
        );
        return;
      }
    }
    setIsPopupVisible(false);
  };

  const handleCreateCustomer = async () => {
    try {
      const customerDataPayload = {
        name: customerData.name,
        email: customerData.email,
        phone: customerData.phone,
        address: customerData.address,
      };

      const response = await addCustomer({
        customerData: customerDataPayload,
        authToken,
      }).unwrap();
      message.success("Customer created successfully!");
      console.log("Customer data:", response.data);
      const customerId = response.data.id;
      await handleCreateExport(customerId);
    } catch (error) {
      console.error("Error creating customer:", error);
      message.error("Failed to create customer. Please try again.");
    }
  };

  const handleCreateExport = async (customerId = null) => {
    try {
      let exportData = {
        description: formData.description,
        status: formData.status,
        exportDate: formData.exportDate,
        exportType: formData.exportType,
        warehouseIdFrom: formData.warehouseIdFrom,
        warehouseIdTo: formData.warehouseIdTo,
        customerId: customerId || formData.customerId,
      };

      const response = await addExport({
        data: exportData,
        authToken,
      }).unwrap();
      message.success("Export created successfully!");
      console.log("Export data:", response.data);
      const exportId = response.data.id;
      await handleCreateExportDetails(exportId);
    } catch (error) {
      console.error("Error creating export:", error);
      message.error("Failed to create export. Please try again.");
    }
  };

  const handleCreateExportDetails = async (exportId = null) => {
    try {
      const selectedProductDetails = selectedProducts.map((product) => {
        const item = inventoriesData.find((inv) => inv.id === product.id);
        return {
          productId: item.product.id,
          exportId: exportId,
          quantity: product.quantity,
          expiredAt: item.expiredAt,
          zoneId: item.zone.id,
        };
      });
      console.log("Selected Product Details:", selectedProductDetails); // Debugging line
      const response = await createExportDetails({
        data: selectedProductDetails,
        authToken,
      }).unwrap();
      console.log("Export Details Creation Response:", response); // Debugging line
      message.success("Export details created successfully!");
      setSelectedProducts([]);
    } catch (error) {
      console.error("Error creating export details:", error);
      message.error("Failed to create export details. Please try again.");
    }
  };

  const filteredData = inventoriesData.filter((item) => {
    const now = new Date();
    const expiredAt = new Date(item.expiredAt);
    const inFifteenDays = new Date(now);
    inFifteenDays.setDate(now.getDate() + 15);

    if (filterType === "expired" && expiredAt >= now) return false;
    if (
      filterType === "expiring" &&
      (expiredAt < now || expiredAt > inFifteenDays)
    )
      return false;
    if (filterType === "valid" && expiredAt <= now) return false;

    return (
      item.product.name.toLowerCase().includes(searchText.toLowerCase()) ||
      item.zone.name.toLowerCase().includes(searchText.toLowerCase())
    );
  });

  const paginatedData = filteredData.slice(
    (currentPage - 1) * 10,
    currentPage * 10
  );

  const columns = [
    {
      title: (
        <Checkbox
          indeterminate={
            selectedProducts.length > 0 &&
            selectedProducts.length < filteredData.length
          }
          onChange={(e) => handleSelectAllChange(e)}
          checked={
            selectedProducts.length > 0 &&
            selectedProducts.length === filteredData.length
          }
        />
      ),
      dataIndex: "select",
      key: "select",
      render: (text, record) => (
        <Checkbox
          checked={selectedProducts.some((product) => product.id === record.id)}
          onChange={(e) => handleProductSelectChange(e, record.id)}
        />
      ),
    },
    {
      title: "Product Name",
      dataIndex: ["product", "name"],
      key: "productName",
      sorter: (a, b) => a.product.name.localeCompare(b.product.name),
      sortOrder: sortOrder.columnKey === "productName" && sortOrder.order,
      render: (text, record) => record.product.name,
    },
    {
      title: "Zone Name",
      dataIndex: ["zone", "name"],
      key: "zoneName",
      sorter: (a, b) => a.zone.name.localeCompare(b.zone.name),
      sortOrder: sortOrder.columnKey === "zoneName" && sortOrder.order,
      render: (text, record) => record.zone.name,
    },
    {
      title: "Quantity",
      dataIndex: "quantity",
      key: "quantity",
      sorter: (a, b) => a.quantity - b.quantity,
      sortOrder: sortOrder.columnKey === "quantity" && sortOrder.order,
    },
    {
      title: "Expired At",
      dataIndex: "expiredAt",
      key: "expiredAt",
      sorter: (a, b) => new Date(a.expiredAt) - new Date(b.expiredAt),
      sortOrder: sortOrder.columnKey === "expiredAt" && sortOrder.order,
    },
  ];

  useEffect(() => {
    const errors = [
      warehouseError,
      zoneError,
      productError,
      inventoryError,
    ].filter(Boolean);
    if (errors.length > 0) {
      message.error("Failed to fetch data. Please try again.");
    }
  }, [warehouseError, zoneError, productError, inventoryError]);

  const handleOpenPopup = () => setIsPopupVisible(true);
  const handleClosePopup = () => setIsPopupVisible(false);
  const handlePageChange = (page) => setCurrentPage(page);

  const isLoading =
    isWarehouseLoading ||
    isZoneLoading ||
    isProductLoading ||
    isInventoryLoading ||
    isCustomerLoading;

  if (isLoading) return <Loading />;
  if (
    warehouseError ||
    zoneError ||
    productError ||
    inventoryError ||
    customerError
  )
    return <Error500 />;

  return (
    <>
      <Breadcrumbs />
      <div className="MainDash relative">
        <h1>New Export</h1>
        <Form form={form} layout="vertical" initialValues={formData}>
          <Form.Item
            label="Description"
            name="description"
            rules={[
              {
                required: true,
                message: "Please input the export description!",
              },
            ]}
          >
            <Input
              name="description"
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
              placeholder="Product Description"
            />
          </Form.Item>
          <Form.Item
            label="Export Type"
            name="exportType"
            rules={[{ required: true, message: "Please select export type!" }]}
          >
            <Select
              placeholder="Select export type..."
              onChange={(value) => handleSelectChange(value, "exportType")}
            >
              <Option value="CUSTOMER">CUSTOMER</Option>
              <Option value="WAREHOUSE">WAREHOUSE</Option>
              <Option value="WASTE">WASTE</Option>
            </Select>
          </Form.Item>
          {formData.exportType === "CUSTOMER" && (
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  label="Customer Name"
                  name="customerName"
                  rules={[
                    {
                      required: true,
                      message: "Please input customer name!",
                    },
                  ]}
                >
                  <Input
                    name="name"
                    onChange={(e) =>
                      setCustomerData({ ...customerData, name: e.target.value })
                    }
                    placeholder="Customer Name"
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  label="Customer Email"
                  name="customerEmail"
                  rules={[
                    {
                      required: true,
                      message: "Please input customer email!",
                    },
                  ]}
                >
                  <Input
                    name="email"
                    onChange={(e) =>
                      setCustomerData({
                        ...customerData,
                        email: e.target.value,
                      })
                    }
                    placeholder="Customer Email"
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  label="Customer Phone"
                  name="customerPhone"
                  rules={[
                    {
                      required: true,
                      message: "Please input customer phone!",
                    },
                  ]}
                >
                  <Input
                    name="phone"
                    onChange={(e) =>
                      setCustomerData({
                        ...customerData,
                        phone: e.target.value,
                      })
                    }
                    placeholder="Customer Phone"
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  label="Customer Address"
                  name="customerAddress"
                  rules={[
                    {
                      required: true,
                      message: "Please input customer address!",
                    },
                  ]}
                >
                  <Input
                    name="address"
                    onChange={(e) =>
                      setCustomerData({
                        ...customerData,
                        address: e.target.value,
                      })
                    }
                    placeholder="Customer Address"
                  />
                </Form.Item>
              </Col>
            </Row>
          )}
          <Form.Item
            label="To:"
            name="warehouseIdTo"
            rules={[
              {
                required:
                  formData.exportType !== "CUSTOMER" &&
                  formData.exportType !== "WASTE",
                message: "Please select warehouse to transfer!",
              },
            ]}
          >
            <Select
              placeholder="Select warehouse to..."
              onChange={(value) => handleSelectChange(value, "warehouseIdTo")}
              disabled={
                formData.exportType === "WASTE" ||
                formData.exportType === "CUSTOMER"
              }
            >
              {warehousesData
                .filter(
                  (warehouse) => warehouse.id !== formData.warehouseIdFrom
                )
                .map((warehouse) => (
                  <Option key={warehouse.id} value={warehouse.id}>
                    {warehouse.name}
                  </Option>
                ))}
            </Select>
          </Form.Item>
          <Form.Item label="Status" name="status">
            <Input value="PENDING" disabled />
          </Form.Item>
          <Form.Item
            label="Export Date"
            name="exportDate"
            rules={[{ required: true, message: "Please select export date!" }]}
          >
            <Input
              type="date"
              onChange={(e) =>
                setFormData({ ...formData, exportDate: e.target.value })
              }
            />
          </Form.Item>
          <Form.Item>
            <Button type="primary" onClick={handleOpenPopup}>
              Choose Products
            </Button>
          </Form.Item>
          <Button
            type="primary"
            onClick={async () => {
              if (formData.exportType === "CUSTOMER") {
                if (
                  customerData.name &&
                  customerData.email &&
                  customerData.phone &&
                  customerData.address
                ) {
                  try {
                    await handleCreateCustomer();
                  } catch (error) {
                    console.error(
                      "Failed to create export with customer:",
                      error
                    );
                  }
                } else {
                  message.error("Please fill all customer fields.");
                }
              } else {
                await handleCreateExport();
              }
            }}
          >
            {formData.exportType === "CUSTOMER"
              ? "Create Customer Export"
              : formData.exportType === "WASTE"
              ? "Create Waste Export"
              : "Continue"}
          </Button>
        </Form>
      </div>
      {isPopupVisible && (
        <div
          style={{
            position: "absolute",
            top: 0,
            left: 0,
            width: "100%",
            height: "100%",
            backgroundColor: "rgba(0, 0, 0, 0.75)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 50,
          }}
        >
          <div
            style={{
              backgroundColor: "white",
              padding: "20px",
              borderRadius: "10px",
              width: "90%",
              height: "90%",
              maxHeight: "100%",
              overflowY: "auto",
            }}
          >
            <div style={{ marginBottom: 16 }}>
              <Radio.Group
                onChange={(e) => setFilterType(e.target.value)}
                value={filterType}
                style={{ marginRight: 16 }}
              >
                <Radio value="all">All</Radio>
                <Radio value="expired">Expired</Radio>
                <Radio value="expiring">Expiring in 15 days</Radio>
                <Radio value="valid">Valid</Radio>
              </Radio.Group>
              <Input
                placeholder="Search by product or zone name"
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
                style={{ width: "50%" }}
              />
            </div>
            <div style={{ display: "flex", height: "80%" }}>
              <div style={{ width: "70%", paddingRight: "10px" }}>
                <Table
                  dataSource={paginatedData}
                  columns={columns}
                  rowKey="id"
                  pagination={false}
                  onChange={handleTableChange}
                />
                <Pagination
                  current={currentPage}
                  pageSize={10}
                  total={filteredData.length}
                  onChange={handlePageChange}
                  style={{ marginTop: "10px", textAlign: "center" }}
                />
              </div>
              <div
                style={{
                  width: "30%",
                  maxHeight: "100%",
                  overflowY: "auto",
                  padding: "10px",
                  border: "1px solid #ddd",
                  borderRadius: "10px",
                }}
              >
                <h2 className="text-xl font-bold mb-4">Selected Products</h2>
                {selectedProducts.map((product) => {
                  const item = inventoriesData.find(
                    (inv) => inv.id === product.id
                  );
                  return (
                    <div
                      key={product.id}
                      style={{
                        marginBottom: "10px",
                        padding: "10px",
                        border: "1px solid #ddd",
                        borderRadius: "5px",
                      }}
                    >
                      <h3>{item.product.name}</h3>
                      <p>Available: {item.quantity}</p>
                      <Input
                        type="number"
                        value={product.quantity}
                        onChange={(e) => handleQuantityChange(e, product.id)}
                        min={1}
                        max={item.quantity}
                        style={{ marginBottom: "10px" }}
                      />
                      <Button
                        type="link"
                        danger
                        onClick={() => handleRemoveProduct(product.id)}
                      >
                        Remove
                      </Button>
                    </div>
                  );
                })}
              </div>
            </div>
            <div className="flex justify-end mt-4" style={{ width: "100%" }}>
              <Button
                onClick={handleDone}
                type="primary"
                style={{ marginRight: "10px" }}
              >
                Done
              </Button>
              <Button onClick={handleClosePopup}>Close</Button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default StaffAddExport;
