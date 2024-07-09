import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Table, Button, Modal, Input, Select, message, Spin } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom'; 
import { useGetAllExportsForAdminQuery, useGetExportByIdQuery } from '../../redux/api/exportApiSlice';
import Loading from '../../utils/Loading';
import Error500 from '../../utils/Error500';

const { Option } = Select;

const OrderExport = () => {
  const [selectedOrderId, setSelectedOrderId] = useState(null);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [searchParams, setSearchParams] = useState({ status: '', sortBy: 'id', direction: 'asc', pageNo: 1 });
  const [pageSize, setPageSize] = useState(5);

  const userInfo = useSelector((state) => state.auth);
  const authToken = userInfo?.userInfo?.data?.token;

  useEffect(() => {
    if (!authToken) {
      message.error('Authorization token is missing. Please log in again.');
    }
  }, [authToken]);

  const {
    data: exportsData,
    error: exportsError,
    isLoading: exportsLoading,
    isFetching: exportsFetching,
  } = useGetAllExportsForAdminQuery({ authToken, ...searchParams }, { skip: !authToken });

  const {
    data: exportDetailsData,
    error: exportDetailsError,
    isLoading: exportDetailsLoading,
    isFetching: exportDetailsFetching,
  } = useGetExportByIdQuery({ exportId: selectedOrderId, authToken }, { skip: !selectedOrderId || !authToken });

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

  if (exportsLoading || exportsFetching) return <Loading />;
  if (exportsError) {
    message.error('Failed to load export data');
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
      dataIndex: ['warehouseFrom', 'name'],
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
        return record.warehouseTo ? 'Inside' : 'Outside';
      },
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (text, record) => (
        <Link to={`/order/export/${record.id}`}>
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
      <h2 class="mb-2 text-2xl font-semibold text-dark">Admin Export</h2>
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
        dataSource={exportsData?.content}
        rowKey='id'
        pagination={{
          current: searchParams.pageNo,
          pageSize,
          total: exportsData?.totalElements,
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
        {exportDetailsLoading || exportDetailsFetching ? (
          <Spin />
        ) : exportDetailsError ? (
          <Error500 />
        ) : (
          <div>
            <p><strong>Order ID:</strong> {exportDetailsData?.data.id}</p>
            <p><strong>Description:</strong> {exportDetailsData?.data.description}</p>
            <p><strong>Status:</strong> {exportDetailsData?.data.status}</p>
            <p><strong>Export Date:</strong> {new Date(exportDetailsData?.data.exportDate).toLocaleString()}</p>
            <p><strong>Export Type:</strong> {exportDetailsData?.data.exportType}</p>
            <p><strong>Transfer Key:</strong> {exportDetailsData?.data.transferKey}</p>
            <p><strong>Warehouse From:</strong> {exportDetailsData?.data.warehouseFrom?.name}</p>
            <p><strong>Warehouse Address:</strong> {exportDetailsData?.data.warehouseFrom?.address}</p>
            <p><strong>Customer Name:</strong> {exportDetailsData?.data.customer.name}</p>
            <p><strong>Customer Email:</strong> {exportDetailsData?.data.customer.email}</p>
            <p><strong>Customer Phone:</strong> {exportDetailsData?.data.customer.phone}</p>
            <p><strong>Customer Address:</strong> {exportDetailsData?.data.customer.address}</p>
          </div>
        )}
      </Modal> */}
    </div>
  );
};

export default OrderExport;
