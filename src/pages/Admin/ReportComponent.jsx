import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { Button, Select, Form, Typography, message } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';
import { useGetInventorySnapshotReportQuery } from '../../redux/api/inventorySnapshotSlice';
import Loading from "../../utils/Loading";
import Error500 from "../../utils/Error500";
import useDocumentTitle from '../../utils/UseDocumentTitle';

const { Title, Paragraph } = Typography;
const { Option } = Select;

const ReportComponent = () => {
  useDocumentTitle('Report')
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

// import React, { useState, useEffect } from 'react';
// import { useSelector } from 'react-redux';
// import { Button, Form, Typography } from 'antd';
// import { DownloadOutlined, EyeOutlined } from '@ant-design/icons';
// import { useGetInventorySnapshotReportQuery } from '../../redux/api/inventorySnapshotSlice';
// import Loading from "../../utils/Loading";
// import Error500 from "../../utils/Error500";
// import * as XLSX from 'xlsx';
// import Modal from 'react-modal';
// import { useTable, useResizeColumns, useFlexLayout } from '@tanstack/react-table';
// import styled from 'styled-components';

// const { Title, Paragraph } = Typography;

// const StyledTable = styled.div`
//   display: block;
//   overflow-x: auto;
//   .tr {
//     display: flex;
//   }
//   .th,
//   .td {
//     flex: 1;
//     padding: 8px;
//     border: 1px solid #ddd;
//     position: relative; /* Added to enable resizer positioning */
//   }
//   .resizer {
//     display: inline-block;
//     width: 5px;
//     height: 100%;
//     position: absolute;
//     right: 0;
//     top: 0;
//     transform: translateX(50%);
//     z-index: 1;
//     touch-action: none;
//   }
//   .resizer.isResizing {
//     background: #000;
//   }
// `;

// const ReportComponent = () => {
//   const [triggerDownload, setTriggerDownload] = useState(false);
//   const [triggerPreview, setTriggerPreview] = useState(false);
//   const [data, setData] = useState([]);
//   const [modalIsOpen, setModalIsOpen] = useState(false);

//   const userInfo = useSelector((state) => state.auth.userInfo);
//   const authToken = userInfo?.data?.token;

//   const { data: reportData, error, isLoading } = useGetInventorySnapshotReportQuery(
//     { authToken },
//     { skip: (!triggerDownload && !triggerPreview) || !authToken }
//   );

//   const handleDownload = () => {
//     setTriggerDownload(true);
//     setTriggerPreview(false);
//   };

//   const handlePreview = () => {
//     setTriggerPreview(true);
//     setTriggerDownload(false);
//   };

//   useEffect(() => {
//     if (reportData) {
//       const blob = new Blob([reportData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
//       const reader = new FileReader();
//       reader.onload = (e) => {
//         const binaryStr = e.target.result;
//         const workbook = XLSX.read(binaryStr, { type: 'binary' });
//         const sheetName = workbook.SheetNames[0];
//         const sheet = workbook.Sheets[sheetName];
//         const jsonData = XLSX.utils.sheet_to_json(sheet);
//         setData(jsonData);

//         if (triggerDownload) {
//           const url = window.URL.createObjectURL(blob);
//           const a = document.createElement('a');
//           a.href = url;
//           a.download = 'inventory_snapshot_report.xlsx';
//           document.body.appendChild(a);
//           a.click();
//           a.remove();
//           setTriggerDownload(false); // Reset trigger after download
//         } else if (triggerPreview) {
//           setModalIsOpen(true);
//         }
//       };
//       reader.readAsBinaryString(blob);
//     }
//   }, [reportData, triggerDownload, triggerPreview]);

//   const columns = data.length > 0 ? Object.keys(data[0]).map((key) => ({
//     Header: key,
//     accessor: key,
//   })) : [];

//   const tableInstance = useTable(
//     {
//       columns,
//       data,
//     },
//     useResizeColumns,
//     useFlexLayout
//   );

//   return (
//     <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
//       <Title level={2}>Report Monthly</Title>
//       <Paragraph>
//         Đây là báo cáo tồn kho của tất cả các sản phẩm trong tất cả các kho. Báo cáo tình từ ngày 1 đến ngày 28 của từng tháng. Đây là chức năng trong giai đoạn hoàn thiện. Nếu có đóng góp gì hãy cho tôi biết nhé. Hãy chọn tháng bạn muốn xem báo cáo và tải xuống!
//       </Paragraph>
//       <Form layout="vertical">
//         <Form.Item>
//           <Button
//             type="primary"
//             icon={<DownloadOutlined />}
//             onClick={handleDownload}
//             disabled={isLoading}
//             loading={isLoading}
//           >
//             {isLoading ? 'Loading...' : 'Download Report'}
//           </Button>
//           <Button
//             type="default"
//             icon={<EyeOutlined />}
//             onClick={handlePreview}
//             disabled={isLoading}
//             loading={isLoading}
//             style={{ marginLeft: '10px' }}
//           >
//             {isLoading ? 'Loading...' : 'Preview Report'}
//           </Button>
//         </Form.Item>
//       </Form>
//       {error && (
//         <Error500>
//           <p style={{ color: 'red' }}>{error.toString()}</p>
//         </Error500>
//       )}
//       {isLoading && <Loading />}
//       <Modal
//         isOpen={modalIsOpen}
//         onRequestClose={() => setModalIsOpen(false)}
//         contentLabel="Report Preview"
//         style={{
//           content: {
//             top: '50%',
//             left: '50%',
//             right: 'auto',
//             bottom: 'auto',
//             marginRight: '-50%',
//             transform: 'translate(-50%, -50%)',
//             width: '80%',
//             height: '80%',
//           },
//         }}
//       >
//         <Button onClick={() => setModalIsOpen(false)}>Close</Button>
//         <StyledTable {...tableInstance.getTableProps()}>
//           <div>
//             {tableInstance.headerGroups.map((headerGroup) => (
//               <div {...headerGroup.getHeaderGroupProps()} className="tr">
//                 {headerGroup.headers.map((column) => (
//                   <div {...column.getHeaderProps()} className="th">
//                     {column.render('Header')}
//                     <div
//                       {...column.getResizerProps()}
//                       className={`resizer ${
//                         column.isResizing ? 'isResizing' : ''
//                       }`}
//                     />
//                   </div>
//                 ))}
//               </div>
//             ))}
//           </div>
//           <div>
//             {tableInstance.rows.map((row) => {
//               tableInstance.prepareRow(row);
//               return (
//                 <div {...row.getRowProps()} className="tr">
//                   {row.cells.map((cell) => {
//                     return (
//                       <div {...cell.getCellProps()} className="td">
//                         {cell.render('Cell')}
//                       </div>
//                     );
//                   })}
//                 </div>
//               );
//             })}
//           </div>
//         </StyledTable>
//       </Modal>
//     </div>
//   );
// };

// export default ReportComponent;







