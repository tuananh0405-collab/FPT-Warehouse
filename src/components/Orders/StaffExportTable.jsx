import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Table } from 'antd';
import {
    useGetAllExportsByWarehouseidQuery,
    useGetTotalExportsByWarehouseidAndFilterByStatusQuery
} from '../../redux/api/exportApiSlice';
import { FormatTime } from '../../utils/FormatTime';
import { useNavigate } from 'react-router-dom';

function StaffExportTable({ searchValue }) {
    const userInfo = useSelector((state) => state.auth.userInfo);
    const authToken = userInfo?.data?.token;
    const warehouseId = userInfo?.data?.warehouseId;

    const [pageNo, setPageNo] = useState(1);
    const [sortField, setSortField] = useState('');
    const [sortOrder, setSortOrder] = useState('');
    const [filterStatus, setFilterStatus] = useState('');
    const [search, setSearch] = useState(searchValue);

    const { data: exportsData = {}, isLoading: exportsLoading, error: exportsError } = useGetAllExportsByWarehouseidQuery({
        warehouseId,
        authToken,
        pageNo: pageNo,
        sortBy: sortField,
        direction: sortOrder,
        status: filterStatus,
        search: search,
    });
    const totalExportItemData = useGetTotalExportsByWarehouseidAndFilterByStatusQuery({
        warehouseId, authToken,
        status: filterStatus,
        search: search
    });
    const exports = exportsData.data || [];
    const totalExportItem = totalExportItemData.data || 0;

    useEffect(() => {
        setSearch(searchValue);
    }, [searchValue]);

    const navigate = useNavigate();

    const handleTableChange = (pagination, filters, sorter) => {
        setPageNo(pagination.current);
        if (sorter.order === undefined) {
            setSortField('');
            setSortOrder('');
        } else {
            setSortField(sorter.field);
            const order = sorter.order === 'ascend' ? 'asc' : 'desc';
            setSortOrder(order);
        }
        if (filters.status && filters.status.length > 0) {
            setFilterStatus(filters.status[0]);
        } else {
            setFilterStatus('');
        }
    };

    const columns = [
        {
            title: 'Description',
            dataIndex: 'description',
            key: 'description',
            sorter: true,
            width: 250,
            ellipsis: true,
        },
        {
            title: 'Export Date',
            dataIndex: 'exportDate',
            key: 'exportDate',
            sorter: true,
            width: 180,
            ellipsis: true,
            render: (text) => FormatTime(text),
        },
        {
            title: 'Customer Name',
            dataIndex: ['customer', 'name'],
            key: 'customerName',
            width: 120,
            render: (_, record) => record.customer ? record.customer.name : 'N/A',
        },
        {
            title: 'To',
            dataIndex: ['warehouseTo', 'name'],
            width: 200,
            ellipsis: true,
            render: (_, record) => {
                if (record.exportType === 'CUSTOMER') {
                    return record.customer ? record.customer.address : 'N/A';
                } else if (record.exportType === 'WAREHOUSE') {
                    return record.warehouseTo ? record.warehouseTo.address : 'N/A';
                }
                return 'N/A';
            }
        },
        {
            title: 'Status',
            dataIndex: 'status',
            key: 'status',
            filters: [
                { text: 'Pending', value: 'PENDING' },
                { text: 'Shipping', value: 'SHIPPING' },
                { text: 'Succeed', value: 'SUCCEED' },
                { text: 'Cancel', value: 'CANCEL' },
            ],
            width: 120,
            filterMultiple: false,
            onFilter: (value, record) => record.status === value,
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, record) => (
                <span>
                    <a onClick={() => navigate(`/staff/export/detail/${record.id}`)}>View</a>
                </span>
            ),
        },
    ];

    if (exportsLoading) return <div>Loading...</div>;
    if (exportsError) return <div>Error loading data</div>;

    return (
        <Table
            className="no-select"
            columns={columns}
            dataSource={exports}
            rowKey="id"
            pagination={{ current: pageNo, pageSize: 5, total: totalExportItem }}
            onChange={handleTableChange}
        />
    );
}

export default StaffExportTable;
