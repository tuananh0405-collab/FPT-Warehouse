import React, { useState, useEffect } from "react";
import { useTransferProductMutation } from "../../redux/api/inventoryApiSlice";
import {
  Box,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Typography,
  Button,
  TextField,
  Grid,
  IconButton,
} from "@mui/material";
import { Add as AddIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { message } from "antd";
import { useGetZoneByWarehouseIdQuery } from "../../redux/api/zoneApiSlice";
import { useGetInventoriesByWarehouseIdQuery } from "../../redux/api/inventoryApiSlice";

const StaffTransfer = ({ initialInventory, authToken, onTransferSuccess }) => {
  console.log(initialInventory);
  const [transfers, setTransfers] = useState([
    { productId: initialInventory.product.id, fromZoneId: initialInventory.zone.id, toZoneId: '', quantity: '', expiredAt: initialInventory.expiredAt }
  ]);
  const [isFormDisabled, setIsFormDisabled] = useState(true);

  useEffect(() => {
    if (initialInventory?.product?.id && initialInventory?.zone?.id) {
      setIsFormDisabled(false);
    } else {
      setIsFormDisabled(true);
    }
  }, [initialInventory]);

  const { data: zones, isLoading: isLoadingZones, error: errorZones } = useGetZoneByWarehouseIdQuery({ id: initialInventory?.zone?.warehouse?.id, authToken });

  const { data: inventories, error: inventoriesError, isLoading: inventoriesLoading } = useGetInventoriesByWarehouseIdQuery({ warehouseId: initialInventory?.zone?.warehouse?.id, authToken });

  const [transferProduct, { isLoading }] = useTransferProductMutation();

  const handleTransfer = async (e) => {
    e.preventDefault();
    const isValid = transfers.every((transfer) => !transfer.quantityError);
    if (!isValid) {
      message.error("Please fix the errors in the form before submitting");
      return;
    }
    try {
        const transferRequests = transfers.map((transfer) => ({
            productId: parseInt(transfer.productId, 10),
            fromZoneId: parseInt(transfer.fromZoneId, 10),
            toZoneId: parseInt(transfer.toZoneId, 10),
            quantity: parseInt(transfer.quantity, 10),
            expiredAt: transfer.expiredAt,
        }));
        await transferProduct({
            transferRequests,
            authToken,
        });
        setTransfers([{ productId: initialInventory.product.id, fromZoneId: initialInventory.zone.id, toZoneId: '', quantity: '', expiredAt: initialInventory.expiredAt }]);
        message.success("Transfers successfully");
        onTransferSuccess();
    } catch (err) {
        message.error("Transfers unsuccessfully");
    }
  };

  const handleAddTransfer = () => {
    setTransfers([...transfers, { productId: '', fromZoneId: '', toZoneId: '', quantity: '', expiredAt: '' }]);
  };

  const handleRemoveTransfer = (index) => {
    const newTransfers = transfers.filter((_, i) => i !== index);
    setTransfers(newTransfers);
  };

  const handleTransferChange = (index, field, value) => {
    const newTransfers = [...transfers];
    newTransfers[index][field] = value;
    if (field === 'quantity') {
      const selectedProductInventories = inventories
        ? inventories.filter((inventory) => inventory.product.id === newTransfers[index].productId && inventory.zone.id === newTransfers[index].fromZoneId && inventory.expiredAt === newTransfers[index].expiredAt)
        : [];
      const availableQuantity = selectedProductInventories.length > 0 ? selectedProductInventories[0].quantity : 0;
      if (value <= 0 || value > availableQuantity) {
        newTransfers[index].quantityError = `Quantity must be greater than 0 and less than or equal to ${availableQuantity}`;
      } else {
        newTransfers[index].quantityError = '';
      }
    }
    setTransfers(newTransfers);
  };

  const uniqueProducts = inventories
    ? Array.from(new Set(inventories.map((inventory) => inventory.product.id)))
        .map((productId) => inventories.find((inventory) => inventory.product.id === productId).product)
    : [];

    // console.log(selectedProductInventories);

  return (
    <Box component="form" onSubmit={handleTransfer} sx={{ mt: 2 }}>
      {transfers.map((transfer, index) => {
        const selectedProductInventories = inventories
          ? inventories.filter((inventory) => inventory.product.id === transfer.productId)
          : [];

        const selectedZoneInventories = selectedProductInventories.filter((inventory) => inventory.zone.id === transfer.fromZoneId);

        return (
          <Grid container spacing={2} key={index}>
            <Grid item xs={2}>
              <FormControl fullWidth variant="outlined" margin="normal" required>
                <InputLabel id={`product-label-${index}`}>Product</InputLabel>
                <Select
                  labelId={`product-label-${index}`}
                  value={transfer.productId}
                  onChange={(e) => handleTransferChange(index, 'productId', e.target.value)}
                  label="Product"
                  disabled={isFormDisabled}
                >
                  {uniqueProducts.map((product) => (
                    <MenuItem key={product.id} value={product.id}>
                      {product.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={2}>
              <FormControl fullWidth variant="outlined" margin="normal" required>
                <InputLabel id={`from-zone-label-${index}`}>From Zone</InputLabel>
                <Select
                  labelId={`from-zone-label-${index}`}
                  value={transfer.fromZoneId}
                  onChange={(e) => handleTransferChange(index, 'fromZoneId', e.target.value)}
                  label="From Zone"
                  disabled={isFormDisabled || !transfer.productId}
                >
                  {selectedProductInventories.map((inventory) => (
                    <MenuItem key={inventory.zone.id} value={inventory.zone.id}>
                      {inventory.zone.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
              {transfer.fromZoneId && (
                <Typography variant="body2">
                  Available Quantity: {selectedZoneInventories.find((inventory) => inventory.zone.id === transfer.fromZoneId && inventory.expiredAt === transfer.expiredAt)?.quantity || 0}
                </Typography>
              )}
            </Grid>
            <Grid item xs={2}>
              <FormControl fullWidth variant="outlined" margin="normal" required>
                <InputLabel id={`expired-at-label-${index}`}>Expired At</InputLabel>
                <Select
                  labelId={`expired-at-label-${index}`}
                  value={transfer.expiredAt}
                  onChange={(e) => handleTransferChange(index, 'expiredAt', e.target.value)}
                  label="Expired At"
                  disabled={isFormDisabled || !transfer.fromZoneId}
                >
                  {selectedZoneInventories.map((inventory) => (
                    <MenuItem key={inventory.expiredAt} value={inventory.expiredAt}>
                      {new Date(inventory.expiredAt).toLocaleDateString()}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={2}>
              <FormControl fullWidth variant="outlined" margin="normal" required>
                <InputLabel id={`to-zone-label-${index}`}>To Zone</InputLabel>
                <Select
                  labelId={`to-zone-label-${index}`}
                  value={transfer.toZoneId}
                  onChange={(e) => handleTransferChange(index, 'toZoneId', e.target.value)}
                  label="To Zone"
                  disabled={isFormDisabled}
                >
                  {zones?.data?.map((zone) => (
                    <MenuItem key={zone.id} value={zone.id}>
                      {zone.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={2}>
              <TextField
                fullWidth
                variant="outlined"
                margin="normal"
                label="Quantity"
                value={transfer.quantity}
                onChange={(e) => handleTransferChange(index, 'quantity', e.target.value)}
                required
                type="number"
                disabled={isFormDisabled}
                error={!!transfer.quantityError}
                helperText={transfer.quantityError}
              />
            </Grid>
            <Grid item xs={1} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <IconButton onClick={() => handleRemoveTransfer(index)} disabled={transfers.length === 1}>
                <DeleteIcon />
              </IconButton>
            </Grid>
          </Grid>
        );
      })}
      <Button onClick={handleAddTransfer} startIcon={<AddIcon />} sx={{ mt: 2 }} disabled={isLoading || isFormDisabled}>
        Add Transfer
      </Button>
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
    </Box>
  );
};

export default StaffTransfer;
