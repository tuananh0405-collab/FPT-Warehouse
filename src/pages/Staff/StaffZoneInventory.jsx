import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Table, Typography, Button } from 'antd';
import { useSelector } from 'react-redux';
import { useGetInventoriesByZoneIdQuery } from '../../redux/api/inventoryApiSlice';
import Loading from '../../utils/Loading';
import Error500 from '../../utils/Error500';
import Breadcrumbs from '../../utils/Breadcumbs';
import { useGetZoneByIdQuery } from '../../redux/api/zoneApiSlice';

const { Title } = Typography;

const StaffZoneInventory = () => {
  const { zoneid } = useParams();
  const navigate = useNavigate();
  const userInfo = useSelector((state) => state.auth);
  let authToken;
  let wid;
  if (userInfo && userInfo.userInfo && userInfo.userInfo.data) {
    authToken = userInfo.userInfo.data.token;
    wid = userInfo.userInfo.data.warehouseId;
  }

  const { data: inventories, isLoading, error } = useGetInventoriesByZoneIdQuery({ id: zoneid, authToken });
  const { data: zone, isLoading2, error2 } = useGetZoneByIdQuery({ id: zoneid, authToken });
  console.log(zone);

  const {data: zone, zoneIsLoading, zoneError} = useGetZoneByWarehouseIdQuery({ id: wid, authToken });
  console.log(zone)
  const zoneName = zone?.data.find((z) => z.id === parseInt(zoneid))?.name;
  console.log(zoneid+zoneName)


  const handleTransfer = (productId, zoneId) => {
    navigate(`/staff/transfer?productId=${productId}&zoneId=${zoneId}`);
  };

  const columns = [
    {
      title: 'Product Name',
      dataIndex: ['product', 'name'],
      key: 'product.name',
      sorter: (a, b) => a.product.name.localeCompare(b.product.name),
    },
    {
      title: 'Quantity',
      dataIndex: 'quantity',
      key: 'quantity',
      sorter: (a, b) => a.quantity - b.quantity,
    },
    {
      title: 'Expired At',
      dataIndex: 'expiredAt',
      key: 'expiredAt',
      render: (text) => (text ? new Date(text).toLocaleDateString() : 'N/A'),
      sorter: (a, b) => new Date(a.expiredAt) - new Date(b.expiredAt),
    },
    {
      title: 'Transfer',
      key: 'transfer',
      render: (_, record) => (
        <Button onClick={() => handleTransfer(record.product.id, zoneid)}>
          Transfer
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Breadcrumbs />
      <Title level={2}>Inventory List for {zone?.data?.name}</Title>
      {isLoading ? (
        <Loading />
      ) : error || zoneError ? (
        <Error500 />
      ) : (
        <Table
          columns={columns}
          dataSource={inventories}
          rowKey="id"
          pagination={true}
        />
      )}
    </div>
  );
};

export default StaffZoneInventory;
