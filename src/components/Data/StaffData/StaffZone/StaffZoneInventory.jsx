import React from 'react';
import { useParams } from 'react-router-dom';
import { Table, Typography } from 'antd';
import { useSelector } from 'react-redux';
import { useGetInventoriesByZoneIdQuery } from '../../../../redux/api/inventoryApiSlice';
import Loading from '../../../../utils/Loading';
import Error500 from '../../../../utils/Error500';
import Breadcrumbs from '../../../../utils/Breadcumbs';

const { Title } = Typography;

const StaffZoneInventory = () => {
  const { zoneid } = useParams();
  const userInfo = useSelector((state) => state.auth);
  let authToken;
  if (userInfo && userInfo.userInfo && userInfo.userInfo.data) {
    authToken = userInfo.userInfo.data.token;
  }

  const { data: inventories, isLoading, error } = useGetInventoriesByZoneIdQuery({ id: zoneid, authToken });

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
      title: 'Held Quantity',
      dataIndex: 'heldQuantity',
      key: 'heldQuantity',
      sorter: (a, b) => a.heldQuantity - b.heldQuantity,
    },
    {
      title: 'Expired At',
      dataIndex: 'expiredAt',
      key: 'expiredAt',
      render: (text) => (text ? new Date(text).toLocaleDateString() : 'N/A'),
      sorter: (a, b) => new Date(a.expiredAt) - new Date(b.expiredAt),
    },
  ];

  return (
    <div>
        <Breadcrumbs/>
      <Title level={2}>Inventory List for Zone {zoneid}</Title>
      {isLoading ? (
        <Loading />
      ) : error ? (
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
