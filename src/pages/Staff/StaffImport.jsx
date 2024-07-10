import "../../assets/styles/MainDash.css";
import Breadcrumbs from "../../utils/Breadcumbs";
import { Button } from "antd";
import AddIcon from '@mui/icons-material/Add';
import { Link } from "react-router-dom";
import StaffImportTable from "../../components/Orders/StaffImportTable";
import { Input } from 'antd';
import { useState } from "react";
import useDebounce from "../../utils/useDebounce"

const { Search } = Input;

function StaffImportPage() {
  const [searchValue, setSearchValue] = useState('');
  const debouncedSearchTerm = useDebounce(searchValue, 500);

  return (
    <div>
      <Breadcrumbs />
      <div className="MainDash">
        <h1 className="font-bold text-3xl py-4">Import</h1>
        <div className="staff-button-flex">
          <Button
            style={{ width: "12%", background: "#000000", display: 'flex', alignItems: 'center', justifyContent: 'center' }}
          >
            <Link
              to="/staff/import/new"
              style={{ color: "white", display: "flex", alignItems: "center", justifyContent: 'center', width: '100%', textDecoration: 'none' }}
            >
              <AddIcon
                style={{ display: "flex", alignItems: "center" }}
                sx={{ fontSize: 18 }}
              />
              New Import
            </Link>
          </Button>
          <Search className="search-input" placeholder="Search import..." onChange={e => setSearchValue(e.target.value)} enterButton />
        </div>
        <StaffImportTable searchValue={debouncedSearchTerm} />
      </div>
    </div>
  );
}

export default StaffImportPage;