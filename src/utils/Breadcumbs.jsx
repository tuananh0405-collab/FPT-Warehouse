import { Breadcrumb } from "antd";
import { HomeOutlined } from "@ant-design/icons";
import { Link, useLocation } from "react-router-dom";

const breadcrumbNameMap = {
  "/data": "Data",
  "/data/warehouse": "Warehouse",
  "/data/zone": "Zone",
  "/products": "Products",
  "/data/category": "Category",
  "/staffs": "Staffs",
};

const Breadcrumbs = () => {
  const location = useLocation();
  const pathSnippets = location.pathname.split("/").filter((i) => i);

  const extraBreadcrumbItems = pathSnippets.map((_, index) => {
    const url = `/${pathSnippets.slice(0, index + 1).join("/")}`;

    // Check if the current path segment is "warehouseId"
    if (url.includes("/data/zone/warehouseId")) {
      const warehouseId = pathSnippets[index];
      return (
        <Breadcrumb.Item key={url}>
          {warehouseId}
        </Breadcrumb.Item>
      );
    }

    return (
      <Breadcrumb.Item key={url}>
        <Link to={url}>{breadcrumbNameMap[url] || pathSnippets[index]}</Link>
      </Breadcrumb.Item>
    );
  });

  const breadcrumbItems = [
    <Breadcrumb.Item key="home">
      <Link to="/">
        <HomeOutlined />
      </Link>
    </Breadcrumb.Item>,
  ];

  return (
    <Breadcrumb style={{ margin: "16px 0" }} className="breadcrumb">
      {breadcrumbItems.concat(extraBreadcrumbItems)}
    </Breadcrumb>
  );
};

export default Breadcrumbs;
