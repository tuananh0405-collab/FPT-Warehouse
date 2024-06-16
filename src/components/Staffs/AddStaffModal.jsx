import { Modal, Button, Form, Input, Select } from "antd";

const AddStaffModal = ({
  addNewVisible,
  handleOkAdd,
  handleCancelAdd,
  formAdd,
  warehouses,
}) => {
  return (
    <Modal
      title="New Staff"
      visible={addNewVisible}
      onOk={handleOkAdd}
      onCancel={handleCancelAdd}
      footer={[
        <Button key="cancel" onClick={handleCancelAdd}>
          Cancel
        </Button>,
        <Button key="submit" type="primary" onClick={handleOkAdd}>
          Save
        </Button>,
      ]}
    >
      <Form form={formAdd} layout="vertical">
        <Form.Item label="Full Name" name="fullName">
          <Input placeholder="fullName" />
        </Form.Item>
        <Form.Item label="User Name" name="username">
          <Input placeholder="fullName" />
        </Form.Item>
        <Form.Item label="Password" name="password">
          <Input placeholder="fullName" />
        </Form.Item>
        <Form.Item label="Email" name="email">
          <Input placeholder="email" />
        </Form.Item>
        <Form.Item label="Phone" name="phone">
          <Input placeholder="phone" />
        </Form.Item>
        <Form.Item label="Address" name="address">
          <Input placeholder="address" />
        </Form.Item>
        <Form.Item label="Role" name="role">
          <Select placeholder="role">
            <Select.Option value="STAFF">STAFF</Select.Option>
            <Select.Option value="ADMIN">ADMIN</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item label="Warehouse" name="warehouseId">
          <Select placeholder="warehouse name">
            {warehouses?.data?.map((warehouse) => (
              <Select.Option key={warehouse.id} value={warehouse.id}>
                {warehouse.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddStaffModal;
