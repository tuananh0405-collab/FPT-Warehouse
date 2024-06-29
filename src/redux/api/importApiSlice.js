import { apiSlice } from "./apiSlice";
import { IMPORT_URL } from "../constants";

export const importApiSlice = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getAllImports: builder.query({
      query: ({ authToken, page = 1 }) => ({
        url: `${IMPORT_URL}/import`,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
        params: {
          page,
        },
      }),
      providesTags: ["Import"],
      keepUnusedDataFor: 5,
    }),
    getImportById: builder.query({
      query: ({ importId, authToken }) => ({
        url: `${IMPORT_URL}/${importId}`,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      }),
      providesTags: ["Import"],
    }),
    addImport: builder.mutation({
      query: ({ data, authToken }) => ({
        url: `${IMPORT_URL}`,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
        method: "POST",
        body: data,
      }),
      invalidatesTags: ["Import"],
    }),
  }),
});

export const {useGetAllImportsQuery, useGetImportByIdQuery, useAddImportMutation } = importApiSlice;
