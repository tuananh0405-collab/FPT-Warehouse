import React, { useState, useEffect } from "react";
import Breadcrumbs from "../../utils/Breadcumbs";
import { useParams } from "react-router-dom";
import { useSelector } from "react-redux";
import {
  useGetExportByIdQuery,
  useUpdateExportByIdMutation,
} from "../../redux/api/exportApiSlice";
import {
  useGetAllExportDetailsByExportIdQuery,
  useDeleteExportDetailsMutation,
  useCheckAvailableQuantityMutation,
  useUpdateExportDetailsMutation,
  useCreateExportDetailsMutation,
  useUpdateAndAddExportDetailsMutation,
} from "../../redux/api/exportDetailApiSlice";
import { useGetAllInventoriesQuery } from "../../redux/api/inventoryApiSlice";
import ExportStatus from "../../components/Orders/ExportStatus";
import "../../components/Orders/MainDash.css";
import {
  Table,
  Button,
  Modal,
  Input,
  Select,
  message,
  Checkbox,
  Pagination,
  Radio,
} from "antd";
import EditNoteIcon from "@mui/icons-material/EditNote";
import DeleteRoundedIcon from "@mui/icons-material/DeleteRounded";
import { useGetAllWarehousesQuery } from "../../redux/api/warehousesApiSlice";

const { TextArea } = Input;
const { Option } = Select;

