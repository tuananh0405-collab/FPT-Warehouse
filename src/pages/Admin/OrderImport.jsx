import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { Table, Button, Modal, Input, Select, message, Spin } from "antd";
import { Link } from 'react-router-dom'; 
import { SearchOutlined } from "@ant-design/icons";
import { useGetAllImportsQuery, useGetImportByIdQuery } from "../../redux/api/importApiSlice";
import Loading from "../../utils/Loading";
import Error500 from "../../utils/Error500";
import useDocumentTitle from "../../utils/UseDocumentTitle";

const { Option } = Select;

const OrderImport = () => {
  useDocumentTitle('Import')
  const [selectedOrderId, setSelectedOrderId] = useState(null);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [searchParams, setSearchParams] = useState({ status: "", sortBy: "id", direction: "asc", pageNo: 1 });
  const [pageSize, setPageSize] = useState(20);

  const userInfo = useSelector((state) => state.auth);
  const authToken = userInfo?.userInfo?.data?.token;

  useEffect(() => {
    if (!authToken) {
      message.error("Authorization token is missing. Please log in again.");
    }
  }, [authToken]);

  const {
    data: importsData,
    error: importsError,
    isLoading: importsLoading,
    isFetching: importsFetching,
  } = useGetAllImportsQuery({ authToken, ...searchParams }, { skip: !authToken });

  const {
    data: importDetailsData,
    error: importDetailsError,
    isLoading: importDetailsLoading,
    isFetching: importDetailsFetching,
  } = useGetImportByIdQuery(
    { importId: selectedOrderId, authToken },
    { skip: !selectedOrderId || !authToken }
  );

  const handleRowClick = (record) => {
    setSelectedOrderId(record.id);
    setIsModalVisible(true);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
    setSelectedOrderId(null);
  };

  if (!authToken) {
    return <Error500 message="Authorization token is missing." />;
  }

  if (importsLoading || importsFetching) return <Loading />;
  if (importsError) {
    message.error("Failed to load import data");
    return <Error500 />;
  }

  const columns = [
    {
      title: 'Order ID',
      dataIndex: 'id',
      key: 'id',
      sorter: true,
    },
    {
      title: 'Warehouse Name',
      dataIndex: ['warehouseTo', 'name'],
      key: 'warehouseName',
      sorter: true,
    },
    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
      sorter: true,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      sorter: true,
      render: (text) => {
        let color;
        switch (text) {
          case 'PENDING':
            color = 'orange';
            break;
          case 'SHIPPING':
            color = 'blue';
            break;
          case 'SUCCEED':
            color = 'green';
            break;
          case 'CANCEL':
            color = 'red';
            break;
          default:
            color = 'grey';
        }
        return <span style={{ color }}>{text}</span>;
      },
    },
    {
      title: 'Type',
      key: 'type',
      render: (text, record) => {
        return record.warehouseTo ? 'Outside' : 'Inside';
      },
    },
    {
        title: 'Actions',
        key: 'actions',
        render: (text, record) => (
          <Link to={`/order/import/${record.id}`}>
            <Button type='link'>View Details</Button>
          </Link>
        ),
      },
  ];

  const sortOptions = [
    { label: 'ID Ascending', value: 'id-asc' },
    { label: 'ID Descending', value: 'id-desc' },
    { label: 'Warehouse Name Ascending', value: 'warehouseName-asc' },
    { label: 'Warehouse Name Descending', value: 'warehouseName-desc' },
    { label: 'Status Ascending', value: 'status-asc' },
    { label: 'Status Descending', value: 'status-desc' },
  ];

  return (
    <div style={{ padding: '20px' }}>
      <h2 class="mb-2 text-2xl font-semibold text-dark">Admin Import</h2>
      <div style={{ marginBottom: '20px', display: 'flex', justifyContent: 'space-between' }}>
        <Input
          placeholder='Search by Status'
          onChange={(e) => setSearchParams((prev) => ({ ...prev, status: e.target.value, pageNo: 1 }))}
          style={{ width: '200px' }}
          prefix={<SearchOutlined />}
        />
        <Select
          placeholder='Sort By'
          onChange={(value) => {
            const [field, order] = value.split('-');
            setSearchParams((prev) => ({ ...prev, sortBy: field, direction: order, pageNo: 1 }));
          }}
          style={{ width: '200px' }}
          value={`${searchParams.sortBy}-${searchParams.direction}`}
        >
          {sortOptions.map((option) => (
            <Option key={option.value} value={option.value}>
              {option.label}
            </Option>
          ))}
        </Select>
      </div>
      <Table
        columns={columns}
        dataSource={importsData?.data}
        rowKey='id'
        pagination={{
          current: searchParams.pageNo,
          pageSize,
          total: importsData?.totalElements,
          onChange: (page, pageSize) => setSearchParams((prev) => ({ ...prev, pageNo: page })),
        }}
        onChange={(pagination, filters, sorter) => {
          if (sorter.order) {
            const field = sorter.field;
            const direction = sorter.order === 'ascend' ? 'asc' : 'desc';
            setSearchParams((prev) => ({ ...prev, sortBy: field, direction, pageNo: 1 }));
          }
        }}
      />
      {/* <Modal
        title='Order Details'
        visible={isModalVisible}
        onCancel={handleCancel}
        footer={[
          <Button key='back' onClick={handleCancel}>
            Close
          </Button>,
        ]}
      >
        {importDetailsLoading || importDetailsFetching ? (
          <Spin />
        ) : importDetailsError ? (
          <Error500 />
        ) : (
          <div>
            <p><strong>Order ID:</strong> {importDetailsData?.data.id}</p>
            <p><strong>Description:</strong> {importDetailsData?.data.description}</p>
            <p><strong>Status:</strong> {importDetailsData?.data.status}</p>
            <p><strong>Received Date:</strong> {new Date(importDetailsData?.data.receivedDate).toLocaleString()}</p>
            <p><strong>Import Type:</strong> {importDetailsData?.data.importType}</p>
            <p><strong>Transfer Key:</strong> {importDetailsData?.data.transferKey}</p>
            <p><strong>Warehouse To:</strong> {importDetailsData?.data.warehouseTo?.name}</p>
            <p><strong>Warehouse Address:</strong> {importDetailsData?.data.warehouseTo?.address}</p>
            <p><strong>Customer Name:</strong> {importDetailsData?.data.customer.name}</p>
            <p><strong>Customer Email:</strong> {importDetailsData?.data.customer.email}</p>
            <p><strong>Customer Phone:</strong> {importDetailsData?.data.customer.phone}</p>
            <p><strong>Customer Address:</strong> {importDetailsData?.data.customer.address}</p>
          </div>
        )}
      </Modal> */}
    </div>
  );
};

export default OrderImport;
