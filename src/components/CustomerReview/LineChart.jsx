import React, { useState } from "react";
import ReactApexChart from "react-apexcharts";

const lineChartOptions = {
  legend: {
    show: false,
    position: "top",
    horizontalAlign: "left",
  },
  colors: ["#478CCF", "#4535C1"],
  chart: {
    fontFamily: "Satoshi, sans-serif",
    height: "100%",
    width: "100%",
    type: "line",
    dropShadow: {
      enabled: true,
      color: "#623CEA14",
      top: 10,
      blur: 4,
      left: 0,
      opacity: 0.1,
    },
    toolbar: {
      show: false,
    },
  },
  responsive: [
    {
      breakpoint: 1024,
      options: {
        chart: {
          height: "100%",
          width: "100%",
        },
      },
    },
    {
      breakpoint: 1366,
      options: {
        chart: {
          height: "100%",
          width: "100%",
        },
      },
    },
  ],
  stroke: {
    width: [2, 2],
    curve: "smooth",
  },
  grid: {
    xaxis: {
      lines: {
        show: true,
      },
    },
    yaxis: {
      lines: {
        show: true,
      },
    },
  },
  dataLabels: {
    enabled: false,
  },
  markers: {
    size: 4,
    colors: "#fff",
    strokeColors: ["#478CCF", "#4535C1"],
    strokeWidth: 3,
    strokeOpacity: 0.9,
    strokeDashArray: 0,
    fillOpacity: 1,
    discrete: [],
    hover: {
      size: undefined,
      sizeOffset: 5,
    },
  },
  xaxis: {
    type: "category",
    categories: [
      "Sep",
      "Oct",
      "Nov",
      "Dec",
      "Jan",
      "Feb",
      "Mar",
      "Apr",
      "May",
      "Jun",
      "Jul",
      "Aug",
    ],
    axisBorder: {
      show: false,
    },
    axisTicks: {
      show: false,
    },
  },
  yaxis: {
    title: {
      style: {
        fontSize: "0px",
      },
    },
    min: 0,
    max: 100,
  },
};

const LineChart = () => {
  const [state, setState] = useState({
    series: [
      {
        name: "Product A",
        data: [10, 41, 35, 51, 49, 62, 69, 91, 148],
      },
      {
        name: "Product B",
        data: [23, 42, 35, 27, 43, 22, 17, 12, 45],
      },
    ],
  });

  const handleReset = () => {
    setState((prevState) => ({
      ...prevState,
    }));
  };

  handleReset;

  return (
    <div className="w-full h-full rounded-sm  shadow-default  ">
      <div>
        <div id="lineChart" className="-ml-5">
          <ReactApexChart
            options={lineChartOptions}
            series={state.series}
            type="line"
            // height={300}
          />
        </div>
      </div>
    </div>
  );
};

export default LineChart;
