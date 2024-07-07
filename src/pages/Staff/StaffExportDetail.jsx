import React, { useState, useEffect } from "react";
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
  useUpdateAndAddExportDetailsMutation, // Import hook mới
} from "../../redux/api/exportDetailApiSlice";
import { useGetAllInventoriesQuery } from "../../redux/api/inventoryApiSlice";
import Breadcrumbs from "../../utils/Breadcumbs";
import "../../components/Orders/MainDash.css";
import {
  Table,
  Button,
  Modal,
  Input,
  message,
  Checkbox,
  Pagination,
  Radio,
} from "antd";
import EditNoteIcon from "@mui/icons-material/EditNote";
import DeleteRoundedIcon from "@mui/icons-material/DeleteRounded";

const { TextArea } = Input;

function StaffExportDetail() {
  const { id } = useParams();
  const userInfo = useSelector((state) => state.auth.userInfo);
  const authToken = userInfo?.data?.token;

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

  const exportData = exportsDataRes.data || {};
  const exportProducts = exportProductsData.data || [];
  const inventories = inventoriesData?.data || [];

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
  const [localUpdatedQuantities, setLocalUpdatedQuantities] = useState({});
  const [selectedProducts, setSelectedProducts] = useState([]);

  const [updateExportById, { isLoading: isUpdating }] =
    useUpdateExportByIdMutation();
  const [deleteExportDetails, { isLoading: isDeleting }] =
    useDeleteExportDetailsMutation();
  const [updateExportDetails] = useUpdateExportDetailsMutation();
  const [addExportDetails] = useCreateExportDetailsMutation();
  const [updateAndAddExportDetails] = useUpdateAndAddExportDetailsMutation(); // Sử dụng mutation mới
  const [checkAvailableQuantity] = useCheckAvailableQuantityMutation();

  const [isProductModalVisible, setIsProductModalVisible] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchText, setSearchText] = useState("");
  const [filterType, setFilterType] = useState("all");

  useEffect(() => {
    setEditableData(exportData);
  }, [exportData]);

  useEffect(() => {
    const selected = exportProducts.map((product) => ({
      id: product.id,
      quantity: product.quantity,
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
    setEditableData(exportData);
  };

  const handleCancelEdit = () => {
    setConfirmationAction({ type: "cancelEditMode" });
    setIsConfirmationPopupVisible(true);
  };

  const calculateCurrentInventory = (item) => {
    const existingExportDetail = exportProducts.find(
      (product) =>
        product.product.id === item.product.id &&
        product.zone.id === item.zone.id &&
        new Date(product.expiredAt).getTime() ===
          new Date(item.expiredAt).getTime()
    );
    return (
      item.quantity + (existingExportDetail ? existingExportDetail.quantity : 0)
    );
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

    const updatedExportDetails = Object.values(localUpdatedQuantities).map(
      (detail) => ({
        exportDetailId: detail.exportDetailId,
        quantity: detail.quantity,
        warehouseId: exportData.warehouseFrom.id,
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
        const item = inventories.find((inv) => inv.id === product.id);
        return {
          productId: item.product.id,
          exportId: id,
          quantity: product.quantity,
          expiredAt: new Date(item.expiredAt).toISOString(), // Chuyển đổi expiredAt sang định dạng ISO
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
    setIsEditMode(false);
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
    const newExportDetails = selectedProducts.map((product) => {
      const item = inventories.find((inv) => inv.id === product.id);
      return {
        productId: item.product.id,
        exportId: id,
        quantity: product.quantity,
        expiredAt: new Date(item.expiredAt).toISOString(),
        zoneId: item.zone.id,
      };
    });

    console.log(newExportDetails);

    try {
      await updateAndAddExportDetails({
        data: newExportDetails,
        exportId: id, // Thêm exportId vào request
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

  const handlePageChange = (page) => setCurrentPage(page);

  const handleProductSelectChange = (e, id) => {
    if (e.target.checked) {
      setSelectedProducts([...selectedProducts, { id, quantity: 0 }]);
    } else {
      setSelectedProducts(
        selectedProducts.filter((product) => product.id !== id)
      );
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
                inventories.find((item) => item.id === id).quantity
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

  const filteredData = inventories.filter((item) => {
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
      render: (text, record) => record.product.name,
    },
    {
      title: "Zone Name",
      dataIndex: ["zone", "name"],
      key: "zoneName",
      sorter: (a, b) => a.zone.name.localeCompare(b.zone.name),
      render: (text, record) => record.zone.name,
    },
    {
      title: "Quantity",
      dataIndex: "quantity",
      key: "quantity",
      sorter: (a, b) => a.quantity - b.quantity,
    },
    {
      title: "Expired At",
      dataIndex: "expiredAt",
      key: "expiredAt",
      sorter: (a, b) => new Date(a.expiredAt) - new Date(b.expiredAt),
      render: (text) => new Date(text).toLocaleDateString(),
    },
  ];

  if (exportsLoading || isInventoryLoading) return <div>Loading...</div>;
  if (exportsError) return <div>Error loading export data</div>;

  const modalConfirmTitle =
    {
      deleteProduct: "Do you want to delete the selected products?",
      cancelEditMode: "Do you want to cancel editing?",
      updateExport: "Do you want to save changes?",
    }[confirmationAction?.type] || "Confirm Action";

  return (
    <div>
      <div className="container">
        <Breadcrumbs />
        <h1>Export {id}</h1>
        <div>
          <a
            className="no-select"
            href="#"
            onClick={handleOpenProductListPopup}
          >
            View Export Details
          </a>
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
        </div>
      </div>
      <Modal
        transitionName=""
        title="Export Details"
        width={1200}
        className="custom-modal"
        visible={isProductListPopupVisible}
        onCancel={handleCloseProductListPopup}
        footer={[
          <div className="footer-modal-button" key="footer">
            <Button key="close" onClick={handleCloseProductListPopup}>
              Close
            </Button>
            {isEditMode && selectedProductIds.length !== 0 && (
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
            )}
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
              onChange={(e) => setSearchText(e.target.value)}
              style={{ marginBottom: 16 }}
            />
            <Table
              dataSource={paginatedData}
              columns={columns}
              rowKey="id"
              pagination={false}
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
              const item = inventories.find((inv) => inv.id === product.id);
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
