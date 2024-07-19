// import Table from "@mui/material/Table";
// import TableBody from "@mui/material/TableBody";
// import TableCell from "@mui/material/TableCell";
// import TableContainer from "@mui/material/TableContainer";
// import TableHead from "@mui/material/TableHead";
// import TableRow from "@mui/material/TableRow";
// import Paper from "@mui/material/Paper";
// import Pagination from "@mui/material/Pagination";
// import { Button } from "antd";

// const createData = (name, id, description, address, createdAt) => {
//   return { name, id, description, address, createdAt };
// };

// const WarehouseTable = ({ warehouseList, page, setPage, rowsPerPage, showModal }) => {
//   const rows = warehouseList.map((warehouse) =>
//     createData(warehouse.name, warehouse.id, warehouse.description, warehouse.address, warehouse.createdAt)
//   );

//   const paginatedRows = rows.slice(
//     (page - 1) * rowsPerPage,
//     (page - 1) * rowsPerPage + rowsPerPage
//   );

//   const handleChangePage = (event, newPage) => {
//     setPage(newPage);
//   };

//   return (
//     <div className="Table">
//       <TableContainer
//         component={Paper}
//         style={{ boxShadow: "0px 13px 20px 0px #80808029" }}
//       >
//         <Table sx={{ minWidth: 650 }} aria-label="simple table">
//           <TableHead>
//             <TableRow>
//               <TableCell>Name</TableCell>
//               <TableCell align="left">ID</TableCell>
//               <TableCell align="left">Description</TableCell>
//               <TableCell align="left">Address</TableCell>
//               <TableCell align="left">Created At</TableCell>
//               <TableCell align="left"></TableCell>
//             </TableRow>
//           </TableHead>
//           <TableBody style={{ color: "white" }}>
//             {paginatedRows.map((row) => (
//               <TableRow
//                 key={row.id}
//                 sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
//               >
//                 <TableCell component="th" scope="row">
//                   {row.name}
//                 </TableCell>
//                 <TableCell align="left">{row.id}</TableCell>
//                 <TableCell align="left">{row.description}</TableCell>
//                 <TableCell align="left">{row.address}</TableCell>
//                 <TableCell align="left">{row.createdAt}</TableCell>
//                 <TableCell align="left" className="Details">
//                   <Button
//                     type="primary"
//                     onClick={() => showModal(row.id)}
//                   >
//                     Details
//                   </Button>
//                 </TableCell>
//               </TableRow>
//             ))}
//           </TableBody>
//         </Table>
//       </TableContainer>
//       <Pagination
//         count={Math.ceil(rows.length / rowsPerPage)}
//         page={page}
//         onChange={handleChangePage}
//         style={{ marginTop: "20px" }}
//       />
//     </div>
//   );
// };

// export default WarehouseTable;

import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Pagination from "@mui/material/Pagination";
import { Button } from "antd";

const createData = (name, id, description, address, createdAt) => {
  return { name, id, description, address, createdAt };
};

const WarehouseTable = ({ warehouseList, page, setPage, rowsPerPage, showModal }) => {
  // Đảo dòng cuối cùng trở thành dòng đầu tiên
  const adjustedList = [...warehouseList];
  if (adjustedList.length > 1) {
    const lastItem = adjustedList.pop();
    adjustedList.unshift(lastItem);
  }

  const rows = adjustedList.map((warehouse) =>
    createData(warehouse.name, warehouse.id, warehouse.description, warehouse.address, warehouse.createdAt)
  );

  const paginatedRows = rows.slice(
    (page - 1) * rowsPerPage,
    (page - 1) * rowsPerPage + rowsPerPage
  );

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  return (
    <div className="Table">
      <TableContainer
        component={Paper}
        style={{ boxShadow: "0px 13px 20px 0px #80808029" }}
      >
        <Table sx={{ minWidth: 650 }} aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell>Index</TableCell>
              <TableCell>Name</TableCell>
              <TableCell align="left">Description</TableCell>
              <TableCell align="left">Address</TableCell>
              <TableCell align="left">Created At</TableCell>
              <TableCell align="left"></TableCell>
            </TableRow>
          </TableHead>
          <TableBody style={{ color: "white" }}>
            {paginatedRows.map((row, index) => (
              <TableRow
                key={row.id}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } ,
                backgroundColor: index % 2 === 0 ? "#e0e0e0" : "#ffffff"}}
              >
                <TableCell component="th" scope="row">
                  {(page - 1) * rowsPerPage + index + 1}
                </TableCell>
                <TableCell component="th" scope="row">
                  {row.name}
                </TableCell>
                <TableCell align="left">{row.description}</TableCell>
                <TableCell align="left">{row.address}</TableCell>
                <TableCell align="left">{row.createdAt}</TableCell>
                <TableCell align="left" className="Details">
                  <Button
                    type="primary"
                    onClick={() => showModal(row.id)}
                  >
                    Details
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <Pagination
        count={Math.ceil(rows.length / rowsPerPage)}
        page={page}
        onChange={handleChangePage}
        style={{ marginTop: "20px" }}
      />
    </div>
  );
};

export default WarehouseTable;
