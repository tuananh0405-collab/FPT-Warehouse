import { Navigate, Outlet } from "react-router-dom";
import { useSelector } from "react-redux";
import Error404 from "../../utils/Error404";

const AdminRoute = () => {
  const { userInfo } = useSelector((state) => state.auth);
  const role = localStorage.getItem('role')
  return userInfo && (role.includes('ADMIN')) ? (
      <Outlet />
  ) : (
    <Navigate to="*" replace />
    // <Error404/>
  );
};
export default AdminRoute;
