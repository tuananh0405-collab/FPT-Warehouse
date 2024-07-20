import "../../assets/styles/MainDash.css";
import Breadcrumbs from "../../utils/Breadcumbs";
import { Button } from "antd";
import AddIcon from '@mui/icons-material/Add';
import { Link } from "react-router-dom";
import StaffExportTable from "../../components/Orders/StaffExportTable";

function StaffExpport() {
    return (
        <div>
            <div className="">
                <Breadcrumbs />
                <h1 className="font-bold text-3xl py-4">Export</h1>
                <div className="staff-button-flex">
                    <Button
                        type="primary"
                        className="w-1/8 flex justify-center items-center mb-4"
                    >
                        <Link
                            to="/staff/export/new"
                            style={{ color: "white", display: "flex", alignItems: "center", justifyContent: 'center', width: '100%', textDecoration: 'none' }}
                        >
                            <AddIcon
                                style={{ display: "flex", alignItems: "center" }}
                                sx={{ fontSize: 18 }}
                            />
                            New Export
                        </Link>
                    </Button>
                </div>
                <StaffExportTable />
            </div>
        </div>
    );
}

export default StaffExpport;