import React, { useEffect, useState } from "react";
import { useGetImportByIdQuery } from "../../redux/api/importApiSlice";
import { useGetImportDetailsByImportIdQuery } from "../../redux/api/importDetailApiSlice";
import { useGetAllCustomersQuery } from "../../redux/api/customersApiSlice";
import { useGetAllWarehousesQuery } from "../../redux/api/warehousesApiSlice";
import { useParams } from "react-router-dom";
import { useSelector } from "react-redux";
import Breadcrumbs from "../../utils/Breadcumbs";
import { Table, Input, Select, DatePicker } from "antd";
import moment from "moment/moment";

const { TextArea } = Input;

function StaffImportDetail() {
  const { id } = useParams();

  const userInfo = useSelector((state) => state.auth);
  const authToken = userInfo?.userInfo?.data?.token;

  const [formData, setFormData] = useState({});
  const [detailFormData, setDetailFormData] = useState([]);

  const { data: importResponse } = useGetImportByIdQuery({
    importId: id,
    authToken,
  });
  const { data: importDetailResponse, isLoading: importDetailResponseLoading } =
    useGetImportDetailsByImportIdQuery({ authToken, importId: id });
  const { data: customerResponse } = useGetAllCustomersQuery(authToken);
  const { data: warehouseResponse } = useGetAllWarehousesQuery(authToken);

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
      title: "Zone",
      dataIndex: ["zone", "name"],
      key: "zone",
      render: (text) => <span>{text}</span>,
    },
    {
      title: "Quantity",
      dataIndex: "quantity",
      key: "quantity",
      render: (text) => <span>{text}</span>,
    },
  ];

  return (
    <div className="p-4">
      <Breadcrumbs />
      <h1 className="text-3xl font-bold mb-4"></h1>
      <br></br>
      <div className="flex flex-wrap gap-4">
        <div className="flex-1 min-w-[300px]">
          <h2 className="font-bold text-xl mb-2">Import Invoice:</h2>
          <div className="mb-2">
            <label className="block font-medium">Description:</label>
            <TextArea
              size="large"
              autoSize={{ minRows: 3 }}
              value={formData?.description}
              disabled
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
              value={
                formData.importDate
                  ? moment(formData.importDate, "YYYY-MM-DD")
                  : null
              }
              disabled
              className="w-full h-[80%]"
            />
          </div>
          <h2 className="font-bold text-xl mb-2">Warehouse From:</h2>
          <div className="mb-2">
            <label className="block font-medium">Warehouse Description:</label>
            <TextArea
              size="large"
              autoSize={{ minRows: 3 }}
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
          {formData?.importType === "WAREHOUSE" ? (
            <>
              <h2 className="font-bold text-xl mb-2">Warehouse To:</h2>
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
                        warehouse.id !== importData.warehouseFrom.id
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
              <h2 className="font-bold text-xl mb-2">Customer:</h2>
              <div className="mb-2">
                <label className="block font-medium">Name:</label>
                <Select
                  size="large"
                  showSearch
                  value={formData.customerName}
                  disabled
                  className="w-full h-[80%]"
                >
                  {customerResponse?.data.map((customer) => (
                    <Select.Option key={customer.id} value={customer.name}>
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
    </div>
  );
}

export default StaffImportDetail;
