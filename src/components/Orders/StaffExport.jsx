import "./MainDash.css";
import Breadcrumbs from "../../utils/Breadcumbs";
import { Button } from "antd";
import AddIcon from '@mui/icons-material/Add';
import { Link } from "react-router-dom";
import ExportTable from "./ExportTable";

function StaffExpport() {
    return (
        <>
            <Breadcrumbs />
            <div className="MainDash">
                <h1>Export</h1>
                <Button
                    style={{ width: "12%", background: "#000000", display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '20px' }}
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
                <ExportTable />
            </div>
        </>
    );
}

export default StaffExpport;