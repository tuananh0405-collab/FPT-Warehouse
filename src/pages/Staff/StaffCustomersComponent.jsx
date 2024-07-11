import React, { useState, useEffect } from "react";
import Breadcrumbs from "../../utils/Breadcumbs";
import { useSelector } from "react-redux";
import {
  useGetAllCustomersQuery,
} from "../../redux/api/customersApiSlice";
import CustomerTable from "../../components/Data/Customers/CustomerTable";
import CustomerModal from "../../components/Data/Customers/CustomerModal";
import { Form } from "antd";
import Loading from "../../utils/Loading";
import Error500 from "../../utils/Error500";
import '../../assets/styles/MainDash.css';

const StaffCustomerComponent = () => {
  const userInfo = useSelector((state) => state.auth);
  const authToken = userInfo.userInfo.data.token;
  const { data: customers, isLoading, error } = useGetAllCustomersQuery(authToken);

  const [page, setPage] = useState(1);
  const rowsPerPage = 5;
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [form] = Form.useForm();
 

  const showModal = (customerId) => {
    const customer = customers.data.find((c) => c.id === customerId);
    setSelectedCustomer(customer);
    setIsModalVisible(true);
  };

 

  const handleCancel = () => {
    setSelectedCustomer(null);
    setIsModalVisible(false);
  };

  if (isLoading) {
    return (
      <div className="">
        <Loading />
      </div>
    );
  }

  if (error) {
    return <Error500 />;
  }

  return (
    <div className="">
      <Breadcrumbs />
      <h1 className ="mb-2 text-2xl font-semibold text-dark">Customers</h1>
     
     
      <CustomerTable
        customerList={customers.data}
        page={page}
        setPage={setPage}
        rowsPerPage={rowsPerPage}
        showModal={showModal}
      />
      <CustomerModal
        isModalVisible={isModalVisible}
        handleCancel={handleCancel}
        selectedCustomer={selectedCustomer}
        form={form}
      />
    </div>
  );
};

export default StaffCustomerComponent;
