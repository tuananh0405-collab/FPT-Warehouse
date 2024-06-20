import React, { useState, useEffect } from "react";
import "../../assets/styles/MainDash.css";
import { Table } from "antd";
import Breadcrumbs from "../../utils/Breadcumbs";
import { Modal, Button, Form, Input, Select } from "antd";
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';

const handleSelectExportType = (e, setExportType) => {
    setExportType(e.key);
}

const exportTypeItems = [
    "CUSTOMER",
    "WAREHOUSE",
    "WASTE"
];

const warehouseItems = [
    {
        id: 1,
        name: "Springfield"
    },
    {
        id: 2,
        name: "Shelbyville"
    },
    {
        id: 3,
        name: "Ogdenville"
    }
];


function StaffNewExport() {
    const [isTypeWarehouse, setIsTypeWarehouse] = useState(false);
    const [form] = Form.useForm();

    return (
        <>
            <Breadcrumbs />
            <div className="MainDash">
                <h1>New Export</h1>
                <Form form={form} layout="vertical">
                    <Form.Item label="Description" name="description" rules={[{ required: true, message: 'Please input the export description!' }]}>
                        <Input style={{}} placeholder="Product Description" />
                    </Form.Item>
                    <Form.Item label="Export type" name="export enum" rules={[{ required: true, message: 'Please select export type!' }]}>
                        <Select placeholder={"Select export type..."}>
                            {exportTypeItems.map(item => (
                                <Option key={item}>{item}</Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item label="To:" name="warehouse to" rules={[{ required: true, message: 'Please select warehouse to transfer!' }]}>
                        <Select placeholder={"Select warehouse to..."}>
                            {warehouseItems.map(item => (
                                <Option key={item.id}>{item.name}</Option>
                            ))}
                        </Select>
                    </Form.Item>
                </Form>
            </div >
        </>
    );
}

export default StaffNewExport;