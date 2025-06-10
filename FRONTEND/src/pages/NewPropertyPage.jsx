import { useState } from "react";
import { useAuth } from "../context/AuthProvider";
import { Link } from "react-router-dom";
import { nanoid } from "nanoid";
import { useProperties } from "../context/PropertyContext.jsx";
import { X } from "lucide-react";

const formFields = [
    { name: "title", label: "Izenburua", type: "text" },
    { name: "description", label: "Deskripzioa", type: "text" },
    { name: "location", label: "Kokapena", type: "text" },
    { name: "price", label: "Prezioa (€)", type: "number" },
    { name: "area", label: "Tamainia (m²)", type: "number" },
    { name: "rooms", label: "Logelak", type: "number" },
    { name: "bathrooms", label: "Bainugelak", type: "number" },
];

const DEFAULT_DESCRIPTION = "IIkasleentzat aproposa, Deustuko Unibertsitatetik pauso batera kokatua. Apartamentu honek kokapen pribilegiatua du, ikasketa eta eguneroko erosotasuna uztartzen dituena. Erabat hornitua dago, bizitzeko prest, eta ekipamendu modernoak ditu: sukalde osatua, altzari erosoak eta biltegiratze espazio ugari. Eguzkiaren argia etxebizitza osoan zehar sartzen da, eta balkoi zabal eta eguzkitsu batek aire librean atseden hartzeko aukera paregabea eskaintzen du. Inguruan, zerbitzu guztiak eskura daude: supermerkatuak, garraio publikoa, gimnasioa eta aisialdirako guneak. Bizitzeko toki atsegina eta funtzionala, ikasleentzako pentsatua.";

const DEFAULT_NEARBY_PLACES = [
    {
        "icon": "🚇",
        "name": "Indautxu Metro Geltokia",
        "distance": "300m",
        "walking_time": "4 min oinez"
    },
    {
        "icon": "🏬",
        "name": "El Corte Ingles Gran Via",
        "distance": "700m",
        "walking_time": "9 min oinez"
    },
    {
        "icon": "🏟️",
        "name": "San Mames Estadioa",
        "distance": "1,2km",
        "walking_time": "17 min oinez"
    },
    {
        "icon": "🎓",
        "name": "Deustuko Unibertsitatea",
        "distance": "1,6km",
        "walking_time": "5 min oinez"
    }
];

const EMPTY_FORM = {
    title: "",
    description: "",
    location: "",
    price: "",
    area: "",
    rooms: "",
    bathrooms: "",
    gallery: [],
};

const NewPropertyPage = () => {
    const { user } = useAuth();
    const { setProperties, setCreatedProperties } = useProperties();

    const [form, setForm] = useState(EMPTY_FORM);
    const [success, setSuccess] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        const newProperty = {
            id: nanoid(8),
            price: Number(form.price),
            area: Number(form.area),
            rooms: Number(form.rooms),
            bathrooms: Number(form.bathrooms),
            owner: {
                name: user.name,
                avatar: "https://i.pravatar.cc/44"
            },
            hasGarage: true,
            description: DEFAULT_DESCRIPTION,
            nearby: DEFAULT_NEARBY_PLACES,
            gallery: [
                "/images/properties/aK3tV9Fb/1.webp",
                "/images/properties/aK3tV9Fb/2.webp",
                "/images/properties/aK3tV9Fb/3.webp",
                "/images/properties/aK3tV9Fb/4.webp",
                "/images/properties/aK3tV9Fb/5.webp"
            ],
            bookmarked: false,
        };

        setCreatedProperties((prevCreatedProperties) => [...prevCreatedProperties, newProperty]);
        setProperties((prevProperties) => [...prevProperties, newProperty]);

        setSuccess(true);
        setForm(EMPTY_FORM);

    };

    return (
        <div className="w-2xl mx-auto p-6">
            <h1 className="text-2xl font-semibold mb-6">
                Iragarki Berria Sortu
            </h1>
            {success && (
                <div className="flex mb-4 p-3 bg-green-100 text-green-800 rounded">
                    Iragarki berria arazo gabe sortu da! <Link to="/nire-iragarkiak" className="cursor-pointer ml-auto text-indigo-600 hover:underline">Ikusi</Link>
                    <button
                        type="button"
                        onClick={() => setSuccess(false)}
                        className="cursor-pointer ml-4 text-gray-500 hover:text-gray-700"
                    >
                        <X className="w-5 h-5" />
                    </button>
                </div>
            )}
            <form onSubmit={handleSubmit} className="space-y-4">
                {formFields.map(({name, label, type}) => (
                    <div key={name}>
                        <label htmlFor={name} className="block mb-1 capitalize">{label}</label>
                        <input
                            id={name}
                            name={name}
                            type={type}
                            value={form[name]}
                            onChange={handleChange}
                            required
                            className="w-full border px-3 py-2 rounded"
                        />
                    </div>
                ))}

                <button
                    type="submit"
                    className="bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700"
                >
                    Sortu Iragarkia
                </button>
            </form>
        </div>
    );
};

export default NewPropertyPage;