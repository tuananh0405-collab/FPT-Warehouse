import OrdersComponent from "../../components/Orders/OrdersComponent"
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