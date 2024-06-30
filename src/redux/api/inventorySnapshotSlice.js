import { apiSlice } from "./apiSlice";
import { INVENTORY_SNAPSHOT_URL } from "../constants";

export const inventorySnapshotApiSlice = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getInventorySnapshotReport: builder.query({
      query: ({ year, month, authToken }) => ({
        url: `${INVENTORY_SNAPSHOT_URL}/report`,
        params: { year, month },
        headers: {
          'Authorization': `Bearer ${authToken}`,
        },
        responseHandler: (response) => response.blob(), // Handling as blob for file download
      }),
    }),
  }),
});

export const { useGetInventorySnapshotReportQuery } = inventorySnapshotApiSlice;
