import React from 'react';
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Pagination from "@mui/material/Pagination";
import { Button } from "antd";

const createData = (name, id, email, phone, address) => {
  return { name, id, email, phone, address };
};

const CustomerTable = ({ customerList, page, setPage, rowsPerPage, showModal }) => {
  rowsPerPage = 10;

  const adjustedList = [...customerList];
  if (adjustedList.length > 1) {
    const lastItem = adjustedList.pop();
    adjustedList.unshift(lastItem);
  }
  const rows = adjustedList.map((customer) =>
    createData(customer.name, customer.id, customer.email, customer.phone, customer.address)
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
          <TableHead style={{ backgroundColor: "#ffffff" }}>
            <TableRow>
              <TableCell>Index</TableCell>
              <TableCell align="left">Name</TableCell>
              <TableCell align="left">Email</TableCell>
              <TableCell align="left">Phone</TableCell>
              <TableCell align="left">Address</TableCell>
              <TableCell align="left"></TableCell>
            </TableRow>
          </TableHead>
          <TableBody style={{ color: "white" }}>
            {paginatedRows.map((row, index) => (
              <TableRow
                key={row.id}
                sx={{
                  "&:last-child td, &:last-child th": { border: 0 },
                  backgroundColor: index % 2 === 0 ? "#e0e0e0" : "#ffffff",
                }}
              >
                <TableCell component="th" scope="row">
                  {(page - 1) * rowsPerPage + index + 1}
                </TableCell>
                <TableCell align="left">{row.name}</TableCell>
                <TableCell align="left">{row.email}</TableCell>
                <TableCell align="left">{row.phone}</TableCell>
                <TableCell align="left">{row.address}</TableCell>
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

export default CustomerTable;
