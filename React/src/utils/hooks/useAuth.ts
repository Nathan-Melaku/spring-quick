import {JWT, useUser} from "@/utils/hooks/userUser.ts";
import {useLocalStorage} from "@/utils/hooks/useLocalStorage.ts";
import {useEffect} from "react";

export function useAuth() {
    const { jwt, addJwt, removeJwt, setJwt } = useUser();
    const { getItem } = useLocalStorage();

    useEffect(() => {
        const user = getItem("user");
        if (user) {
            addJwt(JSON.parse(user))
        }
    }, [addJwt, getItem]);

    const login = (jwt: JWT)  => {
        addJwt(jwt);
    };

    const logout = () => {
        removeJwt();
    };

    return { jwt, login, logout, setJwt }
}
