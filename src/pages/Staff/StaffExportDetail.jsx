import React, { useState, useEffect } from 'react';
import Breadcrumbs from '../../utils/Breadcumbs';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import {
    useGetExportByIdQuery,
    useUpdateExportByIdMutation
} from "../../redux/api/exportApiSlice";
import {
    useGetAllExportDetailsByExportIdQuery,
    useDeleteExportDetailsMutation,
    useCheckAvailableQuantityMutation,
    useUpdateExportDetailsMutation
} from "../../redux/api/exportDetailApiSlice";
import ExportStatus from "../../components/Orders/ExportStatus";
import '../../components/Orders/MainDash.css';
import { Table, Button, Modal, Input, Select, message, Checkbox } from 'antd';
import EditNoteIcon from '@mui/icons-material/EditNote';
import ChecklistIcon from '@mui/icons-material/Checklist';
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';
import { useGetAllWarehousesQuery } from "../../redux/api/warehousesApiSlice";

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
    const {
        data: warehouses,
        isFetching: isWarehouseLoading,
        error: warehouseError,
    } = useGetAllWarehousesQuery(authToken);

    const exportData = exportsDataRes.data || {};
    const exportProducts = exportProductsData.data || [];
    const warehousesData = warehouses?.data || [];

    console.log('exportData', exportData);

    const [isProductListPopupVisible, setIsProductListPopupVisible] = useState(false);
    const [isConfirmationPopupVisible, setIsConfirmationPopupVisible] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [editableData, setEditableData] = useState({
        ...exportData,
        warehouseIdTo: exportData.warehouseTo ? exportData.warehouseTo.id : null
    });
    const [confirmationAction, setConfirmationAction] = useState(null);
    const [selectedProductIds, setSelectedProductIds] = useState([]);
    const [temporarilyHiddenProductIds, setTemporarilyHiddenProductIds] = useState([]);
    const [editableRow, setEditableRow] = useState(null);
    const [editableRowData, setEditableRowData] = useState({});
    const [localUpdatedQuantities, setLocalUpdatedQuantities] = useState({});

    const [updateExportById, { isLoading: isUpdating }] = useUpdateExportByIdMutation();
    const [deleteExportDetails, { isLoading: isDeleting }] = useDeleteExportDetailsMutation();
    const [updateExportDetails] = useUpdateExportDetailsMutation();
    const [checkAvailableQuantity] = useCheckAvailableQuantityMutation();

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

    const handleSaveRowEdit = async (record) => {
        const { product, quantity } = editableRowData;
        const checkResponse = await checkAvailableQuantity({
            authToken,
            data: {
                productId: product.id,
                quantity,
                warehouseId: exportData.warehouseFrom.id
            }
        }).unwrap();
        if (checkResponse.status === 200 && checkResponse.message === 'Enough quantity') {
            setLocalUpdatedQuantities({
                ...localUpdatedQuantities,
                [record.id]: { exportDetailId: record.id, quantity },
            });
            setEditableRow(null);
            setEditableRowData({});
            message.success('Quantity update is valid');
        } else {
            message.error('Not enough quantity in this warehouse');
        }
    };

    const handleSaveEdit = async () => {
        // Handle deletions export details
        if (temporarilyHiddenProductIds.length > 0) {
            try {
                await deleteExportDetails({ authToken, ids: temporarilyHiddenProductIds }).unwrap();
                setTemporarilyHiddenProductIds([]);
            } catch (error) {
                message.error('Failed to delete selected products');
                console.error('Delete export details error:', error);
            }
        }

        const updateData = {
            description: editableData.description,
            status: editableData.status,
            exportDate: new Date(editableData.exportDate).toISOString(),
            warehouseIdTo: editableData.warehouseIdTo,
            customerId: editableData.customerId,
            customerName: editableData.customerName,
            customerAddress: editableData.customerAddress,
            customerPhone: editableData.customerPhone,
            customerEmail: editableData.customerEmail,
        };

        if (exportData.exportType === 'CUSTOMER') {
            updateData.warehouseIdTo = null;
        } else if (exportData.exportType === 'WAREHOUSE') {
            updateData.customerId = null;
            updateData.customerName = null;
            updateData.customerAddress = null;
            updateData.customerPhone = null;
            updateData.customerEmail = null;
        }


        console.log('updateData', updateData);

        // Handle updates export
        try {
            await updateExportById({
                exportId: id,
                authToken,
                data: updateData
            }).unwrap();
            setIsEditMode(false);
            handleCloseConfirmationPopup();
            message.success('Export updated successfully');
        } catch (error) {
            message.error('Failed to update export');
            console.error('Update export error:', error);
        }

        // Prepare and send updated export details
        const updatedExportDetails = Object.values(localUpdatedQuantities).map(detail => ({
            exportDetailId: detail.exportDetailId,
            quantity: detail.quantity,
            warehouseId: exportData.warehouseFrom.id
        }));

        if (updatedExportDetails.length > 0) {
            try {
                await updateExportDetails({
                    data: updatedExportDetails,
                    authToken,
                }).unwrap();
                message.success('Product quantities updated successfully');
            } catch (error) {
                message.error('Failed to update product quantities');
                console.error('Update product quantities error:', error);
            }
        }

        setLocalUpdatedQuantities({});
    };

    const handleChange = (field, value) => {
        setEditableData((prevData) => ({
            ...prevData,
            [field]: value
        }));
    };

    const handleDelete = () => {
        setTemporarilyHiddenProductIds([...temporarilyHiddenProductIds, ...selectedProductIds]);
        setSelectedProductIds([]);
    };

    const handleApprove = () => {
        handleOpenConfirmationPopup({ type: 'approveExport' });
    };

    const handleConfirmAction = async () => {
        switch (confirmationAction.type) {
            case 'deleteProduct':
                await handleDeleteExportDetail();
                break;
            case 'approveExport':
                await handleApproveExport();
                break;
            case 'cancelEditMode':
                setIsEditMode(false);
                setSelectedProductIds([]);
                setTemporarilyHiddenProductIds([]);
                handleCloseConfirmationPopup();
                break;
            case 'updateExport':
                await handleSaveEdit();
                break;
            default:
                break;
        }
    };

    const columns = [
        ...(isEditMode ? [{
            title: 'Select',
            dataIndex: 'select',
            key: 'select',
            render: (_, record) => (
                <Checkbox
                    checked={selectedProductIds.includes(record.id)}
                    onChange={(e) => {
                        if (e.target.checked) {
                            setSelectedProductIds([...selectedProductIds, record.id]);
                        } else {
                            setSelectedProductIds(selectedProductIds.filter((id) => id !== record.id));
                        }
                    }}
                />
            )
        }] : []),
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
            render: (text, record) => (
                editableRow === record.id ? (
                    <Input
                        type="number"
                        value={editableRowData.quantity}
                        onChange={(e) => setEditableRowData({
                            ...editableRowData,
                            quantity: e.target.value
                        })}
                    />
                ) : (
                    localUpdatedQuantities[record.id]?.quantity ?? text
                )
            ),
        },
        {
            title: 'Expired At',
            dataIndex: 'expiredAt',
            key: 'expiredAt',
            render: (text) => new Date(text).toLocaleDateString(),
        },
        ...(isEditMode ? [{
            title: 'Action',
            key: 'action',
            render: (_, record) => (
                editableRow === record.id ? (
                    <span>
                        <a className='no-select' onClick={() => handleSaveRowEdit(record)}>Save</a>
                        <a className='no-select' onClick={() => {
                            setEditableRow(null);
                            setEditableRowData({});
                        }}>Cancel</a>
                    </span>
                ) : (
                    <a className='no-select' onClick={() => {
                        setEditableRow(record.id);
                        setEditableRowData(record);
                    }}>Change</a>
                )
            ),
        }] : []),
    ];


    if (exportsLoading) return <div>Loading...</div>;
    if (exportsError) return <div>Error loading export data</div>;

    const modalConfirmTitle = {
        deleteProduct: 'Do you want to delete the selected products?',
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
                        {exportData.exportType === 'WAREHOUSE' && <tr>
                            <td className="export-attribute-title">Warehouse: </td>
                            <td><p>{exportData.warehouseTo.name}</p></td>
                        </tr>}
                        <tr>
                            <td className="export-attribute-title">To:</td>
                            <td>
                                {isEditMode ? (
                                    <div>
                                        {exportData.exportType === 'CUSTOMER' ? (
                                            <Input
                                                style={{ width: '100%' }}
                                                value={exportData.customer.address}
                                                onChange={(e) => handleChange('customerAddress', e.target.value)}
                                            />
                                        ) : (
                                            <Select
                                                style={{ width: '100%' }}
                                                showSearch
                                                placeholder="Select a warehouse"
                                                value={editableData.warehouseIdTo}
                                                onChange={(value) => handleChange('warehouseIdTo', value)}
                                                loading={isWarehouseLoading}
                                            >
                                                {warehousesData.map((warehouse) => (
                                                    <Option key={warehouse.id} value={warehouse.id}>
                                                        {warehouse.name}
                                                    </Option>
                                                ))}
                                            </Select>
                                        )}
                                    </div>
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
                            <>
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
                            </>
                        )}
                    </tbody>
                </table>
                <div>
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
                transitionName=""
                title="Export Details"
                width={800}
                className="custom-modal"
                visible={isProductListPopupVisible}
                onCancel={handleCloseProductListPopup}
                footer={[
                    <div className='footer-modal-button'>
                        <Button key="close" onClick={handleCloseProductListPopup}>
                            Close
                        </Button>
                        {isEditMode && selectedProductIds.length != 0 ? (
                            <Button
                                style={{
                                    background: '#d32f2f',
                                    color: 'white',
                                }}
                                type="default"
                                onClick={handleDelete}
                            >
                                <DeleteRoundedIcon /> Delete Selected
                            </Button>
                        ) : null}
                    </div>
                ]}
            >
                <Table
                    dataSource={exportProducts.filter(product => !temporarilyHiddenProductIds.includes(product.id))}
                    columns={columns}
                    rowKey="id"
                    pagination={false}
                />
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