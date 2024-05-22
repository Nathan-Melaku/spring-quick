import {useContext, useEffect} from "react";
import {AuthContext} from "@/utils/context/auth-context-type.ts";
import {
    NavigationMenu,
    NavigationMenuItem,
    NavigationMenuLink,
    NavigationMenuList,
    navigationMenuTriggerStyle
} from "@/components/ui/navigation-menu.tsx";
import {Link} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";

export function HomePage() {
    const {jwt, logout} = useContext(AuthContext)

    const onLogout = () => {
        // TODO show dialog
        logout();
    }

    useEffect(() => {
        console.log(jwt);
    }, [jwt])

    return (
        <NavigationMenu className="shadow py-5">
            <div>Logo</div>
            <NavigationMenuList className="flex flex-1 items-center">
                <NavigationMenuItem>
                    <NavigationMenuLink className={navigationMenuTriggerStyle()}>
                        About Us
                    </NavigationMenuLink>
                </NavigationMenuItem>
                <NavigationMenuItem>
                    <NavigationMenuLink className={navigationMenuTriggerStyle()}>
                        Pricing
                    </NavigationMenuLink>
                </NavigationMenuItem>
                <NavigationMenuItem>
                    <NavigationMenuLink className={navigationMenuTriggerStyle()}>
                        How It Works
                    </NavigationMenuLink>
                </NavigationMenuItem>
                <NavigationMenuItem>
                    <NavigationMenuLink className={navigationMenuTriggerStyle()}>
                        Blog
                    </NavigationMenuLink>
                </NavigationMenuItem>
            </NavigationMenuList>

            {jwt ?
                <Button variant="ghost" onClick={onLogout}>Logout</Button>
                :
                <Link to="/login">
                    <Button>Get Started</Button>
                </Link>
            }
        </NavigationMenu>
    )
}
