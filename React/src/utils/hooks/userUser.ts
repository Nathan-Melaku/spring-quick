import {useState} from "react";
import {useLocalStorage} from "@/utils/hooks/useLocalStorage.ts";

export interface JWT {
    accessToken: string | null,
    refreshToken: string | null,
}

export function useUser() {
    const [jwt, setJwt] = useState<JWT>()
    const {setItem} = useLocalStorage();

    const addJwt = (jwt: JWT) => {
        setJwt(jwt);
        setItem("jwt", JSON.stringify(jwt));
    };

    const removeJwt = () => {
        setJwt(undefined);
        console.log("we are here")
        setItem("jwt", "");
    };

    return {jwt, addJwt, removeJwt, setJwt};
}
