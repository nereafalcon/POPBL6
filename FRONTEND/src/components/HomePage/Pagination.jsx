import React from 'react';

const Pagination = () => {
    return (
        <div className="mt-10 flex justify-center">
            <nav aria-label="Pagination" className="inline-flex rounded-md shadow-sm -space-x-px">
                <a className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50" href="#">
                    <span className="sr-only">Previous</span>
                    <span className="material-icons icon-sm">chevron_left</span>
                </a>
                {[1, 2, 3, '...', 8].map((page, i) =>
                    page === '...' ? (
                        <span key={i} className="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">…</span>
                    ) : (
                        <a key={i} className={`relative inline-flex items-center px-4 py-2 border ${page === 1 ? 'border-indigo-500 bg-indigo-50 text-indigo-600' : 'border-gray-300 bg-white text-gray-700 hover:bg-gray-50'} text-sm font-medium`} href="#">{page}</a>
                    )
                )}
                <a className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50" href="#">
                    <span className="sr-only">Next</span>
                    <span className="material-icons icon-sm">chevron_right</span>
                </a>
            </nav>
        </div>
    );
}

export default Pagination;
