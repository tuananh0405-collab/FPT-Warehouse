import { apiSlice } from "./apiSlice";
import { EXPORT_DETAIL_URL } from "../constants";

export const exportDetailApiSlice = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getAllExportDetails: builder.query({
      query: (authToken) => ({
        url: `${EXPORT_DETAIL_URL}`,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      }),
      providesTags: ["ExportDetail"],
      keepUnusedDataFor: 5,
    }),
    getExportDetailsByExportId: builder.query({
      query: ({ exportId, authToken }) => ({
        url: `${EXPORT_DETAIL_URL}/export/${exportId}`,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      }),
      providesTags: ["ExportDetail"],
      keepUnusedDataFor: 5,
    }),
    createExportDetails: builder.mutation({
      query: ({ data, authToken }) => ({
        url: `${EXPORT_DETAIL_URL}`,
        headers: {
          Authorization: `Bearer ${authToken}`,
          "Content-Type": "application/json",
        },
        method: "POST",
        body: data,
      }),
      invalidatesTags: ["ExportDetail"],
    }),
  }),
});

export const {
  useGetAllExportDetailsQuery,
  useGetExportDetailsByExportIdQuery,
  useCreateExportDetailsMutation,
} = exportDetailApiSlice;
