import React from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';

const Layout = () => {
    return (
        <div className="min-h-screen flex flex-col gap-9">
            <Navbar />
            <Outlet />
        </div>
    );
}

export default Layout;
