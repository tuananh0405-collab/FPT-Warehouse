import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { Button, Select, Form, Typography, message } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';
import { useGetInventorySnapshotReportQuery } from '../../redux/api/inventorySnapshotSlice';
import Loading from "../../utils/Loading";
import Error500 from "../../utils/Error500";

const { Title, Paragraph } = Typography;
const { Option } = Select;

const ReportComponent = () => {
  const [year, setYear] = useState(new Date().getFullYear());
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [trigger, setTrigger] = useState(false);

  const userInfo = useSelector((state) => state.auth.userInfo);
  const authToken = userInfo?.data?.token;

  const { data, error, isLoading } = useGetInventorySnapshotReportQuery(
    { year, month, authToken },
    { skip: !trigger || !authToken }
  );

  const handleDownload = () => {
    setTrigger(true);
  };

  if (data) {
    const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `inventory_snapshot_${year}_${month}.xlsx`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    setTrigger(false); // Reset trigger after download
  }

  return (
    <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
      <Title level={2}>Report Monthly</Title>
      <Paragraph>
        Đây là báo cáo tồn kho của tất cả các sản phẩm trong tất cả các kho. Báo cáo tình từ ngày 1 đến ngày 28 của từng tháng. Đây là chức năng trong giai đoạn hoàn thiện. Nếu có đóng góp gì hãy cho tôi biết nhé. Hãy chọn tháng bạn muốn xem báo cáo và tải xuống!
      </Paragraph>
      <Form layout="vertical">
        <Form.Item label="Year">
          <Select value={year} onChange={(value) => setYear(value)}>
            {[...Array(10)].map((_, index) => (
              <Option key={index} value={new Date().getFullYear() - index}>
                {new Date().getFullYear() - index}
              </Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item label="Month">
          <Select value={month} onChange={(value) => setMonth(value)}>
            {[...Array(12)].map((_, index) => (
              <Option key={index} value={index + 1}>
                {index + 1}
              </Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            icon={<DownloadOutlined />}
            onClick={handleDownload}
            disabled={isLoading}
            loading={isLoading}
          >
            {isLoading ? 'Loading...' : 'Download Report'}
          </Button>
        </Form.Item>
      </Form>
      {error && (
        <Error500>
          <p style={{ color: 'red' }}>{error.toString()}</p>
        </Error500>
      )}
      {isLoading && <Loading />}
    </div>
  );
};

export default ReportComponent;
