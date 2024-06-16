import { Flex, Spin } from "antd";
const Loading = () => {
  return (
    // <Flex align="center" gap="middle">
    //   <Spin size="large" />
    // </Flex>
    <div className="loading-container">
    <Spin size="large" />
  </div>
  );
};
export default Loading;
