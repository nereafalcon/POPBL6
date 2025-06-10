
import {useProperties} from "../../context/PropertyContext.jsx";
import {ChevronDown, Euro, Expand, MapPin, Search} from "lucide-react";

const PRICE_RANGES = [
    { label: "Edozein prezio", value: "" },
    { label: "800 €-tik behera", value: "0-800" },
    { label: "€800 – €1200", value: "800-1200" },
    { label: "€1200 – €1800", value: "1200-1800" },
    { label: "1800 €-tik gora", value: "1800-Infinity" },
];

const AREA_RANGES = [
    { label: "Edozein tamaina", value: "" },
    { label: "50 m²-tik behera", value: "0-50" },
    { label: "50 – 80 m²", value: "50-80" },
    { label: "80 – 120 m²", value: "80-120" },
    { label: "120 m²-tik gora", value: "120-Infinity" },
];

const SearchBar = () => {
    const { locationQuery, setLocationQuery, priceRange, setPriceRange, areaRange, setAreaRange } = useProperties();

    return (
        <div className="bg-white py-6 search-bar-shadow -mt-13 rounded-lg container mx-auto px-4 sm:px-6 lg:px-8 z-10 relative">
            <div className="flex flex-col md:flex-row items-center space-y-4 md:space-y-0 md:space-x-4">
                <div className="relative flex-grow w-full md:w-auto">
                    <MapPin className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 icon-sm" />
                    <input
                        type="text"
                        value={locationQuery}
                        onChange={(e) => setLocationQuery(e.target.value)}
                        placeholder="Kokapena (e.g., 'Bilbo', 'Donosti')"
                        className="pl-10 pr-4 py-3 w-full border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                    />
                </div>
                <div className="relative flex-grow w-full md:w-auto">
                    <Euro className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 icon-sm" />
                    <select
                        value={priceRange}
                        onChange={(e) => setPriceRange(e.target.value)}
                        className="appearance-none w-full pl-10 pr-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-700 focus:ring-indigo-500 focus:border-indigo-500"
                    >
                        {PRICE_RANGES.map((range) => (
                            <option key={range.value} value={range.value}>
                                {range.label}
                            </option>
                        ))}
                    </select>
                    <ChevronDown className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-400 pointer-events-none" />
                </div>
                <div className="relative flex-grow w-full md:w-auto">
                    <Expand className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 icon-sm" />
                    <select
                        value={areaRange}
                        onChange={(e) => setAreaRange(e.target.value)}
                        className="appearance-none w-full pl-10 pr-4 py-3 bg-white border border-gray-300 rounded-lg text-gray-700 focus:ring-indigo-500 focus:border-indigo-500"
                    >
                        {AREA_RANGES.map((range) => (
                            <option key={range.value} value={range.value}>
                                {range.label}
                            </option>
                        ))}
                    </select>
                    <ChevronDown className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-400 pointer-events-none" />
                </div>
            </div>
        </div>
    );
};

export default SearchBar;
