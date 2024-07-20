import  { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import '../../assets/styles/MainDash.css'
import Logo from "../../assets/images/FPT_logo_2010.png";
import { UilSignOutAlt, UilBars } from "@iconscout/react-unicons";
import { SidebarData } from "../../Data/Data";
import { motion } from "framer-motion";
import { useDispatch } from "react-redux";
import { logout } from "../../redux/features/auth/authSlice";

const Sidebar = () => {
  const [expanded, setExpaned] = useState(true);
  const navigate = useNavigate();
  const location = useLocation();

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

        <div className="menu">
          {SidebarData.map((item, index) => {
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

export default Sidebar;
