import OrdersComponent from "./OrdersComponent"
import useDocumentTitle from "../../utils/UseDocumentTitle"

const Orders = () => {
  useDocumentTitle('Orders')
  return (
    <div>
      <OrdersComponent/>
    </div>
  )
}
export default Orders