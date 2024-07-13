import React, { useState, useEffect } from 'react';
import { Modal, Table, Button, Input, Select, message } from 'antd';
import { useGetProductListsForAutoSelectQuery, useGetTotalProductsForAutoSelectQuery } from '../../redux/api/productApiSlice';
import { useGetAllSortedCategoryWithSearchQuery } from '../../redux/api/categoryApiSlice';
import useDebounce from '../../utils/useDebounce';
import { useAutoGetProductFromWarehouseMutation } from '../../redux/api/exportDetailApiSlice';

const { Option } = Select;

const AutoSelectModal = ({ authToken, warehouseId, isModalOpen, setIsModalOpen, onProductSelect }) => {
    const [productPageNo, setProductPageNo] = useState(1);
    const [sortBy, setSortBy] = useState('name');
    const [direction, setDirection] = useState('asc');
    const [categoryId, setCategoryId] = useState('');
    const [productSearch, setProductSearch] = useState('');
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [quantityModalVisible, setQuantityModalVisible] = useState(false);
    const [quantity, setQuantity] = useState(0);
    const [categorySearch, setCategorySearch] = useState('');

    const debouncedProductSearch = useDebounce(productSearch, 500);

    const {
        data: productListsForAutoSelectResponse,
        isFetching: isProductListsLoading,
        error: productListsError
    } = useGetProductListsForAutoSelectQuery({
        authToken: authToken,
        warehouseId: warehouseId,
        pageNo: productPageNo,
        sortBy: sortBy,
        direction: direction,
        categoryId: categoryId,
        search: debouncedProductSearch
    });
    const {
        data: totalProductsForAutoSelectResponse,
        isFetching: isTotalProductsLoading,
        error: totalProductsError
    } = useGetTotalProductsForAutoSelectQuery({
        authToken: authToken,
        warehouseId: warehouseId,
        categoryId: categoryId,
        search: debouncedProductSearch
    });
    const {
        data: categoriesResponse,
        isFetching: isCategoriesLoading,
        error: categoriesError
    } = useGetAllSortedCategoryWithSearchQuery({
        authToken,
        search: categorySearch
    });

    const productListsForAutoSelectData = productListsForAutoSelectResponse?.data || [];
    const totalProductsForAutoSelectData = totalProductsForAutoSelectResponse || 0;
    const categoriesData = categoriesResponse?.data || [];

    const [autoGetProductFromWarehouse, { data: autoSelectResponse, isLoading: isAutoSelecting }] = useAutoGetProductFromWarehouseMutation();

    useEffect(() => {
        if (autoSelectResponse && autoSelectResponse.data.length > 0) {
            onProductSelect(autoSelectResponse.data);
            setIsModalOpen(false);
            message.success('Products selected successfully!');
        }
    }, [autoSelectResponse, onProductSelect, setIsModalOpen]);

    const handleTableChange = (pagination) => {
        setProductPageNo(pagination.current);
    };

    const handleRowClick = (record) => {
        setSelectedProduct(record);
        setQuantityModalVisible(true);
        setQuantity(0); // Reset quantity input
    };

    const handleCategoryFilterChange = (value) => {
        setCategoryId(value);
    };

    const handleQuantityChange = (e) => {
        setQuantity(e.target.value);
    };

    const handleAutoTakeProduct = () => {
        if (quantity <= 0) {
            message.warning(`Please enter a valid quantity greater than 0`);
        } else {
            autoGetProductFromWarehouse({
                authToken,
                warehouseId,
                productId: selectedProduct.id,
                quantity
            }).unwrap().catch(error => {
                console.error("Failed to fetch product:", error);
                message.error("Failed to fetch product. Please try again.");
            });
        }
    };

    const handleProductSearchChange = (e) => {
        setProductSearch(e.target.value);
    };

    return (
        <>
            <Modal
                transitionName=""
                open={isModalOpen}
                onCancel={() => { setIsModalOpen(false) }}
                closable={false}
                footer={[
                    <div key="footer-buttons">
                        <Button key="close" type="default" onClick={() => { setIsModalOpen(false) }}>
                            Close
                        </Button>
                    </div>
                ]}
                className="overflow-auto"
            >
                <div className="overflow-auto" >
                    <Table
                        dataSource={productListsForAutoSelectData}
                        columns={[
                            {
                                title: "Product Name",
                                dataIndex: "name",
                                key: "name",
                                filterDropdown: () => (
                                    <div style={{ padding: 8 }}>
                                        <Select
                                            optionFilterProp="children"
                                            style={{ width: '100%' }}
                                            onChange={(value) => setDirection(value)}
                                            value={direction}
                                            className='mb-2'
                                        >
                                            <Option value="asc">Ascending</Option>
                                            <Option value="desc">Descending</Option>
                                        </Select>
                                        <Input
                                            placeholder="Search product"
                                            value={productSearch}
                                            onChange={handleProductSearchChange}
                                            className='w-full block'
                                        />
                                    </div>
                                ),
                            },
                            {
                                title: "Category",
                                dataIndex: ["category", "name"],
                                key: "category",
                                filterDropdown: () => (
                                    <div style={{ padding: 8 }}>
                                        <Select
                                            showSearch
                                            optionFilterProp="children"
                                            style={{ width: '100%' }}
                                            onChange={handleCategoryFilterChange}
                                            value={categoryId || ''}
                                        >
                                            <Option value="">All</Option>
                                            {categoriesData.map(category => (
                                                <Option key={category.id} value={category.id}>
                                                    {category.name}
                                                </Option>
                                            ))}
                                        </Select>
                                    </div>
                                ),
                                onFilter: (value, record) => !value || record.category.id === value,
                            },
                            {
                                title: "Available Quantity",
                                dataIndex: "availableQuantity",
                                key: "availableQuantity",
                            },
                        ]}
                        rowKey="id"
                        pagination={{ current: productPageNo, pageSize: 10, total: totalProductsForAutoSelectData }}
                        loading={isProductListsLoading || isTotalProductsLoading}
                        onChange={handleTableChange}
                        onRow={(record) => ({
                            onClick: () => handleRowClick(record),
                        })}
                        rowClassName={'cursor-pointer'}
                        className="table-fixed w-full"
                    />
                </div>
            </Modal>

            {selectedProduct && (
                <Modal
                    transitionName=""
                    open={quantityModalVisible}
                    onCancel={() => { setQuantityModalVisible(false) }}
                    footer={[
                        <div key="quantity-footer-buttons">
                            <Button className='mr-2' key="take" type="primary" onClick={handleAutoTakeProduct} loading={isAutoSelecting}>
                                Take
                            </Button>
                            <Button key="cancel" type="default" onClick={() => { setQuantityModalVisible(false) }}>
                                Cancel
                            </Button>
                        </div>
                    ]}
                >
                    <div className="p-4">
                        <h2 className="text-xl font-bold mb-4">{selectedProduct.name}</h2>
                        <p className="mb-2"><strong>Category:</strong> {selectedProduct.category.name}</p>
                        <p className="mb-2"><strong>Available Quantity:</strong> {selectedProduct.availableQuantity}</p>
                        <Input
                            placeholder="Enter quantity"
                            value={quantity}
                            onChange={handleQuantityChange}
                            className="mt-4"
                        />
                    </div>
                </Modal>
            )}
        </>
    );
};

export default AutoSelectModal;
