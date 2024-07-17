import { useState } from "react";
import "../../assets/styles/MainDash.css";
import { CircularProgressbar } from "react-circular-progressbar";
import "react-circular-progressbar/dist/styles.css";
import { motion, AnimatePresence, LayoutGroup } from "framer-motion";
import {
  UilTimes,
  UilUsdSquare,
  UilMoneyWithdrawal,
  UilClipboardAlt,
} from "@iconscout/react-unicons";
import Chart from "react-apexcharts";
import { useSelector } from "react-redux";
import { useGetAllExportsByWarehouseidQuery, useGetAllExportsQuery } from "../../redux/api/exportApiSlice";
import DonutChart from "../../components/CustomerReview/DonutChart";
import {
  useGetAllImports2Query,
  useGetAllImportsQuery,
} from "../../redux/api/importApiSlice";
import BarChart from "../../components/CustomerReview/BarChart";
import LineChart from "../../components/CustomerReview/LineChart";

// Card data
const cardsData = {
  title: "Warehouses",
  color: {
    // backGround: "linear-gradient(180deg, #bb67ff 0%, #c484f3 100%)",
    backGround: "linear-gradient(180deg, #77E4C8 0%, #36C2CE 100%)",
    boxShadow: "0px 5px 5px 0px #088395",
  },
  series: [
    {
      name: "Warehouses",
      data: [31, 40, 28, 51, 42, 109, 100],
    },
  ],
};

// Expanded Card
function ExpandedCard({ param }) {
  const data = {
    options: {
      chart: {
        type: "area",
        height: "auto",
      },
      dropShadow: {
        enabled: false,
        top: 0,
        left: 0,
        blur: 3,
        color: "#000",
        opacity: 0.35,
      },
      fill: {
        colors: ["#fff"],
        type: "gradient",
      },
      dataLabels: {
        enabled: false,
      },
      stroke: {
        curve: "smooth",
        colors: ["white"],
      },
      tooltip: {
        x: {
          format: "dd/MM/yy HH:mm",
        },
      },
      grid: {
        show: true,
      },
      xaxis: {
        type: "datetime",
        categories: [
          "2018-09-19T00:00:00.000Z",
          "2018-09-19T01:30:00.000Z",
          "2018-09-19T02:30:00.000Z",
          "2018-09-19T03:30:00.000Z",
          "2018-09-19T04:30:00.000Z",
          "2018-09-19T05:30:00.000Z",
          "2018-09-19T06:30:00.000Z",
        ],
      },
    },
  };

  return (
    <motion.div
      className="ExpandedCard"
      style={{
        background: param.color.backGround,
        boxShadow: param.color.boxShadow,
      }}
      layout
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
    >
      <span>{param.title}</span>
      <div className="chartContainer">
        <Chart options={data.options} series={param.series} type="area" />
      </div>
    </motion.div>
  );
}

const StaffMainDash = () => {
  const userInfo = useSelector((state) => state.auth);
  let authToken;
  let wid;
  if (userInfo && userInfo.userInfo && userInfo.userInfo.data) {
    authToken = userInfo.userInfo.data.token;
    wid = userInfo.userInfo.data.warehouseId;
  }
  const {
    data: allExports,
    error: isError,
    isLoading: isLoading,
  } = useGetAllExportsQuery(authToken);
//   useGetAllExportsByWarehouseidQuery({
//     wid,
//     authToken,
//     pageNo: 1,
// });
  console.log("allExports")
  console.log(allExports)
  const {
    data: allImports,
    error: isError2,
    isLoading: isLoading2,
  } = useGetAllImports2Query({ authToken });
  // console.log(allImports);
  return (
    <div className="MainDash">
      <div class="container mx-auto">
      <div className="flex justify-center items-center h-full">
      <div className="max-w-lg w-full rounded-xl">
        <LineChart importData={allImports?.data} exportData={allExports?.data} />
      </div>
    </div>
          
        <div class="grid grid-cols-1 md:grid-cols-1 lg:grid-cols-2 ">
          <div class="flex justify-center text-6xl  rounded-xl">
            <DonutChart data={allExports} title={"Export"} />
          </div>
          <div class="flex justify-center text-6xl  rounded-xl">
            <DonutChart data={allImports} title={"Import"} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default StaffMainDash;
