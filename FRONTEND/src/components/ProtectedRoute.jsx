import {Navigate} from "react-router-dom";
import {useAuth} from "../context/AuthProvider.jsx";

const ProtectedRoute = ({ element, redirectTo = "/login" }) => {
    const { user } = useAuth();

    return user
        ? element
        : <Navigate to ={redirectTo} />
};

export default ProtectedRoute;