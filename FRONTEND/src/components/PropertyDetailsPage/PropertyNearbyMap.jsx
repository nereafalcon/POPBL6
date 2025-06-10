import React from 'react';

const PropertyNearbyMap = ({ locationName, nearbyPlaces }) => {
    return (
        <section className="mt-6">
            <h2 className="text-2xl font-medium mb-6">{locationName}</h2>

            <div className="grid grid-cols-1 lg:grid-cols-[1fr_2fr] gap-6">
                {/* Nearby places */}
                <div className="border border-gray-300 rounded-xl py-1 px-6 divide-y-1 divide-gray-200">
                    {nearbyPlaces.map((place, i) => (
                        <div className="flex items-center gap-4 py-6 text-gray-800" key={i}>
                            <span className="text-2xl">
                              {place.icon}
                            </span>
                            <div>
                                <p className="text-lg font-medium">{place.name}</p>
                                <p className="text-base text-gray-500">{place.distance} · {place.walking_time}</p>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Map */}
                <div className="max-h-[412px] rounded-xl overflow-hidden">
                    <img src="/images/map.png" alt="Map" className="h-full object-cover" />
                </div>
            </div>
        </section>
    );
};

export default PropertyNearbyMap;