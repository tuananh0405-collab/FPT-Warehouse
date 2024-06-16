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
const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      <Route path="/" element={<App />}>
        <Route path="" element={<AdminRoute />}>
          <Route path="/" element={<Error404 />} />
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

        <Route path="" element={<StaffRoute />}>
          <Route path="/loading" element={<Loading />} />
          <Route path="/orders2" element={<Orders />} />

        </Route>
      </Route>

      <Route path="/login" element={<Auth />}></Route>
    </>
  )
);

ReactDOM.createRoot(document.getElementById("root")).render(
  <Provider store={store}>
    <RouterProvider router={router} />
  </Provider>
);
