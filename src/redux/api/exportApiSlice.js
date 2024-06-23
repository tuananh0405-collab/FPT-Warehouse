import { apiSlice } from "./apiSlice";
import { EXPORT_URL } from "../constants";

export const exportApiSlice = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getAllExportsByWarehouseid: builder.query({
      query: ({
        warehouseId,
        authToken,
        pageNo,
        sortBy,
        direction,
        status,
      }) => ({
        url: `${EXPORT_URL}/by-warehouse/${warehouseId}`,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
        params: {
          pageNo,
          sortBy,
          direction,
          status,
        },
      }),
      providesTags: ["Export"],
      keepUnusedDataFor: 5,
    }),
    getTotalExportsByWarehouseidAndFilterByStatus: builder.query({
      query: ({ warehouseId, authToken, status }) => ({
        url: `${EXPORT_URL}/by-warehouse/total/${warehouseId}`,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
        params: {
          status,
        },
      }),
    }),
    addExport: builder.mutation({
      query: ({ data, authToken }) => ({
        url: `${EXPORT_URL}/add`,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
        method: "POST",
        body: data,
      }),
      invalidatesTags: ["Export"],
    }),
    getExportById: builder.query({
      query: ({ exportId, authToken }) => ({
        url: `${EXPORT_URL}/${exportId}`,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      }),
      invalidatesTags: ["Export"],
    }),
  })
});

export const {
  useGetAllExportsByWarehouseidQuery,
  useGetTotalExportsByWarehouseidAndFilterByStatusQuery,
  useAddExportMutation,
  useGetExportByIdQuery,
} = exportApiSlice;
