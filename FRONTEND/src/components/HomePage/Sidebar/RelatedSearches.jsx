import React from 'react';

const searches = [
    { label: 'Apartments Bilbao', count: 120 },
    { label: 'Student Housing', count: 85 },
    { label: '2 Bedroom Flats', count: 95 },
    { label: 'Houses with Garden', count: 40 },
    { label: 'Pet-friendly', count: 77 },
    { label: 'Vitoria-Gasteiz', count: 65 },
];

const RelatedSearches = () => {
    return (
        <div className="bg-white p-6 rounded-lg card-shadow">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Related searches</h3>
            <div className="flex flex-wrap gap-2">
                {searches.map((item, i) => (
                    <a
                        key={i}
                        href="#"
                        className="bg-gray-100 text-gray-700 hover:bg-gray-200 px-3 py-1 text-sm font-medium rounded-full"
                    >
                        {item.label} <span className="text-xs text-gray-500 ml-1">{item.count}</span>
                    </a>
                ))}
            </div>
        </div>
    )
}

export default RelatedSearches;
