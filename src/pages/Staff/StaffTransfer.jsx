import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { useTransferProductMutation } from "../../redux/api/inventoryApiSlice";
import {
  Container,
  TextField,
  Button,
  Typography,
  Box,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from "@mui/material";
import { message } from "antd";
import { useLocation, useNavigate } from "react-router-dom";
import { useGetZoneByWarehouseIdQuery } from "../../redux/api/zoneApiSlice";
import { useGetProductByIdQuery } from "../../redux/api/productApiSlice";

const StaffTransfer = () => {
  const [productId, setProductId] = useState("");
  const [fromZoneId, setFromZoneId] = useState("");
  const [toZoneId, setToZoneId] = useState("");
  const [quantity, setQuantity] = useState("");
  const [productName, setProductName] = useState("");
  const [isFormDisabled, setIsFormDisabled] = useState(true);

  const location = useLocation();
  const navigate = useNavigate();

  const userInfo = useSelector((state) => state.auth);
  let authToken;
  let wid;
  if (userInfo && userInfo.userInfo && userInfo.userInfo.data) {
    authToken = userInfo.userInfo.data.token;
    wid = userInfo.userInfo.data.warehouseId;
  }

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const prodId = params.get("productId") || "";
    const fromZone = params.get("zoneId") || "";

    setProductId(prodId);
    setFromZoneId(fromZone);

    if (prodId && fromZone) {
      setIsFormDisabled(false);
    } else {
      setIsFormDisabled(true);
    }
  }, [location]);

  const { data: productData } = useGetProductByIdQuery(
    { productId, authToken },
    { skip: !productId }
  );
  useEffect(() => {
    if (productData) {
      setProductName(productData.data.name);
    }
  }, [productData]);

  const {
    data: zones,
    isLoading: isLoadingZones,
    error: errorZones,
  } = useGetZoneByWarehouseIdQuery({ id: wid, authToken });

  const [transferProduct, { isLoading, isSuccess, isError, error }] =
    useTransferProductMutation();

  const handleTransfer = async (e) => {
    e.preventDefault();
    if (quantity <= 0) {
      message.error("Quantity must be greater than zero");
      return;
    }
    try {
      await transferProduct({
        productId: parseInt(productId, 10),
        fromZoneId: parseInt(fromZoneId, 10),
        toZoneId: parseInt(toZoneId, 10),
        quantity: parseInt(quantity, 10),
        authToken,
      });
      setProductId("");
      setFromZoneId("");
      setToZoneId("");
      setQuantity("");
      message.success("Transfer successfully");
    } catch (err) {
      message.error("Transfer unsuccessfully");
    }
  };

  const handleSelectProduct = () => {
    navigate("/staff/data/zone");
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Transfer Product
        </Typography>
        <Box sx={{ my: 2 }}>
          <Button
            type="button"
            variant="outlined"
            color="error"
            fullWidth
            onClick={handleSelectProduct}
            sx={{ mt: 2 }}
          >
            Select Product First
          </Button>
        </Box>
        <form onSubmit={handleTransfer}>
          <TextField
            fullWidth
            variant="outlined"
            margin="normal"
            label="Product Name"
            value={productName}
            disabled
          />
          <TextField
            fullWidth
            variant="outlined"
            margin="normal"
            label="From"
            value={
              zones?.data?.find((zone) => zone.id === parseInt(fromZoneId, 10))
                ?.name || ""
            }
            onChange={(e) => setFromZoneId(e.target.value)}
            required
            type="text"
            disabled
          />
          <FormControl fullWidth variant="outlined" margin="normal" required>
            <InputLabel id="to-zone-label">To</InputLabel>
            <Select
              labelId="to-zone-label"
              value={toZoneId}
              onChange={(e) => setToZoneId(e.target.value)}
              label="To"
              disabled={isFormDisabled}
            >
              {zones?.data?.map((zone) => (
                <MenuItem key={zone.id} value={zone.id}>
                  {zone.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            fullWidth
            variant="outlined"
            margin="normal"
            label="Quantity"
            value={quantity}
            onChange={(e) => setQuantity(e.target.value)}
            required
            type="number"
            disabled={isFormDisabled}
          />
          <Box sx={{ my: 2 }}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              fullWidth
              disabled={isLoading || isFormDisabled}
            >
              Transfer
            </Button>
          </Box>
        </form>
      </Box>
    </Container>
  );
};

export default StaffTransfer;
