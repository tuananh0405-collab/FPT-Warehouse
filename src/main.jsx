import ReactDOM from "react-dom/client";
import App from "./layouts/App.jsx";
import "./index.css";
import {
  Route,
  RouterProvider,
  createBrowserRouter,
  createRoutesFromElements,
} from "react-router-dom";
import Dashboard from "./pages/Admin/Dashboard.jsx";
import Orders from "./pages/Admin/Orders.jsx";
import Staffs from "./pages/Admin/Staffs.jsx";
import Products from "./pages/Admin/Products.jsx";
import Data from "./pages/Admin/Data.jsx";
import Auth from "./layouts/Auth.jsx";
import { Provider } from "react-redux";
import store from "./redux/store.js";
import AdminRoute from "./components/Routes/AdminRoute.jsx";
import StaffRoute from "./components/Routes/StaffRoute.jsx";
import DataWarehouse from "./components/Data/Warehouse/DataWarehouse.jsx";
import DataZone from "./components/Data/Zone/DataZone.jsx";
import DataCategory from "./components/Data/Category/DataCategory.jsx";
import Error404 from "./utils/Error404.jsx";
import Loading from "./utils/Loading.jsx";
import WarehouseZone from "./components/Data/Zone/WarehouseZone.jsx";
import Staff from "./layouts/Staff.jsx";
import StaffOrder from "./components/Orders/StaffOrder.jsx";
import StaffData from "./components/Data/StaffData/StaffData.jsx";
import StaffDashboard from "./components/MainDash/StaffDash/StaffDashboard.jsx";
import StaffZone from "./components/Data/StaffData/StaffZone/StaffZone.jsx";
import StaffProduct from "./components/Data/StaffData/StaffProduct/StaffProduct.jsx";
import StaffCustomer from "./components/Data/StaffData/StaffCustomer/StaffCustomer.jsx";
const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      {/* AUTH  */}
      <Route path="/" element={<Auth />}/>
      {/* ADMIN  */}
      <Route path="/" element={<App />}>
        <Route path="" element={<AdminRoute />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/orders" element={<Orders />} />
          <Route path="/data" element={<Data />} />
          <Route path="/staffs" element={<Staffs />} />
          <Route path="/products" element={<Products />} />
          <Route path="/data/warehouse" element={<DataWarehouse />} />
          <Route path="/data/zone" element={<DataZone />} />
          <Route path="/data/zone/warehouseId/:id" element={<WarehouseZone />} />
          <Route path="/data/category" element={<DataCategory />} />
        </Route>
      </Route>
      {/* STAFF  */}
      <Route path="/staff" element={<Staff />}>
        <Route path="" element={<StaffRoute />}>
          <Route path="/staff/dashboard" element={<StaffDashboard />} />
          <Route path="/staff/orders" element={<StaffOrder />} />
          <Route path="/staff/data" element={<StaffData />} />
          <Route path="/staff/data/zone" element={<StaffZone />} />
          <Route path="/staff/data/products" element={<StaffProduct />} />
          <Route path="/staff/data/customers" element={<StaffCustomer />} />
        </Route>
      </Route>
      {/* Route 404 cho tất cả các route khác */}
      <Route path="*" element={<Error404 />} />
    </>
  )
);

ReactDOM.createRoot(document.getElementById("root")).render(
  <Provider store={store}>
    <RouterProvider router={router} />
  </Provider>
);
