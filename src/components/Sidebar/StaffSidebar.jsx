import { useEffect, useState } from "react";
import { useNavigate, useLocation, Navigate } from "react-router-dom";
import '../../assets/styles/MainDash.css'
import Logo from "../../assets/images/FPT_logo_2010.png";
import { UilSignOutAlt, UilBars } from "@iconscout/react-unicons";
import { StaffSidebarData } from "../../Data/Data";
import { motion } from "framer-motion";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../../redux/features/auth/authSlice";
import { useGetWarehouseByIdQuery } from "../../redux/api/warehousesApiSlice";

const StaffSidebar = () => {
  const [expanded, setExpaned] = useState(true);
  const navigate = useNavigate();
  const location = useLocation();

  const [warehouseId, setWarehouseId] = useState(null);

  const userInfo = useSelector((state) => state.auth);
  if (!userInfo) {
    return <Navigate to={'/'} replace />;
  }
  let authToken;
  let wid;
  if (userInfo && userInfo.userInfo && userInfo.userInfo.data) {
    authToken = userInfo.userInfo.data.token;
    wid = userInfo.userInfo.data.warehouseId;
  }

  const {
    data: warehouse,
    isLoading: isLoadingWarehouse,
    error: errorWarehouse,
  } = useGetWarehouseByIdQuery({ id: wid, authToken });
  useEffect(() => {
    const id = userInfo.userInfo.data.warehouseId;
    setWarehouseId(id);
  }, []);

  const sidebarVariants = {
    true: {
      left: "0",
    },
    false: {
      left: "-60%",
    },
  };

  const handleMenuItemClick = (link) => {
    navigate(link);
  };

  const dispatch = useDispatch();
  const handleLogout = async () => {
    try {
      dispatch(logout());
      navigate("/");
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <>
      <div
        className="bars"
        style={expanded ? { left: "60%" } : { left: "5%" }}
        onClick={() => setExpaned(!expanded)}
      >
        <UilBars />
      </div>
      <motion.div
        className="sidebar"
        variants={sidebarVariants}
        animate={window.innerWidth <= 768 ? `${expanded}` : ""}
      >
        {/* logo */}
        <div className="logo">
          <img src={Logo} alt="logo" />
          <span>
            <span>WHA</span>
          </span>
        </div>

        {warehouseId && (
          <div className="warehouse-id">
            <span>{warehouse?.data?.name}</span>
          </div>
        )}

        <div className="menu">
          {StaffSidebarData.map((item, index) => {
            return (
              <div
                className={
                  location.pathname.startsWith(item.link) ? "menuItem active" : "menuItem"
                }
                key={index}
                onClick={() => handleMenuItemClick(item.link)}
              >
                <item.icon />
                <span>{item.heading}</span>
              </div>
            );
          })}
          {/* signoutIcon */}
          <div className="menuItem logout">
            <UilSignOutAlt onClick={handleLogout} />
          </div>
        </div>
      </motion.div>
    </>
  );
};

export default StaffSidebar;
