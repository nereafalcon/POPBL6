import React from 'react';
import { NavLink } from 'react-router-dom';
import FilterCone from '../assets/filter-cone.svg?react';
import {useAuth} from "../context/AuthProvider.jsx";
import {Power} from "lucide-react";

const Navbar = () => {
    const { user, logout } = useAuth();

    return (
        <nav className="container mx-auto py-4 px-4 sm:px-6 lg:px-8 flex justify-between items-center">
            <div className="flex items-center space-x-4 gap-2">
                <div className="flex items-center space-x-2">
                    <FilterCone />
                    <span className="text-xl font-bold text-gray-900">MUEF</span>
                </div>
                <div className="hidden md:flex text-base font-medium text-gray-700">
                    <NavLink to="/bilatzailea" className="hover:text-indigo-600 px-4 py-2">
                        Bilatzailea
                    </NavLink>

                    <NavLink to="/nire-iragarkiak" className="hover:text-indigo-600 px-4 py-2">
                        Nire Iragarkiak
                    </NavLink>

                    <NavLink to="/txat" className="hover:text-indigo-600 px-4 py-2">
                        Txat
                    </NavLink>
                </div>
            </div>
            <div className="flex items-center space-x-4">
                <NavLink to="/nire-iragarkiak/argitaratu" className="text-sm font-medium py-2 px-4 border rounded-md text-indigo-600 hover:bg-indigo-50">
                    Iragarkia Argitaratu
                </NavLink>
                {user ? (
                    <button
                        onClick={logout}
                        className="flex items-center gap-2 py-2 px-4 border border-red-100 rounded-md bg-red-100 hover:bg-red-200 text-red-700 text-sm font-medium"
                    >
                        <Power className="w-5 h-5" />
                        <span>Saioa Itxi</span>
                        <span className="sr-only">Logout</span>
                    </button>
                ) : (
                    <NavLink to="/login" className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold py-2 px-4 rounded-md">
                        Hasi Saioa
                    </NavLink>
                )}
            </div>
        </nav>
    )
}

export default Navbar;
