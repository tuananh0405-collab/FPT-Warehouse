import React, { useState } from 'react';
import Breadcrumbs from "../../utils/Breadcumbs";
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { useGetExportByIdQuery } from "../../redux/api/exportApiSlice";
import { useGetAllExportDetailsByExportIdQuery } from "../../redux/api/exportDetailApiSlice";
import ExportStatus from "../../components/Orders/ExportStatus";
import '../../components/Orders/MainDash.css';
import { Table, Button, Modal } from 'antd';

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

    const [isPopupVisible, setIsPopupVisible] = useState(false);
    const handleOpenPopup = () => setIsPopupVisible(true);
    const handleClosePopup = () => setIsPopupVisible(false);

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

    if (exportsLoading) return <div>Loading...</div>;
    if (exportsError) return <div>Error loading export data</div>;

    return (
        <div>
            <Breadcrumbs />
            <h1>Export {id}</h1>
            <div className="export-attribute-container">
                <span className="export-attribute-title">Description:</span>
                <p>{exportData.description}</p>
            </div>
            <div className="export-attribute-container">
                <span className="export-attribute-title">Status:</span>
                <p><ExportStatus status={exportData.status} /></p>
            </div>
            <div className="export-attribute-container">
                <span className="export-attribute-title">Export Date:</span>
                <p>{new Date(exportData.exportDate).toLocaleDateString()}</p>
            </div>
            <div className="export-attribute-container">
                <span className="export-attribute-title">Export Type:</span>
                <p>{exportData.exportType}</p>
            </div>
            <div className="export-attribute-container">
                <span className="export-attribute-title">To:</span>
                <p>{exportData.exportType === 'CUSTOMER' ?
                    exportData.customer.address :
                    exportData.warehouseTo.address
                }</p>
            </div>
            <Button type="primary" onClick={handleOpenPopup}>
                View Export Details
            </Button>
            <Modal
                title="Export Details"
                width={800}
                className="custom-modal"
                visible={isPopupVisible}
                onCancel={handleClosePopup}
                footer={[
                    <Button key="close" onClick={handleClosePopup}>
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
        </div>
    );
}

export default StaffExportDetail;
