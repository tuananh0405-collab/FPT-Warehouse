import { Tag } from "antd";

function ExportStatus({ status }) {
    if (status === 'PENDING') {
        return <Tag color="processing">Pending</Tag>;
    } else if (status === 'SHIPPING') {
        return <Tag color="orange">Shipping</Tag>;
    } else if (status === 'SUCCEED') {
        return <Tag color="success">Succeed</Tag>;
    } else if (status === 'CANCEL') {
        return <Tag color="error">Cancel</Tag>;
    }
    return null;
}

export default ExportStatus;