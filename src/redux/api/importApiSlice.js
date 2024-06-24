import { apiSlice } from "./apiSlice";
import { IMPORT_URL } from "../constants";

export const importApiSlice = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
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

export const { useAddImportMutation } = importApiSlice;
