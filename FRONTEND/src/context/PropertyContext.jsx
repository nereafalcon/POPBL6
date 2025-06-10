import {createContext, useCallback, useContext, useEffect, useMemo, useState} from "react";
import useLocalStorage from "../hooks/useLocalStorage.jsx";

const PropertyContext = createContext();

export const PropertyProvider = ({ children }) => {
    const [properties, setProperties] = useState([]);
    const [loading, setLoading] = useState(true);

    const [createdProperties, setCreatedProperties] = useLocalStorage("createdProperties", []); // For storing new properties added by the user

    // Filters & Sorting
    const [locationQuery, setLocationQuery] = useState("");
    const [sortBy, setSortBy] = useState("relevant");
    const [priceRange, setPriceRange] = useState(""); // e.g. "800-1200"
    const [areaRange, setAreaRange] = useState("");


    useEffect(() => {
        fetch("/data/properties.json")
            .then((res) => res.json())
            .then((data) => {
                // Simulate a delay for loading state
                // setTimeout(
                //     () => {
                //         setProperties(data);
                //         setLoading(false);
                //     },
                //     3000
                // )
                setProperties(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error("Failed to fetch properties:", err);
                setLoading(false);
            });
    }, []);

    const filteredProperties = useMemo(() => {
        const filtered = properties.filter(p => {
            const locationMatch = p.location?.toLowerCase().includes(locationQuery.toLowerCase());

            // parse only if the user has entered something
            const [minPrice, maxPrice] = priceRange.split("-").map(Number);
            const [minArea, maxArea] = areaRange.split("-").map(Number);

            const priceMatch = !priceRange
                ? true
                : p.price >= minPrice && (isFinite(maxPrice) ? p.price <= maxPrice : true);

            const areaMatch = !areaRange
                ? true
                : p.area >= minArea && (isFinite(maxArea) ? p.area <= maxArea : true);

            return locationMatch && priceMatch && areaMatch;
        });

        const sorted = [...filtered];
        if (sortBy === "price-asc") {
            sorted.sort((a, b) => a.price - b.price);
        } else if (sortBy === "price-desc") {
            sorted.sort((a, b) => b.price - a.price);
        }

        return sorted;
    }, [properties, locationQuery, sortBy, priceRange, areaRange]);

    const getPropertyById = useCallback(
        (id) => properties.find((p) => p.id === id),
        [properties]
    );

    const deletePropertyById = useCallback(
        (id) => {
            setProperties((prev) => prev.filter((p) => p.id !== id));
            setCreatedProperties((prev) => prev.filter((p) => p.id !== id));
        },
        [setProperties, setCreatedProperties]
    );

    // memoize the value object so it only changes when one of its parts changes
    const contextValue = useMemo(() => ({
        loading,
        properties,
        setProperties,
        filteredProperties,
        createdProperties,
        setCreatedProperties,
        locationQuery,
        setLocationQuery,
        sortBy,
        setSortBy,
        priceRange,
        setPriceRange,
        areaRange,
        setAreaRange,
        getPropertyById,
        deletePropertyById
    }), [
        loading,
        properties,
        setProperties,
        filteredProperties,
        createdProperties,
        setCreatedProperties,
        locationQuery,
        sortBy,
        priceRange,
        areaRange,
        // setters from useState (setSearchQuery, etc.) are stable and
        // don’t need to go in the deps array, but including them is harmless
        getPropertyById,
        deletePropertyById
    ]);

    return (
        <PropertyContext.Provider value={contextValue}>
            {children}
        </PropertyContext.Provider>
    );
};

export const useProperties = () => useContext(PropertyContext);