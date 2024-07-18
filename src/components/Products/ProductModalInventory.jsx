
import { Modal, Button, Form, Input, Select, Radio, Table, Pagination } from "antd";
import { useEffect, useState } from "react";

const { Option } = Select;

const ProductModalInventory = ({
  isModalVisible,
  handleOk,
  handleCancel,
  selectedProduct,
  form,
  categories,
}) => {

  const [filterType, setFilterType] = useState("all");

  const filteredData = inventoriesData.filter((item) => {
    const now = new Date();
    const expiredAt = new Date(item.expiredAt);
    const inFifteenDays = new Date(now);
    inFifteenDays.setDate(now.getDate() + 15);

    if (filterType === "expired" && expiredAt >= now) return false;
    if (
      filterType === "expiring" &&
      (expiredAt < now || expiredAt > inFifteenDays)
    )
      return false;
    if (filterType === "valid" && expiredAt <= now) return false;

    return (
      item.product.name.toLowerCase().includes(searchText.toLowerCase()) ||
      item.zone.name.toLowerCase().includes(searchText.toLowerCase())
    );
  });
  useEffect(() => {
    if (selectedProduct) {
      form.setFieldsValue({
        name: selectedProduct.name,
        description: selectedProduct.description,
        categoryId: selectedProduct.category.id,
      });
    }
  }, [selectedProduct, form]);

  return (
    <Modal
      title="Product Details"
      visible={isModalVisible}
      onOk={handleOk}
      onCancel={handleCancel}
      footer={[
   
        <Button key="cancel" onClick={handleCancel}>
          Cancel
        </Button>,

      ]}
    >
      {isModalVisible && (
        <div
          style={{
            position: "absolute",
            top: 0,
            left: 0,
            width: "100%",
            height: "100%",
            backgroundColor: "rgba(0, 0, 0, 0.75)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 50,
          }}
        >
          <div
            style={{
              backgroundColor: "white",
              padding: "20px",
              borderRadius: "10px",
              width: "90%",
              height: "90%",
              maxHeight: "100%",
              overflowY: "auto",
            }}
          >
            <div style={{ marginBottom: 16 }}>
              <Radio.Group
                onChange={(e) => setFilterType(e.target.value)}
                value={filterType}
                style={{ marginRight: 16 }}
              >
                <Radio value="all">All</Radio>
                <Radio value="expired">Expired</Radio>
                <Radio value="expiring">Expiring in 15 days</Radio>
                <Radio value="valid">Valid</Radio>
              </Radio.Group>
              <Input
                placeholder="Search by product or zone name"
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
                style={{ width: "50%" }}
              />
            </div>
            <div style={{ display: "flex", height: "80%" }}>
              <div style={{ width: "70%", paddingRight: "10px" }}>
                <Table
                  dataSource={paginatedData}
                  columns={columns}
                  rowKey="id"
                  pagination={false}
                  onChange={handleTableChange}
                />
                <Pagination
                  current={currentPage}
                  pageSize={10}
                  total={filteredData.length}
                  onChange={handlePageChange}
                  style={{ marginTop: "10px", textAlign: "center" }}
                />
              </div>
              {/* <div
                style={{
                  width: "30%",
                  maxHeight: "100%",
                  overflowY: "auto",
                  padding: "10px",
                  border: "1px solid #ddd",
                  borderRadius: "10px",
                }}
              >
                <h2 className="text-xl font-bold mb-4">Selected Products</h2>
                {selectedProducts.map((product) => {
                  const item = inventoriesData.find(
                    (inv) => inv.id === product.id
                  );
                  return (
                    <div
                      key={product.id}
                      style={{
                        marginBottom: "10px",
                        padding: "10px",
                        border: "1px solid #ddd",
                        borderRadius: "5px",
                      }}
                    >
                      <h3>{item.product.name}</h3>
                      <p>Available: {item.quantity}</p>
                      <Input
                        type="number"
                        value={product.quantity}
                        onChange={(e) => handleQuantityChange(e, product.id)}
                        min={1}
                        max={item.quantity}
                        style={{ marginBottom: "10px" }}
                      />
                      <Button
                        type="link"
                        danger
                        onClick={() => handleRemoveProduct(product.id)}
                      >
                        Remove
                      </Button>
                    </div>
                  );
                })}
              </div> */}
            </div>
            {/* <div className="flex justify-end mt-4" style={{ width: "100%" }}>
              <Button
                onClick={handleDone}
                type="primary"
                style={{ marginRight: "10px" }}
              >
                Done
              </Button>
              <Button onClick={handleClosePopup}>Close</Button>
            </div> */}
          </div>
        </div>
      )}
    </Modal>
  );
};

export default ProductModalInventory;
