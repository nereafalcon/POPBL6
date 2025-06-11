import React from 'react';
import PropTypes from 'prop-types';

const items = [
    {
        title: 'Cozy Studio, Indautxu',
        location: '€800/month · Bilbao',
        image: 'https://example.com/thumb1.jpg',
    },
    {
        title: 'Beachfront Apartment, Sopela',
        location: '€1100/month · Sopela',
        image: 'https://example.com/thumb2.jpg',
    },
];

const RecentlyViewed = ({ items, onItemClick }) => {
    return (
        <div className="bg-white p-6 rounded-lg card-shadow">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Recently Viewed</h3>
            <div className="space-y-3">
                {items.map((item, i) => (
                    <button
                        key={i}
                        onClick={() => onItemClick?.(item)}
                        className="flex items-center group w-full text-left focus:outline-none"
                        type="button"
                    >
                        <img
                            alt={`Thumbnail of ${item.title}`}
                            className="w-12 h-12 object-cover rounded-md mr-3"
                            src={item.image}
                        />
                        <div>
                            <p className="text-sm font-medium text-indigo-600 group-hover:underline">
                                {item.title}
                            </p>
                            <p className="text-xs text-gray-500">{item.location}</p>
                        </div>
                    </button>
                ))}
            </div>
        </div>
    );
};

RecentlyViewed.propTypes = {
    items: PropTypes.arrayOf(
        PropTypes.shape({
            title: PropTypes.string.isRequired,
            location: PropTypes.string,
            image: PropTypes.string.isRequired
        })
    ).isRequired,
    onItemClick: PropTypes.func
};

export default RecentlyViewed;
