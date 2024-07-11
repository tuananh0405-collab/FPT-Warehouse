import { useState } from "react";
import { useSelector } from "react-redux";
import {
  useGetAllProductByWarehouseIdQuery,
  
} from "../../redux/api/productApiSlice";
import {useGetCategoriesQuery} from '../../redux/api/categoryApiSlice'
import ProductTable from "../../components/Products/ProductTable";
import ProductModal from "../../components/Products/ProductModal";
import { Form } from "antd";
import Loading from "../../utils/Loading";
import Error500 from "../../utils/Error500";
import ProductModalInventory from "../../components/Products/ProductModalInventory";

const StaffProductComponent = () => {
  const userInfo = useSelector((state) => state.auth);

  let authToken;
  let warehouseId;
  if (userInfo && userInfo.userInfo && userInfo.userInfo.data) {
    authToken = userInfo.userInfo.data.token;
    warehouseId = userInfo.userInfo.data.warehouseId;
  }
  const { data: products, isLoading: productsLoading, error: productsError } = useGetAllProductByWarehouseIdQuery({ id: warehouseId, authToken });
  console.log(products);
  const { data: categories, isLoading: categoriesLoading, error: categoriesError } = useGetCategoriesQuery(authToken);

  const [page, setPage] = useState(1);
  const rowsPerPage = 7;
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [form] = Form.useForm();

  const showModal = (productId) => {
    const product = products.data.find((p) => p.id === productId);
    setSelectedProduct(product);
    setIsModalVisible(true);
  };


  const handleCancel = () => {
    setIsModalVisible(false);
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
    <div className="">
      <h1 className ="my-4 text-2xl font-semibold text-dark">Products</h1>
     
      <ProductTable
        productList={products.data}
        page={page}
        setPage={setPage}
        rowsPerPage={rowsPerPage}
        showModal={showModal}
      />
      <ProductModalInventory
        isModalVisible={isModalVisible}
        handleCancel={handleCancel}
        selectedProduct={selectedProduct}
        form={form}
        categories={categories.data}
      />
    </div>
  );
};

export default StaffProductComponent;
