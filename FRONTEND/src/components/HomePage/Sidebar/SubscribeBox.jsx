import React from 'react';

const SubscribeBox = () => {
    return (
        <div className="bg-indigo-50 p-6 rounded-lg">
            <div className="flex items-center mb-3">
                <span className="material-icons text-indigo-600 icon-md mr-2">email</span>
                <h3 className="text-lg font-semibold text-gray-800">Subscribe for updates</h3>
            </div>
            <p className="text-sm text-gray-600 mb-4">
                Turn on this property alert so you don't miss out on new listings that fit your needs.
            </p>
            <input
                className="w-full bg-white px-4 py-2 border border-gray-300 rounded-lg mb-3 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="enter email"
                type="email"
            />
            <button className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-lg">
                Subscribe
            </button>
        </div>
    )
}

export default SubscribeBox;
