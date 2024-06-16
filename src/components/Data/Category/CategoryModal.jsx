import { Modal, Button, Form, Input } from "antd";

const CategoryModal = ({
  isModalVisible,
  handleOk,
  handleCancel,
  handleDelete,
  selectedCategory,
  form,
}) => {
  return (
    <Modal
      title="Edit Category"
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
      {selectedCategory && (
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            name: selectedCategory.name,
            description: selectedCategory.description,
          }}
        >
          <Form.Item label="Name" name="name">
            <Input placeholder={selectedCategory.name} />
          </Form.Item>
          <Form.Item label="Description" name="description">
            <Input placeholder={selectedCategory.description} />
          </Form.Item>
        </Form>
      )}
    </Modal>
  );
};

export default CategoryModal;
