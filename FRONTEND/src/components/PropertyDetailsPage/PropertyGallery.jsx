import React from 'react';

const PropertyGallery = ({ images }) => {
    return (
        <div className="grid grid-cols-4 grid-rows-2 gap-4">
            {/* Main large image (spans 2x2) */}
            <div className="col-span-2 row-span-2">
                <img
                    src={images[0]}
                    alt="Gallery Primary Image"
                    className="w-full h-full object-cover rounded-xl"
                />
            </div>

            {/* Remaining 4 images */}
            {images.slice(1, 5).map((src, i) => (
                <img
                    key={i}
                    src={src}
                    alt={`Gallery ${i + 1}`}
                    className="w-full h-full object-cover rounded-xl"
                />
            ))}
        </div>
    );
};

export default PropertyGallery;