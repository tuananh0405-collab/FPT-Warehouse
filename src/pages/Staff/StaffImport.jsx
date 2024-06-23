import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { Table, Pagination, message } from "antd";
import { useGetAllExportsByWarehouseidQuery } from "../../redux/api/exportApiSlice";
import { useGetAllCustomersQuery } from "../../redux/api/customersApiSlice";
import Loading from "../../utils/Loading";
import Error500 from "../../utils/Error500";

const StaffImport = () => {
  const userInfo = useSelector((state) => state.auth);
  const authToken = userInfo?.userInfo?.data?.token;
  const currentWarehouseId = userInfo?.userInfo?.data?.warehouseId;
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [sortOrder, setSortOrder] = useState({
    columnKey: null,
    order: null,
  });
  const [filteredData, setFilteredData] = useState([]);
  const [paginatedData, setPaginatedData] = useState([]);
  const [total, setTotal] = useState(0);

  const {
    data: exportsData,
    isFetching: isFetchingExports,
    error: exportsError,
  } = useGetAllExportsByWarehouseidQuery({
    warehouseId: currentWarehouseId,
    authToken,
    pageNo: 1,
    sortBy: "id", // Initial sort order
    direction: "asc", // Initial sort direction
  });

  const {
    data: customersData,
    isFetching: isFetchingCustomers,
    error: customersError,
  } = useGetAllCustomersQuery(authToken);

  useEffect(() => {
    if (
      exportsData &&
      exportsData.data &&
      customersData &&
      customersData.data
    ) {
      const exports = exportsData.data.filter(
        (exp) => exp.warehouseIdFrom !== currentWarehouseId
      );
      const customers = customersData.data;

      const exportsWithCustomerNames = exports.map((exp) => {
        const customer = customers.find((cust) => cust.id === exp.customerId);
        return {
          ...exp,
          customerName: customer ? customer.name : "Unknown",
        };
      });

      setFilteredData(exportsWithCustomerNames);
      setTotal(exportsWithCustomerNames.length);
    }
  }, [exportsData, customersData, currentWarehouseId]);

  useEffect(() => {
    const start = (currentPage - 1) * pageSize;
    const end = start + pageSize;
    let sortedData = [...filteredData];

    if (sortOrder.columnKey) {
      sortedData.sort((a, b) => {
        if (a[sortOrder.columnKey] < b[sortOrder.columnKey])
          return sortOrder.order === "ascend" ? -1 : 1;
        if (a[sortOrder.columnKey] > b[sortOrder.columnKey])
          return sortOrder.order === "ascend" ? 1 : -1;
        return 0;
      });
    }

    setPaginatedData(sortedData.slice(start, end));
  }, [filteredData, currentPage, pageSize, sortOrder]);

  const handleTableChange = (pagination, filters, sorter) => {
    setSortOrder({
      columnKey: sorter.field,
      order: sorter.order,
    });
    setCurrentPage(pagination.current);
    setPageSize(pagination.pageSize);
  };

  const columns = [
    {
      title: "Export ID",
      dataIndex: "id",
      key: "id",
      sorter: true,
    },
    {
      title: "Description",
      dataIndex: "description",
      key: "description",
      sorter: true,
    },
    {
      title: "Export Date",
      dataIndex: "exportDate",
      key: "exportDate",
      sorter: true,
    },
    {
      title: "Warehouse From",
      dataIndex: "warehouseIdFrom",
      key: "warehouseIdFrom",
      sorter: true,
    },
    {
      title: "Customer Name",
      dataIndex: "customerName",
      key: "customerName",
      sorter: true,
    },
  ];

  if (isFetchingExports || isFetchingCustomers) return <Loading />;
  if (exportsError || customersError) return <Error500 />;

  return (
    <>
      <h1>Staff Import</h1>
      <Table
        dataSource={paginatedData}
        columns={columns}
        rowKey="id"
        pagination={{ current: currentPage, pageSize, total }}
        onChange={handleTableChange}
      />
    </>
  );
};

export default StaffImport;
