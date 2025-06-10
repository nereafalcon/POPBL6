import React, { useState } from 'react';

const amenitiesList = [
    { key: 'ad_haslift', label: 'Lift' },
    { key: 'ad_hasgarden', label: 'Garden' },
    { key: 'ad_hasterrace', label: 'Terrace' },
    { key: 'ad_hasparkingspace', label: 'Parking' },
    { key: 'ad_hasairconditioning', label: 'Air Conditioning' }
];

const FiltersModal = ({ onApply }) => {
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [bedrooms, setBedrooms] = useState('');
    const [amenities, setAmenities] = useState({});

    const toggleAmenity = (key) => {
        setAmenities((prev) => ({ ...prev, [key]: !prev[key] }));
    };

    const handleApply = () => {
        onApply({ minPrice, maxPrice, bedrooms, amenities });
    };

    return (
        <div className="p-6 bg-white border border-gray-300 rounded-lg shadow-lg w-full max-w-md absolute top-20 right-4 z-50">
            <h2 className="text-lg font-semibold mb-4">Filters</h2>

            {/* Price Range */}
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">Price Range (€)</label>
                <div className="flex gap-2">
                    <input
                        type="number"
                        value={minPrice}
                        onChange={(e) => setMinPrice(e.target.value)}
                        placeholder="Min"
                        className="w-1/2 border border-gray-300 rounded-lg px-3 py-2"
                    />
                    <input
                        type="number"
                        value={maxPrice}
                        onChange={(e) => setMaxPrice(e.target.value)}
                        placeholder="Max"
                        className="w-1/2 border border-gray-300 rounded-lg px-3 py-2"
                    />
                </div>
            </div>

            {/* Bedrooms */}
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">Bedrooms</label>
                <select
                    value={bedrooms}
                    onChange={(e) => setBedrooms(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                >
                    <option value="">Any</option>
                    <option value="1">1+</option>
                    <option value="2">2+</option>
                    <option value="3">3+</option>
                    <option value="4">4+</option>
                </select>
            </div>

            {/* Amenities */}
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">Amenities</label>
                <div className="grid grid-cols-2 gap-2">
                    {amenitiesList.map(({ key, label }) => (
                        <label key={key} className="flex items-center space-x-2">
                            <input
                                type="checkbox"
                                checked={!!amenities[key]}
                                onChange={() => toggleAmenity(key)}
                                className="rounded text-indigo-600 focus:ring-indigo-500"
                            />
                            <span className="text-sm text-gray-700">{label}</span>
                        </label>
                    ))}
                </div>
            </div>

            <div className="flex justify-end gap-2">
                <button
                    className="text-sm px-4 py-2 border rounded-lg text-gray-700 border-gray-300 hover:bg-gray-100"
                    onClick={() => {
                        setMinPrice('');
                        setMaxPrice('');
                        setBedrooms('');
                        setAmenities({});
                    }}
                >
                    Reset
                </button>
                <button
                    className="text-sm px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
                    onClick={handleApply}
                >
                    Apply
                </button>
            </div>
        </div>
    );
};

export default FiltersModal;