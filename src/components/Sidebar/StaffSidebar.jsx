import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "./Sidebar.css";
import Logo from "../../imgs/warehouse-1073.png";
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

  const userInfo = useSelector((state) => state.auth)
  const authToken = userInfo.userInfo.data.token;

  console.log(userInfo.userInfo.data.warehouseId);
  const wid = userInfo.userInfo.data.warehouseId


  const {
    data: warehouse,
    isLoading: isLoadingWarehouse,
    error: errorWarehouse,
  } = useGetWarehouseByIdQuery({ id: wid, authToken });
  useEffect(() => {
    const id = userInfo.userInfo.data.warehouseId
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
            FPT<span>-</span>W
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
          <div className="menuItem">
            <UilSignOutAlt onClick={handleLogout} />
          </div>
        </div>
      </motion.div>
    </>
  );
};

export default StaffSidebar;
