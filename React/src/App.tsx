import './App.css'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import ErrorPage from "@/ErrorPage.tsx";
import {AuthContext} from "@/utils/context/auth-context-type.ts";
import {useAuth} from "@/utils/hooks/useAuth.ts";
import {HomePage} from "@/pages/HomePage.tsx";
import {Login} from "@/pages/Authentication/Login.tsx";
import {Register} from "@/pages/Authentication/Register.tsx"
import {AuthLogin} from "@/pages/Authentication/AuthLogin.tsx";

const router = createBrowserRouter([
    {
        path: '/',
        element: <HomePage/>,
        errorElement: <ErrorPage/>,
    },
    {
        path: '/login',
        element: <Login/>,
        errorElement: <ErrorPage/>,
    },
    {
        path: '/register',
        element: <Register/>,
        errorElement: <ErrorPage/>
    },
    {
        path: '/auth/login',
        element: <AuthLogin/>,
        errorElement: <ErrorPage/>,
    },
]);

function App() {

    const {jwt,login, logout, setJwt} = useAuth();

    return (
        <AuthContext.Provider value={{jwt, login, logout, setJwt}}>
            <div>
                <RouterProvider router={router}/>
            </div>
        </AuthContext.Provider>
    );
}

export default App
