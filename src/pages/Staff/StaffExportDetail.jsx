import React from 'react';
import { useGetExportByIdQuery } from "../../redux/api/exportApiSlice";
import { useGetAllExportDetailsByExportIdQuery } from "../../redux/api/exportDetailApiSlice";
import { useNavigate } from 'react-router-dom';
import { useParams } from "react-router-dom";
import { useSelector } from 'react-redux';
import Breadcrumbs from "../../utils/Breadcumbs";
import { Table, Input, Button } from 'antd';
import { FormatTime } from '../../utils/FormatTime';
import { EditTwoTone } from '@mui/icons-material';

function StaffExportDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const userInfo = useSelector((state) => state.auth);
  if (!userInfo) {
    return navigate('/', { replace: true });
  }
  let authToken;
  let wid;
  if (userInfo && userInfo.userInfo && userInfo.userInfo.data) {
    authToken = userInfo.userInfo.data.token;
    wid = userInfo.userInfo.data.warehouseId;
  }

  const { data: exportResponse, isLoading: exportResponseLoading, error: exportResponseError } = useGetExportByIdQuery({ exportId: id, authToken: authToken });
  const { data: exportDetailData, isLoading: exportDetailDataLoading, error: exportDetailDataError } = useGetAllExportDetailsByExportIdQuery({ authToken: authToken, exportId: id });
  const exportData = exportResponse?.data;
  const exportDetail = exportDetailData?.data;

  const columns = [
    {
      title: 'Product Name',
      dataIndex: ['product', 'name'],
      key: 'productName',
    },
    {
      title: 'Description',
      dataIndex: ['product', 'description'],
      key: 'description',
    },
    {
      title: 'Category',
      dataIndex: ['product', 'category', 'name'],
      key: 'category',
    },
    {
      title: 'Quantity',
      dataIndex: 'quantity',
      key: 'quantity',
    },
    {
      title: 'Expiration Date',
      dataIndex: 'expiredAt',
      key: 'expiredAt',
    },
    {
      title: 'Zone',
      dataIndex: ['zone', 'name'],
      key: 'zone',
    },
  ];

  return (
    <div>
      <Breadcrumbs />
      <h1 className="font-bold text-3xl p-4">Export {id}</h1>
      <div className="px-4">
        <div className='flex justify-end'>
          <Button
            type="primary"
            className="flex justify-center items-center mr-4"
          >
            <EditTwoTone />
          </Button>
        </div>
        <div className='grid grid-cols-2 h-full'>
          <div className='ml-4'>
            <table>
              <p className="mt-3 mb-1 font-bold text-xl"><strong>Export Invoice:</strong></p>
              <tr>
                <td><p><strong>Description:</strong></p></td>
                <td className='w-3/4'><Input value={exportData?.description} disabled /></td>
              </tr>
              <tr>
                <td><p><strong>Warehouse From:</strong></p></td>
                <td className='w-3/4'><Input value={exportData?.warehouseFrom?.name} disabled /></td>
              </tr>
              <tr>
                <td><p><strong>Export Date:</strong></p></td>
                <td className='w-3/4'><Input value={FormatTime(exportData?.exportDate)} disabled /></td>
              </tr>
              <p className="mt-3 mb-1 font-bold text-xl"><strong>Warehouse From:</strong></p>
              <tr>
                <td><p><strong>Warehouse Description:</strong></p></td>
                <td className='w-3/4'><Input value={exportData?.warehouseFrom?.description} disabled /></td>
              </tr>
              <tr>
                <td><p><strong>Export Type:</strong></p></td>
                <td className='w-3/4'><Input value={exportData?.exportType} disabled /></td>
              </tr>
              <tr>
                <td><p><strong>Warehouse Address:</strong></p></td>
                <td className='w-3/4'><Input value={exportData?.warehouseFrom?.address} disabled /></td>
              </tr>
            </table>
          </div>
          <div className='ml-4'>
            {exportData?.exportType === 'WAREHOUSE' ? (
              <table>
                <td><p className="mt-3 font-bold text-xl"><strong>Warehouse To:</strong></p></td>
                <tr>
                  <td><p><strong>Name:</strong></p></td>
                  <td className='w-3/4'><Input value={exportData?.warehouseTo?.name} disabled /></td>
                </tr>
                <tr>
                  <td><p><strong>Description:</strong></p></td>
                  <td className='w-3/4'><Input value={exportData?.warehouseTo?.description} disabled /></td>
                </tr>
                <tr>
                  <td><p><strong>Address:</strong></p></td>
                  <td className='w-3/4'><Input value={exportData?.warehouseTo?.address} disabled /></td>
                </tr>
              </table>
            ) : (
              <table>
                <td><p className="mt-3 font-bold text-xl"><strong>Customer:</strong></p></td>
                <tr>
                  <td><p><strong>Name:</strong></p></td>
                  <td className='w-full'><Input value={exportData?.customer?.name} disabled /></td>
                </tr>
                <tr>
                  <td><p><strong>Email:</strong></p></td>
                  <td className='w-full'><Input value={exportData?.customer?.email} disabled /></td>
                </tr>
                <tr>
                  <td><p><strong>Phone:</strong></p></td>
                  <td className='w-full'><Input value={exportData?.customer?.phone} disabled /></td>
                </tr>
                <tr>
                  <td><p><strong>Address:</strong></p></td>
                  <td className='w-full'><Input value={exportData?.customer?.address} disabled /></td>
                </tr>
              </table>
            )}
          </div>
        </div>
        <div className="py-4">
          <Table
            columns={columns}
            dataSource={exportDetail}
            rowKey="id"
            loading={exportDetailDataLoading}
            pagination={false}
          />
        </div>
      </div>
    </div>
  );
}

export default StaffExportDetail;
