import React, { useState, useEffect } from 'react';
import { useGetExportByIdQuery, useGetLatestExportQuery, useUpdateExportByIdMutation } from "../../redux/api/exportApiSlice";
import { useGetAllExportDetailsByExportIdQuery, useUpdateExportDetailsMutation, useDeleteExportDetailsMutation, useCheckQuantityForUpdateMutation } from "../../redux/api/exportDetailApiSlice";
import { useGetAllCustomersQuery } from "../../redux/api/customersApiSlice";
import { useGetAllWarehousesQuery } from "../../redux/api/warehousesApiSlice";
import { useGetAllProductsQuery } from '../../redux/api/productApiSlice';
import { useGetAllInventoriesByWarehouseIdAndProductIdQuery } from '../../redux/api/inventoryApiSlice';
import { useNavigate } from 'react-router-dom';
import { useParams } from "react-router-dom";
import { useSelector } from 'react-redux';
import Breadcrumbs from "../../utils/Breadcumbs";
import { Table, Input, Button, Modal, Select, message, DatePicker } from 'antd';
import { EditTwoTone } from '@mui/icons-material';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import moment from 'moment/moment';
import jsPDF from 'jspdf';
import 'jspdf-autotable';
import logo from '../../assets/images/FPT_logo_2010.png'

const { TextArea } = Input;

function StaffExportDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const userInfo = useSelector((state) => state.auth);
  if (!userInfo) {
    navigate('/', { replace: true });
    return null;
  }

  const authToken = userInfo?.userInfo?.data?.token;
  const wid = userInfo?.userInfo?.data?.warehouseId;

  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({});
  const [initialData, setInitialData] = useState({});
  const [detailFormData, setDetailFormData] = useState([]);
  const [initialDetailData, setInitialDetailData] = useState([]);
  const [selectedProductId, setSelectedProductId] = useState(null);
  const [editedDetails, setEditedDetails] = useState([]);
  const [deletedDetails, setDeletedDetails] = useState([]); // Mảng lưu trữ các ID export detail bị xoá
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [pdfData, setPdfData] = useState(null);

  const { data: exportResponse } = useGetExportByIdQuery({ exportId: id, authToken });
  const { data: exportDetailResponse, isLoading: exportDetailResponseLoading } = useGetAllExportDetailsByExportIdQuery({ authToken, exportId: id });
  const { data: latestExportResponse } = useGetLatestExportQuery({ authToken, warehouseId: wid });
  const { data: customerResponse } = useGetAllCustomersQuery(authToken);
  const { data: warehouseResponse } = useGetAllWarehousesQuery(authToken);
  const { data: productResponse } = useGetAllProductsQuery(authToken);
  const { data: inventoryResponse, refetch: refetchInventories } = useGetAllInventoriesByWarehouseIdAndProductIdQuery({ warehouseId: wid, productId: selectedProductId, authToken }, { skip: !selectedProductId });

  const [updateExport] = useUpdateExportByIdMutation();
  const [checkQuantityForUpdate] = useCheckQuantityForUpdateMutation();
  const [updateExportDetails] = useUpdateExportDetailsMutation();
  const [deleteExportDetails] = useDeleteExportDetailsMutation();

  const exportData = exportResponse?.data;
  const exportDetailData = exportDetailResponse?.data;
  const latestExportData = latestExportResponse?.data;

  useEffect(() => {
    if (exportData) {
      const flattenedData = {
        ...exportData,
        warehouseFromDescription: exportData.warehouseFrom?.description,
        warehouseFromName: exportData.warehouseFrom?.name,
        warehouseFromAddress: exportData.warehouseFrom?.address,
        warehouseToName: exportData.warehouseTo?.name,
        warehouseToDescription: exportData.warehouseTo?.description,
        warehouseToAddress: exportData.warehouseTo?.address,
        customerName: exportData.customer?.name,
        customerEmail: exportData.customer?.email,
        customerPhone: exportData.customer?.phone,
        customerAddress: exportData.customer?.address,
        customerId: exportData.customer?.id,
        warehouseIdTo: exportData.warehouseTo?.id,
      };
      setFormData(flattenedData);
      setInitialData(flattenedData);
    }
  }, [exportData]);

  useEffect(() => {
    if (exportDetailData) {
      const detailsWithKeys = exportDetailData.map((item) => ({
        ...item,
        key: item.id,
      }));
      setDetailFormData(detailsWithKeys);
      setInitialDetailData(detailsWithKeys);
    }
  }, [exportDetailData]);

  const handleInputChange = (e, key) => {
    setFormData({
      ...formData,
      [key]: e.target.value
    });
  };

  const handleDateChange = (date, dateString) => {
    setFormData({
      ...formData,
      exportDate: dateString
    });
  };

  const handleSelectChange = (value, entity, field) => {
    const update = { ...formData };
    update[field] = value;

    // Check for the entity type and update relevant fields
    const selectedEntity = (entity === 'customer' ? customerResponse : warehouseResponse)?.data.find(item => item.name === value);

    if (selectedEntity) {
      if (entity === 'customer') {
        update.customerId = selectedEntity.id; // Ensure customer ID is also updated
        update.customerEmail = selectedEntity.email || '';
        update.customerPhone = selectedEntity.phone || '';
        update.customerAddress = selectedEntity.address || '';
        if (formData.exportType !== 'WAREHOUSE') {
          update.warehouseIdTo = null;
        }
      } else {
        if (formData.exportType === 'WAREHOUSE') {
          update.warehouseIdTo = selectedEntity.id;
          update.warehouseToDescription = selectedEntity.description || '';
          update.warehouseToAddress = selectedEntity.address || '';
        } else {
          update.warehouseIdTo = null;
        }
      }
    }

    setFormData(update);
  };

  const handleDetailInputChange = (e, key, recordId) => {
    setDetailFormData(detailFormData.map(item =>
      item.id === recordId ? { ...item, [key]: e.target.value } : item
    ));

    if (key === 'quantity') {
      setEditedDetails(editedDetails => {
        const existingIndex = editedDetails.findIndex(detail => detail.id === recordId);
        const updatedDetails = [...editedDetails];
        if (existingIndex >= 0) {
          updatedDetails[existingIndex] = { ...updatedDetails[existingIndex], quantity: e.target.value };
        } else {
          updatedDetails.push({ id: recordId, quantity: e.target.value });
        }
        return updatedDetails;
      });
    }
  };

  const handleDetailSelectChange = (value, key, recordId) => {
    if (key === 'productName') {
      const selectedProduct = productResponse?.data.find(product => product.name === value);
      const selectedProductId = selectedProduct?.id;

      setSelectedProductId(selectedProductId);

      setDetailFormData(detailFormData.map(item =>
        item.id === recordId ? {
          ...item,
          product: {
            ...item.product,
            name: value,
            description: selectedProduct?.description || '',
            category: {
              ...item.product.category,
              name: selectedProduct?.category?.name || ''
            }
          }
        } : item
      ));

      refetchInventories();
    } else if (key === 'zone') {
      setDetailFormData(detailFormData.map(item =>
        item.id === recordId ? { ...item, zone: { name: value } } : item
      ));
    } else {
      setDetailFormData(detailFormData.map(item =>
        item.id === recordId ? { ...item, [key]: value } : item
      ));
    }
  };

  const handleDetailDateChange = (value, key, recordId) => {
    setDetailFormData(detailFormData.map(item =>
      item.id === recordId ? { ...item, [key]: value } : item
    ));
  };

  const handleCancelEdit = () => {
    if (JSON.stringify(formData) !== JSON.stringify(initialData) || JSON.stringify(detailFormData) !== JSON.stringify(initialDetailData)) {
      Modal.confirm({
        title: 'Unsaved Changes',
        content: 'You have unsaved changes. Do you really want to cancel?',
        onOk: () => {
          setIsEditing(false);
          setFormData(initialData);
          setDetailFormData(initialDetailData);
          setEditedDetails([]); // Reset edited details
          setDeletedDetails([]); // Reset deleted details
        }
      });
    } else {
      setIsEditing(false);
    }
  };

  const handleConfirmDelete = () => {
    Modal.confirm({
      title: 'Confirm Delete',
      content: 'Are you sure you want to delete this export?',
      onOk: handleDelete
    });
  };

  const handleDelete = () => {
    try {
      // Handle delete logic here
    } catch (error) {
      console.error("handleDelete:", error);
    }
  };

  const handleUpdateExport = async () => {
    try {
      const formattedDate = formData.exportDate ? new Date(formData.exportDate).toISOString() : null;

      const updatedData = {
        description: formData.description,
        type: formData.exportType,
        exportDate: formattedDate,
        warehouseIdTo: formData.exportType === 'WAREHOUSE' ? formData.warehouseIdTo : null,
        customerId: formData.exportType !== 'WAREHOUSE' ? formData.customerId : null,
      };

      const result = await updateExport({ data: updatedData, exportId: id, authToken });

      if (result?.data) {
        message.success('Export updated successfully!');
        setIsEditing(false);
        setInitialData(formData);

        if (editedDetails.length > 0) {
          await handleUpdateExportDetails();
        }

        if (deletedDetails.length > 0) {
          await handleDeleteExportDetails();
        }
      } else {
        throw new Error('Failed to update export');
      }
    } catch (error) {
      console.error('Save error:', error);
      message.error('Failed to update export: ' + error.message);
    }
  }

  const handleUpdateExportDetails = async () => {
    try {
      const requests = editedDetails.map(detail => ({
        exportDetailId: detail.id,
        quantity: detail.quantity,
      }));

      console.log('Edited Details:', requests);

      const result = await updateExportDetails({ data: requests, authToken });

      if (result?.data) {
        message.success('Export details updated successfully!');
        setEditedDetails([]);
      } else {
        throw new Error('Failed to update export details');
      }
    } catch (error) {
      console.error('Update details error:', error);
      message.error('Failed to update export details: ' + error.message);
    }
  }

  const handleDeleteExportDetails = async () => {
    try {
      const result = await deleteExportDetails({ data: deletedDetails, authToken });

      if (result?.data) {
        message.success('Export details deleted successfully!');
        setDeletedDetails([]);
        setDetailFormData(detailFormData.filter(detail => !deletedDetails.includes(detail.id)));
      } else {
        throw new Error('Failed to delete export details');
      }
    } catch (error) {
      console.error('Delete details error:', error);
      message.error('Failed to delete export details: ' + error.message);
    }
  }

  const handleSaveEdit = () => {
    Modal.confirm({
      title: 'Confirm Save',
      content: 'Are you sure you want to save the changes?',
      onOk: () => {
        handleUpdateExport();
        handleUpdateExportDetails();
        handleDeleteExportDetails();
      }
    });
  };

  const handleSaveDetail = async (record) => {
    try {
      const formattedDate = record.expiredAt ? moment(record.expiredAt).utc().format("YYYY-MM-DDTHH:mm:ss.SSSZ") : null;

      const bodyData = {
        exportId: record.id,
        productId: record.product.id,
        zoneId: record.zone.id,
        expiredAt: formattedDate,
        quantity: record.quantity,
      }

      const result = await checkQuantityForUpdate({
        authToken,
        data: bodyData
      });

      if (result?.data.status === 200 && result?.data.message === "Sufficient inventory") {
        message.success(result?.data?.message);
        setEditedDetails(editedDetails => {
          const existingIndex = editedDetails.findIndex(detail => detail.id === record.id);
          const updatedDetails = [...editedDetails];
          if (existingIndex >= 0) {
            updatedDetails[existingIndex] = { ...updatedDetails[existingIndex], quantity: record.quantity };
          } else {
            updatedDetails.push({ id: record.id, quantity: record.quantity });
          }
          return updatedDetails;
        });
        // Hide Save and Cancel buttons, show Delete button
        setDetailFormData(detailFormData.map(item =>
          item.id === record.id ? { ...item, showSaveCancel: false } : item
        ));
      } else if (result?.data?.status !== 200) {
        throw new Error(result?.data?.message);
      }
    } catch (error) {
      console.error('Check quantity error:', error);
      message.error('Failed to check quantity ' + error.message || error);
      handleCancelDetail(record.id);
    }
  }

  const handleCancelDetail = (recordId) => {
    setDetailFormData(detailFormData.map(item =>
      item.id === recordId ? { ...item, quantity: initialDetailData.find(initialItem => initialItem.id === recordId).quantity, showSaveCancel: false } : item
    ));
    setEditedDetails(editedDetails => editedDetails.filter(detail => detail.id !== recordId));
  };

  const handleDeleteDetail = (recordId) => {
    setDeletedDetails([...deletedDetails, recordId]);
    setDetailFormData(detailFormData.filter(item => item.id !== recordId));
  }

  const groupInventories = (inventories) => {
    const groupedInventories = {};

    inventories.forEach((inventory) => {
      const { expiredAt, zone } = inventory;
      const key = expiredAt ? moment(expiredAt).format('YYYY-MM-DD') : 'None';

      if (!groupedInventories[key]) {
        groupedInventories[key] = {};
      }

      if (!groupedInventories[key][zone.name]) {
        groupedInventories[key][zone.name] = [];
      }

      groupedInventories[key][zone.name].push(inventory);
    });

    return groupedInventories;
  };

  const columns = [
    {
      title: 'Product Name',
      dataIndex: ['product', 'name'],
      key: 'productName',
      width: 250,
      render: (text, record) => (
        <span>{text}</span>
      ),
    },
    {
      title: 'Description',
      dataIndex: ['product', 'description'],
      key: 'description',
      width: 250, // Set a fixed width for the column
    },
    {
      title: 'Category',
      dataIndex: ['product', 'category', 'name'],
      key: 'category',
      width: 150, // Set a fixed width for the column
    },
    {
      title: 'Expiration Date',
      dataIndex: 'expiredAt',
      key: 'expiredAt',
      width: 150, // Set a fixed width for the column
      render: (text, record) => (
        <span>{text ? moment(text).format('YYYY-MM-DD') : 'None'}</span>
      )
    },
    {
      title: 'Zone',
      dataIndex: ['zone', 'name'],
      key: 'zone',
      width: 150, // Set a fixed width for the column
      render: (text, record) => (
        <span>{text}</span>
      )
    },
    {
      title: 'Quantity',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 100, // Set a fixed width for the column
      render: (text, record) => (
        isEditing ? (
          <Input
            className='w-full'
            type="number"
            value={text}
            onChange={(e) => handleDetailInputChange(e, 'quantity', record.id)}
          />
        ) : text
      ),
    },
  ];

  if (isEditing) {
    columns.push({
      title: 'Actions',
      key: 'actions',
      width: 150, // Set a fixed width for the column
      render: (text, record) => (
        <span>
          {editedDetails.some(detail => detail.id === record.id) ? (
            <>
              <a className='text-blue-300 hover:text-blue-600 no-underline bg-transparent cursor-pointer transition duration-300' onClick={() => handleSaveDetail(record)}>Save</a>
              <a className='text-red-300 hover:text-red-600 no-underline bg-transparent cursor-pointer transition duration-300 ml-2' onClick={() => handleCancelDetail(record.id)}>Cancel</a>
            </>
          ) : (
            <a className='text-red-300 hover:text-red-600 no-underline bg-transparent cursor-pointer transition duration-300' onClick={() => handleDeleteDetail(record.id)} danger>Delete</a>
          )}
        </span>
      )
    });
  }
  console.log(formData);

  

  const generatePDFData = () => {
    const doc = new jsPDF();
  
    // Add company logo
  
    // Add invoice title and number
    doc.setFontSize(20);
    doc.text('EXPORT INVOICE', 105, 30, null, null, 'center');
    doc.setFontSize(10);
    doc.text(`No. ${formData.id}`, 180, 20);
  
    // Add date
    doc.setFontSize(12);
    doc.text(`Date: ${moment().format('DD MMMM, YYYY')}`, 10, 50);
  
    // Add billed to and from information
    doc.setFontSize(10);
    doc.text('To:', 10, 60);
    if(formData.exportType === 'WAREHOUSE'){
      doc.text(formData.warehouseToName || 'N/A', 10, 65);
      doc.text(formData.warehouseToAddress || 'N/A', 10, 70);
      doc.text(formData.warehouseToDescription || 'N/A', 10, 75);
    }else if(formData.exportType === 'CUSTOMER'){
      doc.text(formData.customerName || 'N/A', 10, 65);
      doc.text(formData.customerAddress || 'N/A', 10, 70);
      doc.text(formData.customerEmail || 'N/A', 10, 75);
      doc.text(formData.customerPhone || 'N/A', 10, 80);
    }
  
    doc.text('From:', 105, 60);
    doc.text(formData.warehouseFromName || 'N/A', 105, 65);
    doc.text(formData.warehouseFromAddress || 'N/A', 105, 70);
    doc.text(formData.warehouseFromDescription || 'N/A', 105, 75);
  
    // Add table
    doc.autoTable({
      head: [['Product Name', 'Description', 'Category', 'Expiration Date', 'Zone', 'Quantity']],
      body: detailFormData.map(item => [
        item.product.name,
        item.product.description,
        item.product.category.name,
        item.expiredAt ? moment(item.expiredAt).format('YYYY-MM-DD') : 'None',
        item.zone.name,
        item.quantity,
      ]),
      startY: 90,
    });

    // Add footer
    doc.setFontSize(10);
    // doc.text('Payment method: Cash', 10, doc.lastAutoTable.finalY + 30);
    doc.text('Note: Thank you for choosing us!', 10, doc.lastAutoTable.finalY + 35);
  
    setPdfData(doc.output('datauristring'));
    setIsModalVisible(true);
  };

  const downloadPDF = () => {
    const doc = new jsPDF();
    doc.fromDataURL(pdfData);
    doc.save(`Export_${id}.pdf`);
    setIsModalVisible(false);
  };

  return (
    <div>
      <Breadcrumbs />
      {/* <h1 className="font-bold text-3xl p-4">Export {id}</h1> */}
      <div className="px-4 overflow-auto">
        <div className='flex justify-end gap-2'>
          {!isEditing ? (
            <>
              <Button size="large" style={{ backgroundColor: '#ff4d4f', color: '#fff', borderColor: '#ff4d4f' }} onClick={generatePDFData}>
                <PictureAsPdfIcon /> Preview
              </Button>
              {exportData?.id === latestExportData?.id && (
                <Button
                  size="large"
                  type="primary"
                  className="flex justify-center items-center mr-4"
                  onClick={() => setIsEditing(true)}
                >
                  <EditTwoTone /> Edit
                </Button>)}
            </>
          ) : (
            <>
              <Button
                size="large"
                onClick={handleCancelEdit}
              >
                Cancel
              </Button>
              <Button
                size="large"
                type="primary" danger
                onClick={handleConfirmDelete}
              >
                Delete
              </Button>
              <Button
                size="large"
                type='primary'
                onClick={handleSaveEdit}
                disabled={JSON.stringify(formData) === JSON.stringify(initialData) && JSON.stringify(detailFormData) === JSON.stringify(initialDetailData)}
              >
                Save
              </Button>

            </>
          )}
        </div>
        <div className='grid grid-cols-2 h-full'>
          <div className='ml-4'>
            <table>
              <p className="mt-3 mb-1 font-bold text-xl">Export Invoice:</p>
              <tr>
                <td><p className='font-medium'>Description:</p></td>
                <td className='w-3/4'><TextArea className='mb-2' size='large' autoSize={{ minRows: 3 }} value={formData?.description} disabled={!isEditing} onChange={(e) => handleInputChange(e, 'description')} /></td>
              </tr>
              <tr>
                <td><p className='font-medium'>Export Type:</p></td>
                <td className='w-3/4'>
                  <Select className='mb-2 w-full' size='large' value={formData?.exportType} disabled={!isEditing}
                    onChange={(value) => handleSelectChange(value, 'exportType', 'exportType')}>
                    <Select.Option value="WAREHOUSE">Warehouse</Select.Option>
                    <Select.Option value="CUSTOMER">Customer</Select.Option>
                    <Select.Option value="WASTE">Waste</Select.Option>
                  </Select>
                </td>
              </tr>
              <tr>
                <td><p className='font-medium'>Export Date:</p></td>
                <td className='w-3/4'>
                  <DatePicker
                    className='mb-2 w-full'
                    size='large'
                    value={formData.exportDate ? moment(formData.exportDate) : null}
                    onChange={handleDateChange}
                    disabled={!isEditing}
                  />
                </td>
              </tr>
              <p className="mt-3 mb-1 font-bold text-xl">Warehouse From:</p>
              <tr>
                <td><p className='font-medium'>Warehouse Description:</p></td>
                <td className='w-3/4'><TextArea className='mb-2' size='large' autoSize={{ minRows: 3 }} value={formData?.warehouseFromDescription} disabled /></td>
              </tr>
              <tr>
                <td><p className='font-medium'>Warehouse Name:</p></td>
                <td className='w-3/4'><Input className='mb-2' size='large' value={formData?.warehouseFromName} disabled /></td>
              </tr>
              <tr>
                <td><p className='font-medium'>Warehouse Address:</p></td>
                <td className='w-3/4'><Input className='mb-2' size='large' value={formData?.warehouseFromAddress} disabled /></td>
              </tr>
            </table>
          </div>
          <div className='ml-4'>
            {formData?.exportType === 'WAREHOUSE' ? (
              <table>
                <td><p className="mt-3 font-bold text-xl">Warehouse To:</p></td>
                <tr>
                  <td><p className='font-medium'>Name:</p></td>
                  <td className='w-3/4'>
                    <Select
                      className='w-full mb-2'
                      size='large'
                      showSearch
                      value={formData.warehouseToName}
                      onChange={(value) => handleSelectChange(value, 'warehouse', 'warehouseToName')}
                      disabled={!isEditing}
                    >
                      {warehouseResponse?.data
                        .filter((warehouse) => warehouse.id !== exportData.warehouseFrom.id)
                        .map((warehouse) => (
                          <Select.Option key={warehouse.id} value={warehouse.name}>
                            {warehouse.name}
                          </Select.Option>
                        ))}
                    </Select>
                  </td>
                </tr>
                <tr>
                  <td><p className='font-medium'>Description:</p></td>
                  <td className='w-3/4'><Input className='mb-2' size='large' value={formData?.warehouseToDescription} disabled /></td>
                </tr>
                <tr>
                  <td><p className='font-medium'>Address:</p></td>
                  <td className='w-3/4'><Input className='mb-2' size='large' value={formData?.warehouseToAddress} disabled /></td>
                </tr>
              </table>
            ) : (
              <table>
                <td><p className="mt-3 font-bold text-xl">Customer:</p></td>
                <tr>
                  <td><p className='font-medium'>Name:</p></td>
                  <td className='w-full'>
                    <Select
                      className='w-full mb-2'
                      size='large'
                      showSearch
                      value={formData.customerName}
                      onChange={(value) => handleSelectChange(value, 'customer', 'customerName')}
                      disabled={!isEditing}
                    >
                      {customerResponse?.data.map((customer) => (
                        <Select.Option key={customer.id} value={customer.name}>
                          {customer.name}
                        </Select.Option>
                      ))}
                    </Select>
                  </td>
                </tr>
                <tr>
                  <td><p className='font-medium'>Email:</p></td>
                  <td className='w-full'><Input className='mb-2' size='large' value={formData?.customerEmail} disabled /></td>
                </tr>
                <tr>
                  <td><p className='font-medium'>Phone:</p></td>
                  <td className='w-full'><Input className='mb-2' size='large' value={formData?.customerPhone} disabled /></td>
                </tr>
                <tr>
                  <td><p className='font-medium'>Address:</p></td>
                  <td className='w-full'><Input className='mb-2' size='large' value={formData?.customerAddress} disabled /></td>
                </tr>
              </table>
            )}
          </div>
        </div >
        <div className="mt-4 py-4">
          <Table
            columns={columns}
            dataSource={detailFormData}
            rowKey="id"
            loading={exportDetailResponseLoading}
            pagination={false}
          />
        </div>
        <Modal
          title="PDF Preview"
          visible={isModalVisible}
          onCancel={() => setIsModalVisible(false)}
          footer={[
            <Button key="cancel" onClick={() => setIsModalVisible(false)}>
              Cancel
            </Button>,
            <Button key="download" type="primary" onClick={() => downloadPDF()}>
              Download
            </Button>,
          ]}
          width={"80%"}
        >
          <iframe
            src={pdfData}
            width="100%"
            height="700px"
            style={{ border: 'none' }}
          ></iframe>
        </Modal>
      </div >
    </div >
  );
}

export default StaffExportDetail;
