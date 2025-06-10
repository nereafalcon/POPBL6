import { useState} from "react";
import { useAuth } from "../context/AuthProvider.jsx";
import { NavLink } from "react-router-dom";

const RegisterPage = () => {
    const { setUsers, login } = useAuth();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    const handleRegister = (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            alert("Pasahitzak ez dira berdinak");
            return;
        }

        const newUser = { username, password };

        setUsers(prevUsers => [...prevUsers, newUser]);

        login(newUser)

    };

    return (
        <div className="max-w-md w-full space-y-8 mx-auto my-auto">
            <h1 className="text-3xl font-semibold text-center">
                Kontu Berria Sortu
            </h1>

            <form onSubmit={handleRegister} className="space-y-5">
                <div>
                    <label htmlFor="username" className="block text-sm font-medium mb-1">
                        Erabiltzaile Izena
                    </label>
                    <input
                        id="username"
                        type="username"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md outline-none focus:ring-2 focus:ring-indigo-500"
                        placeholder="Sartu zure erabiltzaile izena"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="password" className="block text-sm font-medium mb-1">
                        Pasahitza
                    </label>
                    <input
                        id="password"
                        type="password"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md outline-none focus:ring-2 focus:ring-indigo-500"
                        placeholder="Sartu zure pasahitza"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="confirmPassword" className="block text-sm font-medium mb-1">
                        Pasahitza
                    </label>
                    <input
                        id="confirmPassword"
                        type="password"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md outline-none focus:ring-2 focus:ring-indigo-500"
                        placeholder="Berretsi zure pasahitza"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                </div>

                <button
                    type="submit"
                    className="w-full bg-indigo-600 text-white py-2 rounded-md hover:bg-indigo-700 transition"
                >
                    Erregistratu
                </button>
            </form>

            <div className="flex justify-between text-sm mt-4">
                <NavLink to="/login" className="text-gray-600 underline">
                    Kontu bat duzu? Hasi saioa
                </NavLink>
            </div>
        </div>
    )
}

export default RegisterPage;
