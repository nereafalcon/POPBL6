import React from 'react';
import {Bath, Bed, Bookmark, Car, Expand, MessageCircle} from "lucide-react";
import {Link} from "react-router-dom";
import PropTypes from "prop-types";

const PropertySummary = ({ property }) => {
    const {
        title,
        rooms,
        bathrooms,
        area,
        hasGarage,
        description,
        price,
        owner,
    } = property;

    return (
        <section className="grid grid-cols-1 lg:grid-cols-[2fr_1fr] gap-8 mt-6">
            {/* Left: Main info */}
            <div>
                <h2 className="text-[2rem] font-medium mb-2">{title}</h2>

                <div className="flex flex-wrap gap-6 text-sm text-gray-700 mb-6">
                    {rooms !== undefined && (
                        <div className="flex items-center gap-2">
                            <Bed className="text-gray-500 text-base" />
                            {rooms} Logela
                        </div>
                    )}
                    {bathrooms !== undefined && (
                        <div className="flex items-center gap-2">
                            <Bath className="text-gray-500 text-base" />
                            {bathrooms} Bainugela
                        </div>
                    )}
                    {area && (
                        <div className="flex items-center gap-2">
                            <Expand className="text-gray-500 text-base" />
                            {area}m²
                        </div>
                    )}
                    {hasGarage && (
                        <div className="flex items-center gap-2">
                            <Car className="text-gray-500 text-base" />
                            Garajea
                        </div>
                    )}
                </div>

                <h3 className="text-2xl font-medium mb-2">Deskripzioa</h3>
                <p className="text-[#6E6E6E] font-light mb-4">{description}</p>
            </div>

            {/* Right: Sidebar card */}
            <div className="flex flex-col border border-gray-300 rounded-xl p-6 space-y-6 divide-y-1 divide-gray-200">
                <div className="pb-6">
                    <p className="text-base text-gray-500 mb-2">Prezioa</p>
                    <p className="text-[2rem] leading-none font-medium">
                        {price.toLocaleString()} € <span className="text-lg font-normal text-gray-500">/ hillero</span>
                    </p>
                </div>

                {owner && (
                    <div>
                        <div className="flex items-center gap-3 mb-6 border border-gray-300 p-4 rounded-lg">
                            <img
                                src={owner.avatar}
                                alt={owner.name}
                                className="w-10 h-10 rounded-full object-cover"
                            />
                            <div>
                                <p className="text-base font-medium text-gray-900">{owner.name}</p>
                                <p className="text-sm text-gray-500">Jabea</p>
                            </div>
                            <Link to="/txat" className="ml-auto">
                                <button className="flex gap-2 items-center py-2 px-4 cursor-pointer bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg">
                                    <MessageCircle className="w-4 h-4" />
                                    Mezua Bidali
                                </button>
                            </Link>
                        </div>
                    </div>
                )}

                <button className="w-full cursor-pointer text-sm text-gray-600 hover:text-indigo-600 border border-gray-300 rounded-lg py-2 flex items-center justify-center gap-2">
                    <Bookmark className="w-4 h-4" />
                    Gorde nire laster-marketan
                </button>
            </div>
        </section>
    );
};

PropertySummary.propTypes = {
    property: PropTypes.shape({
        title: PropTypes.string.isRequired,
        rooms: PropTypes.number,
        bathrooms: PropTypes.number,
        area: PropTypes.number,
        hasGarage: PropTypes.bool,
        description: PropTypes.string.isRequired,
        price: PropTypes.number.isRequired,
        owner: PropTypes.shape({
            name: PropTypes.string.isRequired,
            avatar: PropTypes.string.isRequired
        })
    }).isRequired
};

export default PropertySummary;