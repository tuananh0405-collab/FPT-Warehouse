import React from 'react';
import { Pagination, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';
import { Button } from 'antd';

const createData = (id, name, description) => {
  return { id, name, description };
};

const ZoneTable = ({ zones, showModal, page, setPage }) => {
  const rowsPerPage = 10;
  const rows = zones.map((zone) => createData(zone.id, zone.name, zone.description));
  const paginatedRows = rows.slice((page - 1) * rowsPerPage, (page - 1) * rowsPerPage + rowsPerPage);
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };
  return (
    <div className="Table">
      <TableContainer component={Paper} style={{ boxShadow: '0px 13px 20px 0px #80808029' }}>
        <Table sx={{ minWidth: 650 }} aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell>Index</TableCell>
              <TableCell align="left">Name</TableCell>
              <TableCell align="left">Description</TableCell>
              <TableCell align="left"></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {paginatedRows.map((row, index) => (
              <TableRow
                key={row.id}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } ,
                backgroundColor: index % 2 === 0 ? "#e0e0e0" : "#ffffff"}}
              >
                <TableCell component="th" scope="row">
                  {(page - 1) * rowsPerPage + index + 1}
                </TableCell>
                <TableCell align="left">{row.name}</TableCell>
                <TableCell align="left">{row.description}</TableCell>
                <TableCell align="left">
                  <Button type="primary" onClick={() => showModal(row.id)}>
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
        style={{ marginTop: '20px' }}
      />
    </div>
  );
};

export default ZoneTable;
