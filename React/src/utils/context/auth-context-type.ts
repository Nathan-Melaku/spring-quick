import {createContext} from "react";
import {JWT} from "@/utils/hooks/userUser.ts";

export interface AuthContextType {
    jwt: JWT | undefined;
    login: () => void;
    logout: () => void;
    setJwt: (jwt: JWT | undefined) => void
}

export const AuthContext = createContext<AuthContextType>({
    jwt: undefined,
    login: () => {},
    logout: () => {},
    setJwt: () => {},
})
