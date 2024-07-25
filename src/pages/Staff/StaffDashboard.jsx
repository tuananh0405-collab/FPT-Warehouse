import React from 'react'
import MainDash from '../Admin/MainDash'
import useDocumentTitle from '../../utils/UseDocumentTitle'
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import NotificationListener from '../../components/WebSocket/NotificationListener';

const StaffDashboard = () => {
  useDocumentTitle('Dashboard')
  return (
    <div>
      <NotificationListener />
      <ToastContainer />
      <MainDash />
    </div>
  )
}

export default StaffDashboard