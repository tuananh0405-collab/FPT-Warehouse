import ProductsComponent from "../../components/Products/ProductsComponent"
import useDocumentTitle from "../../utils/UseDocumentTitle"

const Products = () => {
  useDocumentTitle('Products')
  return (
    <div><ProductsComponent/></div>
  )
}
export default Products