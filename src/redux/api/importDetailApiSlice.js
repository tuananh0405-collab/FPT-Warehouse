import { apiSlice } from "./apiSlice";
import { IMPORT_DETAIL_URL } from "../constants";

export const importDetailApiSlice = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    createImportDetails: builder.mutation({
      query: ({ data, authToken }) => ({
        url: `${IMPORT_DETAIL_URL}`,
        headers: {
          Authorization: `Bearer ${authToken}`,
          "Content-Type": "application/json",
        },
        method: "POST",
        body: data,
      }),
      invalidatesTags: ["ImportDetail"],
    }),
  }),
});

export const { useCreateImportDetailsMutation } = importDetailApiSlice;
