import {useProperties} from "../context/PropertyContext.jsx";
import {Link, NavLink} from "react-router-dom";
import PropertyCard from "../components/PropertyCard.jsx";
import React from "react";
import {Plus} from "lucide-react";

const MyPropertiesPage = () => {

    const { createdProperties } = useProperties();

    const PropertyCardSkeleton = () => (
        <div className="rounded-2xl overflow-hidden border p-4 animate-pulse space-y-4 bg-white">
            <div className="w-full h-60 bg-gray-200 rounded-xl" />
            <div className="h-5 bg-gray-200 rounded w-1/2" />
            <div className="h-4 bg-gray-200 rounded w-1/3" />
            <div className="flex gap-3">
                <div className="h-4 w-12 bg-gray-200 rounded" />
                <div className="h-4 w-12 bg-gray-200 rounded" />
                <div className="h-4 w-12 bg-gray-200 rounded" />
            </div>
        </div>
    );

    return (
        <main className="container mx-auto px-4 sm:px-6 lg:px-8">
            <div className="space-y-11">
                <div className="flex justify-between items-center">
                    <h2 className="text-2xl font-medium text-gray-800">
                        Nire Iragarkiak ({createdProperties.length})
                    </h2>
                    <NavLink to="/nire-iragarkiak/argitaratu" className="flex text-sm font-medium py-2 px-4 border rounded-md bg-indigo-600 hover:bg-indigo-700 text-white">
                        <Plus className="w-5 h-5 mr-2" />
                        Iragarki Berria
                    </NavLink>
                </div>
                <div className="grid gap-16 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
                    {createdProperties.length === 0
                        ? Array.from({ length: 8 }).map((_, index) => <PropertyCardSkeleton key={index} />)
                        : createdProperties.map((property) => (
                            <Link to={`/properties/${property.id}`} key={`link-${property.id}`}>
                                <PropertyCard key={property.id} property={property} canBeDeleted />
                            </Link>
                        ))
                    }
                </div>
            </div>
        </main>
    );
}

export default MyPropertiesPage;