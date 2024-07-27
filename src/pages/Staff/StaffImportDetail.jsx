import React, { useEffect, useState } from "react";
import { useGetImportByIdQuery } from "../../redux/api/importApiSlice";
import { useGetImportDetailsByImportIdQuery } from "../../redux/api/importDetailApiSlice";
import { useGetAllCustomersQuery } from "../../redux/api/customersApiSlice";
import { useGetAllWarehousesQuery } from "../../redux/api/warehousesApiSlice";
import { useUpdateImportMutation } from "../../redux/api/importApiSlice";
import { useGetLatestImportQuery } from "../../redux/api/importApiSlice";
import { useParams } from "react-router-dom";
import { useSelector } from "react-redux";
import Breadcrumbs from "../../utils/Breadcumbs";
import moment from "moment/moment";
import {
  Table,
  Input,
  Button,
  Modal,
  Select,
  message,
  DatePicker,
} from "antd";
import { Description, EditTwoTone } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import dayjs from 'dayjs';

const { TextArea } = Input;

function StaffImportDetail() {
  const { id } = useParams();

  const navigate = useNavigate();
  const userInfo = useSelector((state) => state.auth);

  if (!userInfo) {
    navigate("/", { replace: true });
    return null;
  }

  const authToken = userInfo?.userInfo?.data?.token;
  const wid = userInfo?.userInfo?.data?.warehouseId;

  const [formData, setFormData] = useState({});
  const [initialFormData, setInitialFormData] = useState({});
  const [detailFormData, setDetailFormData] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);

  const { data: importResponse } = useGetImportByIdQuery({
    importId: id,
    authToken,
  });
  const { data: importDetailResponse, isLoading: importDetailResponseLoading } =
    useGetImportDetailsByImportIdQuery({ authToken, importId: id });
  const { data: customerResponse } = useGetAllCustomersQuery(authToken);
  const { data: warehouseResponse } = useGetAllWarehousesQuery(authToken);
  const { data: latestImportResponse } = useGetLatestImportQuery({
    authToken,
    warehouseId: wid,
  });

  const [updateImport] = useUpdateImportMutation();

  const importData = importResponse?.data;
  const importDetailData = importDetailResponse?.data;

  useEffect(() => {
    if (importData) {
      const flattenedData = {
        ...importData,
        warehouseFromDescription: importData.warehouseFrom?.description,
        warehouseFromName: importData.warehouseFrom?.name,
        warehouseFromAddress: importData.warehouseFrom?.address,
        warehouseToName: importData.warehouseTo?.name,
        warehouseToDescription: importData.warehouseTo?.description,
        warehouseToAddress: importData.warehouseTo?.address,
        customerName: importData.customer?.name,
        customerEmail: importData.customer?.email,
        customerPhone: importData.customer?.phone,
        customerAddress: importData.customer?.address,
        customerId: importData.customer?.id,
        warehouseIdTo: importData.warehouseTo?.id,
      };
      setFormData(flattenedData);
      setInitialFormData(flattenedData);
    }
  }, [importData]);

  useEffect(() => {
    if (importDetailData) {
      const detailsWithKeys = importDetailData.map((item) => ({
        ...item,
        key: item.id,
      }));
      setDetailFormData(detailsWithKeys);
    }
  }, [importDetailData]);

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const isFormChanged = () => {
    return JSON.stringify(formData) !== JSON.stringify(initialFormData);
  };

  const handleSave = async () => {
    try {
      const bodyRequest = {
        description: formData?.description,
        customerId: formData?.customerId,
        receivedDate: formData?.receivedDate,
      }
      console.log(bodyRequest);
      await updateImport({ id: id, data: bodyRequest, authToken }).unwrap();
      message.success("Import updated successfully!");
      setIsEditing(false);
      setIsModalVisible(false);
    } catch (error) {
      message.error("Failed to update import!");
    }
  };

  const handleDateChange = (date, dateString) => {
    setFormData({
      ...formData,
      receivedDate: dateString,
    });
  };
  const columns = [
    {
      title: "Product Name",
      dataIndex: ["product", "name"],
      key: "productName",
      render: (text) => <span>{text}</span>,
    },
    {
      title: "Description",
      dataIndex: ["product", "description"],
      key: "description",
    },
    {
      title: "Category",
      dataIndex: ["product", "category", "name"],
      key: "category",
    },
    {
      title: "Expiration Date",
      dataIndex: "expiredAt",
      key: "expiredAt",
      render: (text) => (
        <span>{text ? moment(text).format("YYYY-MM-DD") : "None"}</span>
      ),
    },
    {
      title: "Quantity",
      dataIndex: "quantity",
      key: "quantity",
      render: (text) => <span>{text}</span>,
    },
  ];

  const handleCancel = () => {
    setFormData(initialFormData); // Reset form data to the initial data
    setIsEditing(false); // Close editing mode
  };

  return (
    <div className="p-4">
      <Breadcrumbs />
      <div className="flex justify-center items-center">
        <h1 className="font-bold text-3xl py-4 mt-2">Import {id}</h1>
      </div>
      <div className="flex justify-end gap-2">
        {!isEditing ? (
          <>
            {latestImportResponse?.data?.id === importData?.id && (
              <Button
                size="medium"
                type="primary"
                className="flex justify-center items-center mr-4"
                onClick={() => setIsEditing(true)}
              >
                <EditTwoTone /> Edit
              </Button>
            )}
          </>
        ) : (
          <>
            <Button size="medium" onClick={handleCancel}>
              Cancel
            </Button>
            <Button
              size="medium"
              type="primary"
              disabled={!isFormChanged()}
              onClick={() => setIsModalVisible(true)}
            >
              Save
            </Button>
          </>
        )}
      </div>
      <div className="flex flex-wrap gap-4">
        <div className="flex-1 min-w-[300px]">
          <h2 className="font-bold text-xl mb-2">Import Invoice:</h2>
          <div className="mb-2">
            <label className="block font-medium">Description:</label>
            <TextArea
              size="large"
              autoSize={{ minRows: 3 }}
              value={formData?.description}
              disabled={!isEditing}
              onChange={(e) => handleInputChange("description", e.target.value)}
              className="h-[80%]"
            />
          </div>
          <div className="mb-2">
            <label className="block font-medium">Import Type:</label>
            <Select
              size="large"
              value={formData?.importType}
              disabled
              className="w-full h-[80%]"
            >
              <Select.Option value="WAREHOUSE">Warehouse</Select.Option>
              <Select.Option value="CUSTOMER">Customer</Select.Option>
              <Select.Option value="WASTE">Waste</Select.Option>
            </Select>
          </div>
          <div className="mb-2">
            <label className="block font-medium">Import Date:</label>
            <DatePicker
              size="large"
              value={formData?.receivedDate ? dayjs(formData.receivedDate, "YYYY-MM-DD") : null}
              className="w-full h-[80%]"
              disabled={true}
            />
          </div>
          <h2 className="font-bold text-xl mb-2">From Warehouse:</h2>
          <div className="mb-2">
            <label className="block font-medium">Warehouse Description:</label>
            <Input
              size="large"
              value={formData?.warehouseFromDescription}
              disabled
              className="h-[80%]"
            />
          </div>
          <div className="mb-2">
            <label className="block font-medium">Warehouse Name:</label>
            <Input
              size="large"
              value={formData?.warehouseFromName}
              disabled
              className="h-[80%]"
            />
          </div>
          <div className="mb-2">
            <label className="block font-medium">Warehouse Address:</label>
            <Input
              size="large"
              value={formData?.warehouseFromAddress}
              disabled
              className="h-[80%]"
            />
          </div>
        </div>
        <div className="flex-1 min-w-[300px]">
          {formData?.importType === "WAREHOUSE" && importData ? (
            <>
              <h2 className="font-bold text-xl mb-2">To Warehouse:</h2>
              <div className="mb-2">
                <label className="block font-medium">Name:</label>
                <Select
                  size="large"
                  showSearch
                  value={formData.warehouseToName}
                  disabled
                  className="w-full h-[80%]"
                >
                  {warehouseResponse?.data
                    .filter(
                      (warehouse) =>
                        warehouse.id !== importData?.warehouseFrom?.id
                    )
                    .map((warehouse) => (
                      <Select.Option key={warehouse.id} value={warehouse.name}>
                        {warehouse.name}
                      </Select.Option>
                    ))}
                </Select>
              </div>
              <div className="mb-2">
                <label className="block font-medium">Description:</label>
                <Input
                  size="large"
                  value={formData?.warehouseToDescription}
                  disabled
                  className="h-[80%]"
                />
              </div>
              <div className="mb-2">
                <label className="block font-medium">Address:</label>
                <Input
                  size="large"
                  value={formData?.warehouseToAddress}
                  disabled
                  className="h-[80%]"
                />
              </div>
            </>
          ) : (
            <>
              <h2 className="font-bold text-xl mb-2">From Customer:</h2>
              <div className="mb-2">
                <label className="block font-medium">Name:</label>
                <Select
                  size="large"
                  showSearch
                  value={formData.customerName}
                  disabled={!isEditing}
                  onChange={(value) =>
                    handleInputChange("customerId", value)
                  }
                  className="w-full h-[80%]"
                >
                  {customerResponse?.data.map((customer) => (
                    <Select.Option key={customer.id} value={customer.id}>
                      {customer.name}
                    </Select.Option>
                  ))}
                </Select>
              </div>
              <div className="mb-2">
                <label className="block font-medium">Email:</label>
                <Input
                  size="large"
                  value={formData?.customerEmail}
                  disabled
                  className="h-[80%]"
                />
              </div>
              <div className="mb-2">
                <label className="block font-medium">Phone:</label>
                <Input
                  size="large"
                  value={formData?.customerPhone}
                  disabled
                  className="h-[80%]"
                />
              </div>
              <div className="mb-2">
                <label className="block font-medium">Address:</label>
                <Input
                  size="large"
                  value={formData?.customerAddress}
                  disabled
                  className="h-[80%]"
                />
              </div>
            </>
          )}
        </div>
      </div>
      <div className="mt-4">
        <Table
          columns={columns}
          dataSource={detailFormData}
          rowKey="id"
          loading={importDetailResponseLoading}
          pagination={false}
        />
      </div>
      <Modal
        title="Confirm Update"
        visible={isModalVisible}
        onOk={handleSave}
        onCancel={() => setIsModalVisible(false)}
      >
        <p>Are you sure you want to update this import?</p>
      </Modal>
    </div>
  );
}

export default StaffImportDetail;
