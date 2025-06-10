import { useState } from "react";
import {Bed, Bath, Expand, Bookmark, BookmarkCheck, Trash2} from "lucide-react";
import PropTypes from "prop-types";
import {useProperties} from "../context/PropertyContext.jsx";

const PropertyCard = ({ property, canBeDeleted = false }) => {

    const { deletePropertyById } = useProperties();

    const [bookmarked, setBookmarked] = useState(property.bookmarked || false);

    const toggleBookmark = () => {
        setBookmarked(!bookmarked);
    }

    const handleDelete = (e) => {
        e.preventDefault();
        e.stopPropagation(); // Prevent click from propagating to the card
        if (canBeDeleted) {
            deletePropertyById(property.id);
        }
    }
    
    return (
        <div className="cursor-pointer rounded-2xl space-y-3 bg-white">
            {/* Image */}
            <div className="relative group">
                <img
                    src={property.gallery[0]}
                    alt="Property"
                    className="w-full h-65 object-cover rounded-xl"
                />
                <button
                    onClick={toggleBookmark}
                    aria-label={bookmarked ? "Remove from bookmarks" : "Add to bookmarks"}
                    className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200 bg-black/50 p-2 rounded-full shadow hover:bg-black/70"
                >
                    {
                        bookmarked ?
                        <BookmarkCheck className="w-5 h-5 text-white" /> :
                        <Bookmark className="w-5 h-5 text-white" />
                    }
                </button>

                {canBeDeleted && (
                    <button
                        onClick={handleDelete}
                        className="absolute top-2 left-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200 bg-red-500 p-2 rounded-full shadow hover:bg-red-600"
                        aria-label="Delete property"
                    >
                        <Trash2 className="w-5 h-5 text-white" />
                    </button>
                )}
            </div>

            {/* Price */}
            <div className="text-[1.75rem] font-semibold">
                {property.price.toLocaleString()} € <span className="text-sm font-normal text-gray-500">/ hillero</span>
            </div>

            {/* Location */}
            <div className="flex flex-col gap-1">
                <h4 className="text-xl font-medium">
                    {property.location}
                </h4>
                <span className="text-base text-[#6E6E6E] font-light">7 min oinez Deustuko Unibertsitatera</span>
            </div>

            {/* Features */}
            <div className="flex items-center gap-4 text-sm text-gray-700 pt-2">
                <div className="flex items-center gap-3">
                    <Bed className="w-5 h-5" />
                    <span>{property.rooms} LOGELA</span>
                </div>
                <span className="text-gray-400 font-black">·</span>
                <div className="flex items-center gap-3">
                    <Bath className="w-5 h-5" />
                    <span>{property.bathrooms} BAINUGELA</span>
                </div>
                <span className="text-gray-400 font-black">·</span>
                <div className="flex items-center gap-3">
                    <Expand className="w-5 h-5" />
                    <span>{property.area} m²</span>
                </div>
            </div>
        </div>
    );
}

PropertyCard.propTypes = {
    property: PropTypes.shape({
        gallery: PropTypes.arrayOf(PropTypes.string).isRequired,
        price: PropTypes.number.isRequired,
        location: PropTypes.string.isRequired,
        rooms: PropTypes.number.isRequired,
        bathrooms: PropTypes.number.isRequired,
        area: PropTypes.number.isRequired,
        bookmarked: PropTypes.bool
    }).isRequired
};

export default PropertyCard;