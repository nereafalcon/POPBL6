import {useEffect, useState} from 'react';
import PropertyGallery from "../components/PropertyDetailsPage/PropertyGallery.jsx";
import PropertySummary from "../components/PropertyDetailsPage/PropertySummary.jsx";
import PropertyNearbyMap from "../components/PropertyDetailsPage/PropertyNearbyMap.jsx";
import {useParams} from "react-router-dom";
import {useProperties} from "../context/PropertyContext.jsx";

const PropertyDetailsPage = () => {

    const { id } = useParams()
    const { getPropertyById, loading: contextLoading } = useProperties()

    const [property, setProperty] = useState(null)
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!contextLoading) {
            const found = getPropertyById(id);
            if (found) {
                setProperty(found);
                setLoading(false);
            }
        }
    }, [id, contextLoading, getPropertyById]);

    const PropertyDetailsSkeleton = () => (
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-7 space-y-8 animate-pulse">
            <div className="grid grid-cols-4 gap-4">
                <div className="col-span-2 h-96 bg-gray-200 rounded-xl" />
                <div className="col-span-1 h-96 bg-gray-200 rounded-xl" />
                <div className="col-span-1 h-96 bg-gray-200 rounded-xl" />
            </div>
            <div className="h-8 bg-gray-200 rounded w-1/3" />
            <div className="h-4 bg-gray-200 rounded w-1/2" />
            <div className="h-72 bg-gray-200 rounded-xl" />
        </div>
    );

    return loading
        ? <PropertyDetailsSkeleton/>
        : (
            <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-7">
                <PropertyGallery images={property.gallery}/>
                <PropertySummary property={property}/>
                <hr className="border-gray-200 mt-6"/>
                <PropertyNearbyMap locationName={property.location} nearbyPlaces={property.nearby}/>
            </div>
        );
};

export default PropertyDetailsPage;