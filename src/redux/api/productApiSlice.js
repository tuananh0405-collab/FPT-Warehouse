import { PRODUCT_URL } from "../constants";
import { apiSlice } from "./apiSlice";

export const productApiSlice = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    addProduct: builder.mutation({
      query: ({ productData, authToken }) => ({
        url: `${PRODUCT_URL}`,
        method: "POST",
        body: productData,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      }),
      invalidatesTags: ["Product"],
    }),
    getAllProducts: builder.query({
      query: (authToken) => ({
        url: `${PRODUCT_URL}`,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      }),
      providesTags: ["Product"],
      keepUnusedDataFor: 5,
    }),
    getProductById: builder.query({
      query: ( productId ) => ({
        url: `${PRODUCT_URL}/${productId}`,
      }),
      keepUnusedDataFor: 5,
    }),
    updateProduct: builder.mutation({
      query: ({ productId, formData, authToken }) => ({
        url: `${PRODUCT_URL}/${productId}`,
        method: "PUT",
        body: formData,
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      }),
      invalidatesTags: ["Product"],
    }),
    deleteProduct: builder.mutation({
      query: ({ productId, authToken }) => ({
        url: `${PRODUCT_URL}/${productId}`,
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      }),
      invalidatesTags: ["Product"],
    }),
  }),
});

export const {
  useAddProductMutation,
  useGetAllProductsQuery,
  useGetProductByIdQuery,
  useUpdateProductMutation,
  useDeleteProductMutation,
} = productApiSlice;
