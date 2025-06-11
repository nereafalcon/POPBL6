import React from 'react';
import PropTypes from 'prop-types';

const searches = [
    { label: 'Apartments Bilbao', count: 120 },
    { label: 'Student Housing', count: 85 },
    { label: '2 Bedroom Flats', count: 95 },
    { label: 'Houses with Garden', count: 40 },
    { label: 'Pet-friendly', count: 77 },
    { label: 'Vitoria-Gasteiz', count: 65 },
];

const RelatedSearches = ({ searches, onSearchClick }) => {
    return (
        <div className="bg-white p-6 rounded-lg card-shadow">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Related searches</h3>
            <div className="flex flex-wrap gap-2">
                {searches.map((item, i) => (
                    <button
                        key={i}
                        type="button"
                        onClick={() => onSearchClick?.(item)}
                        className="bg-gray-100 text-gray-700 hover:bg-gray-200 px-3 py-1 text-sm font-medium rounded-full focus:outline-none"
                    >
                        {item.label}
                        <span className="text-xs text-gray-500 ml-1">({item.count})</span>
                    </button>
                ))}
            </div>
        </div>
    );
};

RelatedSearches.propTypes = {
    searches: PropTypes.arrayOf(
        PropTypes.shape({
            label: PropTypes.string.isRequired,
            count: PropTypes.number
        })
    ).isRequired,
    onSearchClick: PropTypes.func
};

export default RelatedSearches;
