import { Modal, Button, Form, Input, Select } from "antd";

const StaffModal = ({
  isModalVisible,
  handleOk,
  handleCancel,
  handleDelete,
  selectedStaff,
  form,
  warehouses,
}) => {
  return (
    <Modal
      title="Staff Details"
      visible={isModalVisible}
      onOk={handleOk}
      onCancel={handleCancel}
      footer={[
        <Button key="delete" type="primary" danger onClick={handleDelete}>
          Delete
        </Button>,
        <Button key="cancel" onClick={handleCancel}>
          Cancel
        </Button>,
        <Button key="submit" type="primary" onClick={handleOk}>
          Save
        </Button>,
      ]}
    >
      {selectedStaff && (
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            fullName: selectedStaff.fullName,
            email: selectedStaff.email,
            phone: selectedStaff.phone,
            address: selectedStaff.address,
            role: selectedStaff.role,
            warehouseId: selectedStaff.warehouse?.id,
          }}
        >
          <Form.Item label="Full Name" name="fullName">
            <Input placeholder={selectedStaff.fullName} />
          </Form.Item>
          <Form.Item label="Email" name="email">
            <Input placeholder={selectedStaff.email} />
          </Form.Item>
          <Form.Item label="Phone" name="phone">
            <Input placeholder={selectedStaff.phone} />
          </Form.Item>
          <Form.Item label="Address" name="address">
            <Input placeholder={selectedStaff.address} />
          </Form.Item>
          <Form.Item label="Role" name="role">
            <Select placeholder={selectedStaff.role}>
              <Select.Option value="STAFF">STAFF</Select.Option>
              <Select.Option value="ADMIN">ADMIN</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item label="Warehouse" name="warehouseId">
            <Select placeholder={selectedStaff.warehouse?.name}>
              {warehouses?.data?.map((warehouse) => (
                <Select.Option key={warehouse.id} value={warehouse.id}>
                  {warehouse.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      )}
    </Modal>
  );
};

export default StaffModal;
