import { Breadcrumb } from "antd";
import { HomeOutlined } from "@ant-design/icons";
import { Link, useLocation } from "react-router-dom";

const breadcrumbNameMap = {
  "/data": "Data",
  "/data/warehouse": "Warehouse",
  "/data/zone": "Zone",
  "/data/zone/warehouseId": "Zones",
  "/products": "Products",
  "/data/category": "Category",
  "/staff": "Staff",
  "/staff/order": "Order",
  "/staff/order/import": "Import",
  "/staff/order/export": "Export",
  "/data/customer": "Customer",
};

const Breadcrumbs = () => {
  const location = useLocation();
  const pathSnippets = location.pathname.split("/").filter((i) => i);

  const extraBreadcrumbItems = pathSnippets.map((_, index) => {
    const url = `/${pathSnippets.slice(0, index + 1).join("/")}`;

    // Check if the current path segment is "warehouseId"
    if (url.includes("/data/zone/warehouseId")) {
      let warehouseId = pathSnippets[index];
      if (warehouseId === "warehouseId") {
        warehouseId = "Zones";
      }
      // console.log(warehouseId);
      return <Breadcrumb.Item key={url}>{warehouseId}</Breadcrumb.Item>;
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
