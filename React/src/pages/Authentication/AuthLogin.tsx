import {useContext, useEffect, useState} from "react";
import {AuthContext} from "@/utils/context/auth-context-type.ts";
import {Navigate, useLocation} from "react-router-dom";
import {handle} from "@/utils/social-login.ts";

export function AuthLogin() {
    const {setJwt} = useContext(AuthContext);
    const [loginSuccess, setLoginSuccess] = useState(false);
    const location = useLocation();

    useEffect(() => {
        const accessToken = new URLSearchParams(location.search).get("accessToken");
        const refreshToken = new URLSearchParams(location.search).get("refreshToken");

        console.log(location.search)
        if (accessToken) {
            handle(accessToken);
            setJwt({
                accessToken,
                refreshToken,
            });
            console.log({
                access_token: accessToken,
                refreshToken: refreshToken,
            })
            setLoginSuccess(true)
        }
    }, [])

    return (
        <>
            {
                loginSuccess ?
                    <p>login Failed</p> :
                    <Navigate to={'/'}/>
            }
        </>
    )
}
