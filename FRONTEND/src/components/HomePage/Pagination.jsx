import React from 'react';

const Pagination = () => {
    const pages = [1, 2, 3, '...', 8];

    const handlePageClick = (page) => {
        console.log(`Go to page ${page}`);
    };

    const handlePrevious = () => {
        console.log('Go to previous page');
    };

    const handleNext = () => {
        console.log('Go to next page');
    };

    return (
        <div className="mt-10 flex justify-center">
            <nav aria-label="Pagination" className="inline-flex rounded-md shadow-sm -space-x-px">
                <button
                    type="button"
                    onClick={handlePrevious}
                    className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
                >
                    <span className="sr-only">Previous</span>
                    <span className="material-icons icon-sm">chevron_left</span>
                </button>

                {pages.map((page, i) =>
				  page === '...' ? (
					<span
					  key={`ellipsis-${i}`}
					  className="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700"
					>
					  …
					</span>
				  ) : (
					<button
					  key={`page-${page}`}
					  type="button"
					  onClick={() => handlePageClick(page)}
					  className={`relative inline-flex items-center px-4 py-2 border ${
						page === 1
						  ? 'border-indigo-500 bg-indigo-50 text-indigo-600'
						  : 'border-gray-300 bg-white text-gray-700 hover:bg-gray-50'
					  } text-sm font-medium`}
					>
					  {page}
					</button>
				  )
				)}


                <button
                    type="button"
                    onClick={handleNext}
                    className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
                >
                    <span className="sr-only">Next</span>
                    <span className="material-icons icon-sm">chevron_right</span>
                </button>
            </nav>
        </div>
    );
};

export default Pagination;
