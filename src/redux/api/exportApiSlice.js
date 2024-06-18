import { apiSlice } from "./apiSlice";
import { EXPORT_URL } from "../constants";

export const exportApiSlice = apiSlice.injectEndpoints({
    endpoints: (builder) => ({
        getAllExportsByWarehouseid: builder.query({
            query: ({ warehouseId, authToken, pageNo, sortBy, direction }) => ({
                url: `${EXPORT_URL}/by-warehouse/${warehouseId}`,
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                },
                params: {
                    pageNo,
                    sortBy,
                    direction,
                },
            }),
            providesTags: ["Export"],
            keepUnusedDataFor: 5,
        }),
        getTotalExportsByWarehouseidAndFilterByStatus: builder.query({
            query: ({ warehouseId, authToken, status }) => ({
                url: `${EXPORT_URL}/by-warehouse/total/${warehouseId}`,
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                },
                params: {
                    status,
                },
            }),
        }),
    })
});

export const {
    useGetAllExportsByWarehouseidQuery,
    useGetTotalExportsByWarehouseidAndFilterByStatusQuery,
} = exportApiSlice;
