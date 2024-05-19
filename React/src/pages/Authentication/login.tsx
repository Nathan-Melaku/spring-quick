import {useForm} from "react-hook-form";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import { SocialLogin, getProviderRedirect } from "@/utils/social-login.ts";

const loginSchema = z.object({
    email: z.string().email("Please use a valid email"),
    password: z.string().min(8).max(40),
})

const Login = () => {
    const form = useForm<z.infer<typeof loginSchema>>({
        resolver: zodResolver(loginSchema),
        defaultValues: {
            email: "",
            password: "",
        }
    })

    function onLogin(values: z.infer<typeof loginSchema>) {
        //form handle
        console.log(values)
    }

    function onSignUp() {
        console.log("sign up");
    }

    function onSocialLogin(loginProvider: SocialLogin) {
        window.location.href = getProviderRedirect(loginProvider);
    }

    return (
        <div className="flex">
            <div className="m-auto mt-20 w-96">
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onLogin)} className="space-y-4 p-4 border rounded-md bg-gray-50">
                        <FormField
                            control={form.control}
                            name="email"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>Email</FormLabel>
                                    <FormControl>
                                        <Input placeholder="email" required={true} {...field} />
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="password"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>Password</FormLabel>
                                    <FormControl>
                                        <Input placeholder="password" {...field} type="password"/>
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
                        <Button type="submit" className="w-full">Login</Button>
                        <Button variant="link" className="w-full mt-2" onClick={onSignUp}>Create Account</Button>
                    </form>
                </Form>

                <div className="space-y-2 mt-5 p-4 border rounded-md bg-gray-50">
                    <Button className="w-full " variant="outline" onClick={() => {
                        onSocialLogin(SocialLogin.GITHUB)
                    }}>Continue with Github</Button>
                    <Button className="w-full " variant="outline" onClick={() => {
                        onSocialLogin(SocialLogin.GOOGLE)
                    }}>Continue with Google</Button>
                </div>
            </div>
        </div>
    )
}

export default Login