function StaffExportDetail() {
  const { id } = useParams();
  const userInfo = useSelector((state) => state.auth.userInfo);
  const authToken = userInfo?.data?.token;
  const warehouseId = userInfo?.data?.warehouseId;

  const {
    data: exportsDataRes = {},
    isLoading: exportsLoading,
    error: exportsError,
  } = useGetExportByIdQuery({
    authToken,
    exportId: id,
  });
  const {
    data: exportProductsData = {},
    isLoading: exportProductsLoading,
    error: exportProductsError,
  } = useGetAllExportDetailsByExportIdQuery({
    authToken,
    exportId: id,
  });
  const {
    data: inventoriesData,
    isFetching: isInventoryLoading,
    error: inventoryError,
  } = useGetAllInventoriesQuery(authToken);

  const {
    data: warehouses,
    isFetching: isWarehouseLoading,
    error: warehouseError,
  } = useGetAllWarehousesQuery(authToken);

  const exportData = exportsDataRes.data || {};
  const exportProducts = exportProductsData.data || [];
  const inventories = inventoriesData?.data || [];
  const warehousesData = warehouses?.data || [];

  const [isProductListPopupVisible, setIsProductListPopupVisible] =
    useState(false);
  const [isConfirmationPopupVisible, setIsConfirmationPopupVisible] =
    useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [editableData, setEditableData] = useState({});
  const [confirmationAction, setConfirmationAction] = useState(null);
  const [selectedProductIds, setSelectedProductIds] = useState([]);
  const [temporarilyHiddenProductIds, setTemporarilyHiddenProductIds] =
    useState([]);
  const [editableRow, setEditableRow] = useState(null);
  const [editableRowData, setEditableRowData] = useState({});
  const [localUpdatedQuantities, setLocalUpdatedQuantities] = useState({});
  const [selectedProducts, setSelectedProducts] = useState([]);

  const [updateExportById, { isLoading: isUpdating }] =
    useUpdateExportByIdMutation();
  const [deleteExportDetails, { isLoading: isDeleting }] =
    useDeleteExportDetailsMutation();
  const [updateExportDetails] = useUpdateExportDetailsMutation();
  const [addExportDetails] = useCreateExportDetailsMutation();
  const [updateAndAddExportDetails] = useUpdateAndAddExportDetailsMutation();
  const [checkAvailableQuantity] = useCheckAvailableQuantityMutation();

  const [isProductModalVisible, setIsProductModalVisible] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchText, setSearchText] = useState("");
  const [filterType, setFilterType] = useState("all");

  useEffect(() => {
    console.log(warehouseId);
    if (exportData) {
      setEditableData({
        ...exportData,
        customerName: exportData.customer?.name || "",
        customerEmail: exportData.customer?.email || "",
        customerPhone: exportData.customer?.phone || "",
        customerAddress: exportData.customer?.address || "",
        warehouseIdTo: exportData.warehouseTo
          ? exportData.warehouseTo.id
          : null,
      });
    }
  }, [warehouseId, exportData]);

  useEffect(() => {
    console.log(exportProducts);
    const selected = exportProducts.map((product) => ({
      id: product.id,
      productId: product.product.id,
      quantity: product.quantity,
      expiredAt: product.expiredAt,
      zoneId: product.zone.id,
    }));
    setSelectedProducts(selected);
  }, [exportProducts]);

  const handleOpenProductListPopup = () => setIsProductListPopupVisible(true);
  const handleCloseProductListPopup = () => setIsProductListPopupVisible(false);

  const handleOpenConfirmationPopup = (action) => {
    setConfirmationAction(action);
    setIsConfirmationPopupVisible(true);
  };

  const handleCloseConfirmationPopup = () =>
    setIsConfirmationPopupVisible(false);

  const handleOpenEditMode = () => {
    setIsEditMode(true);
    setEditableData({
      ...exportData,
      customerName: exportData.customer?.name || "",
      customerEmail: exportData.customer?.email || "",
      customerPhone: exportData.customer?.phone || "",
      customerAddress: exportData.customer?.address || "",
      warehouseIdTo: exportData.warehouseTo ? exportData.warehouseTo.id : null,
    });
  };

  const handleCancelEdit = () => {
    setConfirmationAction({ type: "cancelEditMode" });
    setIsConfirmationPopupVisible(true);
  };

  const handleSaveRowEdit = async (record) => {
    const { product, quantity } = editableRowData;
    const checkResponse = await checkAvailableQuantity({
      authToken,
      data: {
        productId: product.id,
        quantity,
        warehouseId: warehouseId,
      },
    }).unwrap();
    if (
      checkResponse.status === 200 &&
      checkResponse.message === "Enough quantity"
    ) {
      setLocalUpdatedQuantities({
        ...localUpdatedQuantities,
        [record.id]: { exportDetailId: record.id, quantity },
      });
      setEditableRow(null);
      setEditableRowData({});
      message.success("Quantity update is valid");
    } else {
      message.error("Not enough quantity in this warehouse");
    }
  };

  const handleSaveEdit = async () => {
    if (temporarilyHiddenProductIds.length > 0) {
      try {
        await deleteExportDetails({
          authToken,
          ids: temporarilyHiddenProductIds,
        }).unwrap();
        setTemporarilyHiddenProductIds([]);
      } catch (error) {
        message.error("Failed to delete selected products");
        console.error("Delete export details error:", error);
      }
    }

    const updateData = {
      description: editableData.description || "",
      exportDate: editableData.exportDate
        ? new Date(editableData.exportDate).toISOString()
        : null,
      warehouseIdTo: editableData.warehouseIdTo || null,
      customerName: editableData.customerName || null,
      customerAddress: editableData.customerAddress || null,
      customerPhone: editableData.customerPhone || null,
      customerEmail: editableData.customerEmail || null,
    };

    const customerChanged =
      editableData.customerName !== exportData.customer?.name ||
      editableData.customerAddress !== exportData.customer?.address ||
      editableData.customerPhone !== exportData.customer?.phone ||
      editableData.customerEmail !== exportData.customer?.email;

    if (customerChanged && exportData.customer?.id) {
      updateData.customerId = exportData.customer.id;
    }

    try {
      await updateExportById({
        exportId: id,
        authToken,
        data: updateData,
      }).unwrap();
      setIsEditMode(false);
      handleCloseConfirmationPopup();
      message.success("Export updated successfully");
    } catch (error) {
      message.error("Failed to update export");
      console.error("Update export error:", error);
    }

    const updatedExportDetails = Object.values(localUpdatedQuantities).map(
      (detail) => ({
        exportDetailId: detail.exportDetailId,
        quantity: detail.quantity,
        warehouseId: warehouseId,
      })
    );

    if (updatedExportDetails.length > 0) {
      try {
        await updateExportDetails({
          data: updatedExportDetails,
          authToken,
        }).unwrap();
        message.success("Product quantities updated successfully");
      } catch (error) {
        message.error("Failed to update product quantities");
        console.error("Update product quantities error:", error);
      }
    }

    if (selectedProducts.length > 0) {
      const newExportDetails = selectedProducts.map((product) => {
        const item = inventories.find(
          (inv) => inv.product.id === product.productId
        );
        return {
          productId: item.product.id,
          exportId: id,
          quantity: product.quantity,
          expiredAt: new Date(item.expiredAt).toISOString(),
          zoneId: item.zone.id,
        };
      });

      try {
        await addExportDetails({
          data: newExportDetails,
          authToken,
        }).unwrap();
        message.success("New products added successfully");
      } catch (error) {
        message.error("Failed to add new products");
        console.error("Add new products error:", error);
      }
    }

    setLocalUpdatedQuantities({});
    setSelectedProducts([]);
  };

  const handleChange = (field, value) => {
    setEditableData((prevData) => ({
      ...prevData,
      [field]: value,
    }));
  };

  const handleDelete = () => {
    setTemporarilyHiddenProductIds([
      ...temporarilyHiddenProductIds,
      ...selectedProductIds,
    ]);
    setSelectedProductIds([]);
  };

  const handleConfirmAction = async () => {
    switch (confirmationAction.type) {
      case "deleteProduct":
        await handleDelete();
        break;
      case "cancelEditMode":
        setIsEditMode(false);
        setSelectedProductIds([]);
        setTemporarilyHiddenProductIds([]);
        handleCloseConfirmationPopup();
        break;
      case "updateExport":
        await handleSaveEdit();
        break;
      default:
        break;
    }
  };

  const handleOpenProductModal = () => {
    setIsProductModalVisible(true);
  };

  const handleCloseProductModal = () => {
    setIsProductModalVisible(false);
  };

  const handleUpdate = () => {
    handleOpenProductModal();
  };

  const handleDone = async () => {
    const uniqueProducts = selectedProducts.reduce((acc, product) => {
      const key = `${product.productId}-${product.zoneId}-${new Date(
        product.expiredAt
      ).getTime()}`;
      if (!acc[key]) {
        acc[key] = product;
      }
      return acc;
    }, {});

    const newExportDetails = Object.values(uniqueProducts).map((product) => {
      const item = inventories.find(
        (inv) => inv.product.id === product.productId
      );
      return {
        productId: item.product.id,
        exportId: id,
        quantity: product.quantity,
        expiredAt: new Date(item.expiredAt).toISOString(),
        zoneId: item.zone.id,
      };
    });

    try {
      await updateAndAddExportDetails({
        data: newExportDetails,
        exportId: id,
        authToken,
      }).unwrap();
      message.success("Export details updated successfully");
    } catch (error) {
      message.error("Failed to update export details");
      console.error("Update and add export details error:", error);
    }

    setLocalUpdatedQuantities({});
    setSelectedProducts([]);
    setIsEditMode(false);
    setIsProductModalVisible(false);
  };

  const calculateCurrentInventory = (item) => {
    const existingExportDetail = exportProducts.find(
      (product) =>
        product.product.id === item.product.id &&
        product.zone.id === item.zone.id &&
        new Date(product.expiredAt).getTime() ===
          new Date(item.expiredAt).getTime()
    );

    if (existingExportDetail) {
      return item.quantity + existingExportDetail.quantity;
    }

    return item.quantity;
  };

  const handlePageChange = (page) => setCurrentPage(page);

  const handleQuantityChange = (e, id) => {
    const { value } = e.target;
    setSelectedProducts((prevSelectedProducts) =>
      prevSelectedProducts.map((product) =>
        product.productId === id
          ? {
              ...product,
              quantity: parseInt(value),
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

  const handleProductSelectChange = (e, product) => {
    if (e.target.checked) {
      setSelectedProducts((prevSelectedProducts) => [
        ...prevSelectedProducts,
        { ...product, quantity: 0 },
      ]);
    } else {
      setSelectedProducts((prevSelectedProducts) =>
        prevSelectedProducts.filter((p) => p.productId !== product.productId)
      );
    }
  };

  const handleSearch = (e) => {
    setSearchText(e.target.value.toLowerCase());
  };

  const filteredData = inventories
    .filter(
      (item) =>
        item.zone.warehouse.id === warehouseId &&
        [5, 6, 7, 8].includes(item.zone.id)
    )
    .filter((item) => {
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
        item.product.name.toLowerCase().includes(searchText) ||
        item.zone.name.toLowerCase().includes(searchText)
      );
    });

  const paginatedData = filteredData.slice(
    (currentPage - 1) * 7,
    currentPage * 7
  );

  const columns = [
    ...(isEditMode
      ? [
          {
            title: "Select",
            dataIndex: "select",
            key: "select",
            render: (_, record) => (
              <Checkbox
                checked={selectedProductIds.includes(record.id)}
                onChange={(e) => {
                  if (e.target.checked) {
                    setSelectedProductIds([...selectedProductIds, record.id]);
                  } else {
                    setSelectedProductIds(
                      selectedProductIds.filter((id) => id !== record.id)
                    );
                  }
                }}
              />
            ),
          },
        ]
      : []),
    {
      title: "Product Name",
      dataIndex: ["product", "name"],
      key: "productName",
    },
    {
      title: "Product Description",
      dataIndex: ["product", "description"],
      key: "productDescription",
    },
    {
      title: "Product Category",
      dataIndex: ["product", "category", "name"],
      key: "productCategory",
    },
    {
      title: "Quantity",
      dataIndex: "quantity",
      key: "quantity",
      render: (text, record) =>
        editableRow === record.id ? (
          <Input
            type="number"
            value={editableRowData.quantity}
            onChange={(e) =>
              setEditableRowData({
                ...editableRowData,
                quantity: e.target.value,
              })
            }
          />
        ) : (
          localUpdatedQuantities[record.id]?.quantity ?? text
        ),
    },
    {
      title: "Expired At",
      dataIndex: "expiredAt",
      key: "expiredAt",
      render: (text) => new Date(text).toLocaleDateString(),
    },
    ...(isEditMode
      ? [
          {
            title: "Action",
            key: "action",
            render: (_, record) =>
              editableRow === record.id ? (
                <span>
                  <a
                    className="no-select"
                    onClick={() => handleSaveRowEdit(record)}
                  >
                    Save
                  </a>
                  <a
                    className="no-select"
                    onClick={() => {
                      setEditableRow(null);
                      setEditableRowData({});
                    }}
                  >
                    Cancel
                  </a>
                </span>
              ) : (
                <a
                  className="no-select"
                  onClick={() => {
                    setEditableRow(record.id);
                    setEditableRowData(record);
                  }}
                >
                  Change
                </a>
              ),
          },
        ]
      : []),
  ];

  const productSelectionColumns = [
    {
      title: "Select",
      dataIndex: "select",
      key: "select",
      render: (_, record) => (
        <Checkbox
          checked={selectedProducts.some(
            (product) => product.productId === record.product.id
          )}
          onChange={(e) =>
            handleProductSelectChange(e, {
              ...record,
              productId: record.product.id,
            })
          }
        />
      ),
    },
    {
      title: "Product Name",
      dataIndex: ["product", "name"],
      key: "productName",
    },
    {
      title: "Product Description",
      dataIndex: ["product", "description"],
      key: "productDescription",
    },
    {
      title: "Product Category",
      dataIndex: ["product", "category", "name"],
      key: "productCategory",
    },
    {
      title: "Quantity",
      dataIndex: "quantity",
      key: "quantity",
    },
    {
      title: "Expired At",
      dataIndex: "expiredAt",
      key: "expiredAt",
      render: (text) => new Date(text).toLocaleDateString(),
    },
  ];

  if (exportsLoading || isInventoryLoading) return <div>Loading...</div>;
  if (exportsError) return <div>Error loading export data</div>;

  const modalConfirmTitle =
    {
      deleteProduct: "Do you want to delete the selected products?",
      approveExport: "Do you want to approve this export?",
      cancelEditMode: "Do you want to cancel editing?",
      updateExport: "Do you want to save changes?",
    }[confirmationAction?.type] || "Confirm Action";

  return (
    <div>
      <div className="container">
        <Breadcrumbs />
        <h1 className="font-bold text-3xl py-4">Export {id}</h1>
        <table className="table-detail">
          <tbody>
            <tr>
              <td className="export-attribute-title">Description:</td>
              <td>
                {isEditMode ? (
                  <TextArea
                    rows={4}
                    style={{ width: "100%" }}
                    value={editableData.description}
                    onChange={(e) =>
                      handleChange("description", e.target.value)
                    }
                  />
                ) : (
                  <p>{exportData.description}</p>
                )}
              </td>
            </tr>
            <tr>
              <td className="export-attribute-title">Status:</td>
              <td>
                {isEditMode ? (
                  <Select
                    placeholder="Select a status"
                    defaultValue={exportData.status}
                    onChange={(value) => handleChange("status", value)}
                    style={{ width: "100%" }}
                  >
                    <Option value="PENDING">PENDING</Option>
                    <Option value="SHIPPING">SHIPPING</Option>
                    <Option value="SUCCEED">SUCCEED</Option>
                    <Option value="CANCEL">CANCEL</Option>
                  </Select>
                ) : (
                  <p>
                    <ExportStatus status={exportData.status} />
                  </p>
                )}
              </td>
            </tr>
            <tr>
              <td className="export-attribute-title">Export Date:</td>
              <td>
                {isEditMode ? (
                  <Input
                    type="date"
                    style={{ width: "100%" }}
                    value={
                      new Date(editableData.exportDate)
                        .toISOString()
                        .split("T")[0]
                    }
                    onChange={(e) => handleChange("exportDate", e.target.value)}
                  />
                ) : (
                  <p>{new Date(exportData.exportDate).toLocaleDateString()}</p>
                )}
              </td>
            </tr>
            <tr>
              <td className="export-attribute-title">Export Type:</td>
              <td>
                <p>{exportData.exportType}</p>
              </td>
            </tr>
            {exportData.exportType === "WAREHOUSE" && (
              <tr>
                <td className="export-attribute-title">Warehouse:</td>
                <td>
                  <p>{exportData.warehouseTo?.name}</p>
                </td>
              </tr>
            )}
            <tr>
              <td className="export-attribute-title">To:</td>
              <td>
                {isEditMode ? (
                  <div>
                    {exportData.exportType === "CUSTOMER" ? (
                      <Input
                        style={{ width: "100%" }}
                        value={editableData.customerAddress}
                        onChange={(e) =>
                          handleChange("customerAddress", e.target.value)
                        }
                      />
                    ) : (
                      <Select
                        style={{ width: "100%" }}
                        showSearch
                        placeholder="Select a warehouse"
                        value={editableData.warehouseIdTo}
                        onChange={(value) =>
                          handleChange("warehouseIdTo", value)
                        }
                        loading={isWarehouseLoading}
                      >
                        {warehousesData.map((warehouse) => (
                          <Option key={warehouse.id} value={warehouse.id}>
                            {warehouse.name}
                          </Option>
                        ))}
                      </Select>
                    )}
                  </div>
                ) : (
                  <p>
                    {exportData.exportType === "CUSTOMER"
                      ? exportData.customer?.address
                      : exportData.warehouseTo?.address}
                  </p>
                )}
              </td>
            </tr>
            {exportData.exportType === "CUSTOMER" && (
              <>
                <tr>
                  <td className="export-attribute-title">Customer:</td>
                  <td>
                    {isEditMode ? (
                      <Input
                        style={{ width: "100%" }}
                        value={editableData.customerName}
                        onChange={(e) =>
                          handleChange("customerName", e.target.value)
                        }
                      />
                    ) : (
                      <p>{exportData.customer?.name}</p>
                    )}
                  </td>
                </tr>
                <tr>
                  <td className="export-attribute-title">Customer Email:</td>
                  <td>
                    {isEditMode ? (
                      <Input
                        style={{ width: "100%" }}
                        value={editableData.customerEmail}
                        onChange={(e) =>
                          handleChange("customerEmail", e.target.value)
                        }
                      />
                    ) : (
                      <p>{exportData.customer?.email}</p>
                    )}
                  </td>
                </tr>
                <tr>
                  <td className="export-attribute-title">Customer Phone:</td>
                  <td>
                    {isEditMode ? (
                      <Input
                        style={{ width: "100%" }}
                        value={editableData.customerPhone}
                        onChange={(e) =>
                          handleChange("customerPhone", e.target.value)
                        }
                      />
                    ) : (
                      <p>{exportData.customer?.phone}</p>
                    )}
                  </td>
                </tr>
              </>
            )}
          </tbody>
        </table>
        <div>
          <Button
            className="no-select"
            href="#"
            onClick={handleOpenProductListPopup}
          >
            View Export Details
          </Button>
          <div>
            {isEditMode ? (
              <div className="action-buttons">
                <Button
                  style={{
                    background: "#ef4444",
                    color: "white",
                  }}
                  type="default"
                  onClick={handleCancelEdit}
                >
                  Cancel
                </Button>
                <Button
                  style={{
                    background: "#16a34a",
                    color: "white",
                  }}
                  type="default"
                  onClick={() =>
                    handleOpenConfirmationPopup({ type: "updateExport" })
                  }
                >
                  Save
                </Button>
              </div>
            ) : (
              <div>
                {exportData.status === "PENDING" && (
                  <div className="action-buttons">
                    <Button
                      style={{
                        background: "#0284c7",
                        color: "white",
                      }}
                      type="default"
                      onClick={handleOpenEditMode}
                    >
                      <EditNoteIcon /> Edit
                    </Button>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
      <Modal
        transitionName=""
        title="Export Details"
        width={800}
        className="custom-modal"
        visible={isProductListPopupVisible}
        onCancel={handleCloseProductListPopup}
        footer={[
          <div className="footer-modal-button">
            <Button key="close" onClick={handleCloseProductListPopup}>
              Close
            </Button>
            {isEditMode && selectedProductIds.length !== 0 ? (
              <Button
                style={{
                  background: "#d32f2f",
                  color: "white",
                }}
                type="default"
                onClick={() =>
                  handleOpenConfirmationPopup({ type: "deleteProduct" })
                }
              >
                <DeleteRoundedIcon /> Delete Selected
              </Button>
            ) : null}
            <Button
              key="update"
              style={{
                background: "#3b82f6",
                color: "white",
              }}
              type="default"
              onClick={handleUpdate}
            >
              Update
            </Button>
          </div>,
        ]}
      >
        <Table
          dataSource={exportProducts.filter(
            (product) => !temporarilyHiddenProductIds.includes(product.id)
          )}
          columns={columns}
          rowKey="id"
          pagination={false}
        />
      </Modal>

      <Modal
        title="Select Products"
        visible={isProductModalVisible}
        onCancel={handleCloseProductModal}
        footer={[
          <Button key="close" onClick={handleCloseProductModal}>
            Close
          </Button>,
          <Button key="done" type="primary" onClick={handleDone}>
            Done
          </Button>,
        ]}
        style={{ top: 20 }}
        width={1000}
      >
        <div style={{ display: "flex", height: "80vh" }}>
          <div style={{ width: "70%", paddingRight: "10px" }}>
            <Radio.Group
              onChange={(e) => setFilterType(e.target.value)}
              value={filterType}
              style={{ marginBottom: 16 }}
            >
              <Radio value="all">All</Radio>
              <Radio value="expired">Expired</Radio>
              <Radio value="expiring">Expiring in 15 days</Radio>
              <Radio value="valid">Valid</Radio>
            </Radio.Group>
            <Input
              placeholder="Search by product or zone name"
              value={searchText}
              onChange={handleSearch}
              style={{ marginBottom: 16 }}
            />
            <Table
              dataSource={paginatedData}
              columns={productSelectionColumns}
              rowKey="id"
              pagination={false}
            />
            <Pagination
              current={currentPage}
              pageSize={7}
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
              const item = inventories.find(
                (inv) => inv.product.id === product.productId
              );

              if (!item) {
                return null;
              }

              const currentInventory = calculateCurrentInventory(item);

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
                  <p>Available: {currentInventory}</p>
                  <Input
                    type="number"
                    value={product.quantity}
                    onChange={(e) => handleQuantityChange(e, product.id)}
                    min={1}
                    max={currentInventory}
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
      </Modal>

      <Modal
        transitionName=""
        title={modalConfirmTitle}
        width={400}
        className="custom-modal"
        visible={isConfirmationPopupVisible}
        onCancel={handleCloseConfirmationPopup}
        footer={[
          <div key="footer">
            <Button key="close" onClick={handleCloseConfirmationPopup}>
              Close
            </Button>
            <Button
              style={{
                background: "#3b82f6",
                color: "white",
              }}
              type="default"
              onClick={handleConfirmAction}
            >
              Confirm
            </Button>
          </div>,
        ]}
      ></Modal>
    </div>
  );
}

export default StaffExportDetail;
