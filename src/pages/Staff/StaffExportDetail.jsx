import React, { useState, useEffect } from 'react';
import Breadcrumbs from '../../utils/Breadcumbs';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { useGetExportByIdQuery, useUpdateExportByIdMutation } from "../../redux/api/exportApiSlice";
import { useGetAllExportDetailsByExportIdQuery } from "../../redux/api/exportDetailApiSlice";
import ExportStatus from "../../components/Orders/ExportStatus";
import '../../components/Orders/MainDash.css';
import { Table, Button, Modal, Input, Select, message } from 'antd';
import EditNoteIcon from '@mui/icons-material/EditNote';
import ChecklistIcon from '@mui/icons-material/Checklist';
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';

const { TextArea } = Input;
const { Option } = Select;

function StaffExportDetail() {
    const { id } = useParams();
    const userInfo = useSelector((state) => state.auth.userInfo);
    const authToken = userInfo?.data?.token;

    const { data: exportsDataRes = {}, isLoading: exportsLoading, error: exportsError } = useGetExportByIdQuery({
        authToken,
        exportId: id,
    });
    const { data: exportProductsData = {}, isLoading: exportProductsLoading, error: exportProductsError } = useGetAllExportDetailsByExportIdQuery({
        authToken,
        exportId: id,
    });

    const exportData = exportsDataRes.data || {};
    const exportProducts = exportProductsData.data || [];

    const [isProductListPopupVisible, setIsProductListPopupVisible] = useState(false);
    const [isConfirmationPopupVisible, setIsConfirmationPopupVisible] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [editableData, setEditableData] = useState(exportData);
    const [confirmationAction, setConfirmationAction] = useState(null);

    const [updateExportById, { isLoading: isUpdating }] = useUpdateExportByIdMutation();

    useEffect(() => {
        setEditableData(exportData);
    }, [exportData]);

    const handleOpenProductListPopup = () => setIsProductListPopupVisible(true);
    const handleCloseProductListPopup = () => setIsProductListPopupVisible(false);

    const handleOpenConfirmationPopup = (action) => {
        setConfirmationAction(action);
        setIsConfirmationPopupVisible(true);
    };
    const handleCloseConfirmationPopup = () => setIsConfirmationPopupVisible(false);

    const handleOpenEditMode = () => {
        setIsEditMode(true);
        setEditableData(exportData);
    };

    const handleCancelEdit = () => {
        setConfirmationAction({ type: 'cancelEditMode' });
        setIsConfirmationPopupVisible(true);
    };

    const handleSaveEdit = () => {
        setConfirmationAction({ type: 'updateExport' });
        setIsConfirmationPopupVisible(true);
    };

    const handleChange = (field, value) => {
        setEditableData({
            ...editableData,
            [field]: value,
        });
    };

    const handleDeleteExportDetail = async (record) => {
        console.log('Deleting record:', record);
        handleCloseConfirmationPopup();
    };

    const handleApproveExport = async () => {
        console.log('Approving export:', id);
        handleCloseConfirmationPopup();
    };

    const handleUpdateExport = async () => {
        try {
            await updateExportById({
                exportId: id,
                authToken,
                data: {
                    description: editableData.description,
                    status: editableData.status,
                    exportDate: new Date(editableData.exportDate).toISOString(),
                    customerId: exportData.customer.id,
                    customerName: editableData.customer.name,
                    customerAddress: editableData.customer.address,
                    customerPhone: editableData.customer.phone,
                    customerEmail: editableData.customer.email
                }
            }).unwrap();
            setIsEditMode(false);
            handleCloseConfirmationPopup();
            message.success('Export updated successfully');
        } catch (error) {
            message.error('Failed to update export');
            console.error('Update export error:', error);
        }
    };

    const handleDelete = (record) => {
        handleOpenConfirmationPopup({ type: 'deleteProduct', record });
    };

    const handleApprove = () => {
        handleOpenConfirmationPopup({ type: 'approveExport' });
    };

    const handleConfirmAction = async () => {
        switch (confirmationAction.type) {
            case 'deleteProduct':
                await handleDeleteExportDetail(confirmationAction.record);
                break;
            case 'approveExport':
                await handleApproveExport();
                break;
            case 'cancelEditMode':
                setIsEditMode(false);
                handleCloseConfirmationPopup();
                break;
            case 'updateExport':
                await handleUpdateExport();
                break;
            default:
                break;
        }
    };

    const columns = [
        {
            title: 'Product Name',
            dataIndex: ['product', 'name'],
            key: 'productName',
        },
        {
            title: 'Product Description',
            dataIndex: ['product', 'description'],
            key: 'productDescription',
        },
        {
            title: 'Product Category',
            dataIndex: ['product', 'category', 'name'],
            key: 'productCategory',
        },
        {
            title: 'Quantity',
            dataIndex: 'quantity',
            key: 'quantity',
        },
        {
            title: 'Expired At',
            dataIndex: 'expiredAt',
            key: 'expiredAt',
            render: (text) => new Date(text).toLocaleDateString(),
        },
    ];

    if (isEditMode) {
        columns.push({
            title: 'Action',
            key: 'action',
            render: (_, record) => (
                <Button
                    icon={<DeleteRoundedIcon style={{ color: "#ef4444" }} />}
                    onClick={() => handleDelete(record)}
                />
            ),
        });
    }

    if (exportsLoading) return <div>Loading...</div>;
    if (exportsError) return <div>Error loading export data</div>;

    const modalConfirmTitle = {
        deleteProduct: 'Do you want to delete this product?',
        approveExport: 'Do you want to approve this export?',
        cancelEditMode: 'Do you want to cancel editing?',
        updateExport: 'Do you want to save changes?'
    }[confirmationAction?.type] || 'Confirm Action';

    return (
        <div>
            <div className='container'>
                <Breadcrumbs />
                <h1>Export {id}</h1>
                <table className="table-detail">
                    <tbody>
                        <tr>
                            <td className="export-attribute-title">Description:</td>
                            <td>
                                {isEditMode ? (
                                    <TextArea
                                        rows={4}
                                        style={{ width: '100%' }}
                                        value={editableData.description}
                                        onChange={(e) => handleChange('description', e.target.value)}
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
                                        onChange={(value) => handleChange('status', value)}
                                        style={{ width: '100%' }}
                                    >
                                        <Option value="PENDING">PENDING</Option>
                                        <Option value="SHIPPING">SHIPPING</Option>
                                        <Option value="SUCCEED">SUCCEED</Option>
                                        <Option value="CANCEL">CANCEL</Option>
                                    </Select>
                                ) : (
                                    <p><ExportStatus status={exportData.status} /></p>
                                )}
                            </td>
                        </tr>
                        <tr>
                            <td className="export-attribute-title">Export Date:</td>
                            <td>
                                {isEditMode ? (
                                    <Input
                                        type="date"
                                        style={{ width: '100%' }}
                                        value={new Date(editableData.exportDate).toISOString().split('T')[0]}
                                        onChange={(e) => handleChange('exportDate', e.target.value)}
                                    />
                                ) : (
                                    <p>{new Date(exportData.exportDate).toLocaleDateString()}</p>
                                )}
                            </td>
                        </tr>
                        <tr>
                            <td className="export-attribute-title">Export Type:</td>
                            <td><p>{exportData.exportType}</p></td>
                        </tr>
                        <tr>
                            <td className="export-attribute-title">To:</td>
                            <td>
                                {isEditMode && exportData.exportType === 'CUSTOMER' ? (
                                    <Input
                                        style={{ width: '100%' }}
                                        value={exportData.customer.address}
                                        onChange={(e) => handleChange('customerAddress', e.target.value)}
                                    />
                                ) : (
                                    <p>
                                        {exportData.exportType === 'CUSTOMER' ?
                                            exportData.customer.address :
                                            exportData.warehouseTo.address
                                        }
                                    </p>
                                )}
                            </td>
                        </tr>
                        {exportData.exportType === 'CUSTOMER' && (
                            <div>
                                <tr>
                                    <td className="export-attribute-title">Customer:</td>
                                    <td>
                                        {isEditMode ? (
                                            <Input
                                                style={{ width: '100%' }}
                                                value={exportData.customer.name}
                                                onChange={(e) => handleChange('customerName', e.target.value)}
                                            />
                                        ) : (
                                            <p>{exportData.customer.name}</p>
                                        )}
                                    </td>
                                </tr>
                                <tr>
                                    <td className="export-attribute-title">Customer Email:</td>
                                    <td>
                                        {isEditMode ? (
                                            <Input
                                                style={{ width: '100%' }}
                                                value={exportData.customer.email}
                                                onChange={(e) => handleChange('customerEmail', e.target.value)}
                                            />
                                        ) : (
                                            <p>{exportData.customer.email}</p>
                                        )}
                                    </td>
                                </tr>
                                <tr>
                                    <td className="export-attribute-title">Customer Phone:</td>
                                    <td>
                                        {isEditMode ? (
                                            <Input
                                                style={{ width: '100%' }}
                                                value={exportData.customer.phone}
                                                onChange={(e) => handleChange('customerPhone', e.target.value)}
                                            />
                                        ) : (
                                            <p>{exportData.customer.phone}</p>
                                        )}
                                    </td>
                                </tr>
                            </div>
                        )}
                    </tbody>
                </table>
                <div className="export-button-container">
                    <a className='no-select' href='#' onClick={handleOpenProductListPopup}>
                        View Export Details
                    </a>
                    <div>
                        {isEditMode ? (
                            <div className="action-buttons">
                                <Button
                                    style={{
                                        background: '#ef4444',
                                        color: 'white',
                                    }}
                                    type="default"
                                    onClick={handleCancelEdit}
                                >
                                    Cancel
                                </Button>
                                <Button
                                    style={{
                                        background: '#16a34a',
                                        color: 'white',
                                    }}
                                    type="default"
                                    onClick={handleSaveEdit}
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
                                                background: '#0284c7',
                                                color: 'white',
                                            }}
                                            type="default"
                                            onClick={handleOpenEditMode}
                                        >
                                            <EditNoteIcon /> Edit
                                        </Button>
                                        <Button
                                            style={{
                                                background: '#15803d',
                                                color: 'white',
                                            }}
                                            type="default"
                                            onClick={handleApprove}
                                        >
                                            <ChecklistIcon />Approve
                                        </Button>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </div>
            <Modal
                title="Export Details"
                width={800}
                className="custom-modal"
                visible={isProductListPopupVisible}
                onCancel={handleCloseProductListPopup}
                footer={[
                    <Button key="close" onClick={handleCloseProductListPopup}>
                        Close
                    </Button>
                ]}
            >
                <Table
                    dataSource={exportProducts}
                    columns={columns}
                    rowKey="id"
                    pagination={false}
                />
            </Modal>
            <Modal
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
                                background: '#3b82f6',
                                color: 'white',
                            }}
                            type="default"
                            onClick={handleConfirmAction}
                        >
                            Confirm
                        </Button>
                    </div>
                ]}
            >
            </Modal>
        </div>
    );
}

export default StaffExportDetail;