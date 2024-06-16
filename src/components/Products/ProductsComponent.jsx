import { useState } from "react";
import { useSelector } from "react-redux";
import {
  useGetAllProductsQuery,
  useUpdateProductMutation,
  useDeleteProductMutation,
  useAddProductMutation,
  
} from "../../redux/api/productApiSlice";
import {useGetCategoriesQuery} from '../../redux/api/categoryApiSlice'
import ProductTable from "./ProductTable";
import ProductModal from "./ProductModal";
import AddProductModal from "./AddProductModal";
import { Button, message, Form } from "antd";
import Loading from "../../utils/Loading";
import Error500 from "../../utils/Error500";

const ProductComponent = () => {
  const userInfo = useSelector((state) => state.auth);
  const authToken = userInfo.userInfo.data.token;
  const { data: products, isLoading: productsLoading, error: productsError } = useGetAllProductsQuery(authToken);
  const { data: categories, isLoading: categoriesLoading, error: categoriesError } = useGetCategoriesQuery(authToken);
  const [updateProduct] = useUpdateProductMutation();
  const [deleteProduct] = useDeleteProductMutation();
  const [addProduct] = useAddProductMutation();
  const [page, setPage] = useState(1);
  const rowsPerPage = 5;
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [form] = Form.useForm();
  const [formAdd] = Form.useForm();
  const [addNewVisible, setAddNewVisible] = useState(false);

  const showModal = (productId) => {
    const product = products.data.find((p) => p.id === productId);
    setSelectedProduct(product);
    setIsModalVisible(true);
  };

  const handleOk = async () => {
    const values = await form.validateFields();
    const data = { ...values, productId: selectedProduct.id };
    await updateProduct({ productId: selectedProduct.id, formData: data, authToken });
    setIsModalVisible(false);
    message.success("Product updated successfully");
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  const handleDelete = async () => {
    try {
      await deleteProduct({ productId: selectedProduct.id, authToken });
      setIsModalVisible(false);
      message.success("Product deleted successfully");
    } catch (error) {
      console.log(error.message);
    }
  };

  const handleOkAdd = async () => {
    const values = await formAdd.validateFields();
    const data = { ...values };
    console.log(data);
    await addProduct({ productData: data, authToken });
    message.success("Product added successfully");
    setAddNewVisible(false);
    formAdd.resetFields();
  };

  const handleCancelAdd = () => {
    setAddNewVisible(false);
  };

  if (productsLoading || categoriesLoading) {
    return (
      <div className="">
        <Loading />
      </div>
    );
  }

  if (productsError || categoriesError) {
    return <Error500 />;
  }

  return (
    <div className="MainDash">
      <h1>Products</h1>
      <Button
        type="primary"
        style={{ background: "#40A578" }}
        onClick={() => setAddNewVisible(true)}
      >
        Add new product
      </Button>
      <AddProductModal
        addNewVisible={addNewVisible}
        handleOkAdd={handleOkAdd}
        handleCancelAdd={handleCancelAdd}
        formAdd={formAdd}
        categories={categories.data}
      />
      <ProductTable
        productList={products.data}
        page={page}
        setPage={setPage}
        rowsPerPage={rowsPerPage}
        showModal={showModal}
      />
      <ProductModal
        isModalVisible={isModalVisible}
        handleOk={handleOk}
        handleCancel={handleCancel}
        handleDelete={handleDelete}
        selectedProduct={selectedProduct}
        form={form}
        categories={categories.data}
      />
    </div>
  );
};

export default ProductComponent;
