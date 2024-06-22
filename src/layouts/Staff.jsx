import { Outlet } from "react-router-dom";
import '../assets/styles/MainDash.css';

import StaffSidebar from "../components/Sidebar/StaffSidebar";
const Staff = () => {
  return (
    <div className="App">
      <div className="AppGlass">
        <StaffSidebar />
        <div className="content">
          <Outlet />
        </div>
      </div>
    </div>
  );
};
export default Staff;
