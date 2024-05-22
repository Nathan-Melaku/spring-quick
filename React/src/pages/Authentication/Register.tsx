import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Link} from "react-router-dom";
import {onSocialLogin, SocialLogin} from "@/utils/social-login.ts";

const registerSchema = z.object({
    email: z.string().email("Please use a valid email"),
    firstName: z.string().min(2).max(30),
    lastName: z.string().min(2).max(30),
    password: z.string().min(8).max(40),
    repeatPassword: z.string().min(8).max(40),
})

export function Register() {
    const form = useForm<z.infer<typeof registerSchema>>({
        resolver: zodResolver(registerSchema),
        defaultValues: {
            email: "",
            firstName: "",
            lastName: "",
            password: "",
            repeatPassword: "",
        }
    })

    const onRegister = () => {
        console.log("register")
    }

    return (

        <div className="flex">
            <div className="m-auto mt-20 w-96">
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onRegister)}
                          className="space-y-4 p-4 border rounded-md bg-gray-50">
                        <FormField
                            control={form.control}
                            name="firstName"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>First Name</FormLabel>
                                    <FormControl>
                                        <Input placeholder="first name" required={true} {...field} />
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="lastName"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>Last Name</FormLabel>
                                    <FormControl>
                                        <Input placeholder="last name" required={true} {...field} />
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
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
                        <FormField
                            control={form.control}
                            name="repeatPassword"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>Repeat Password</FormLabel>
                                    <FormControl>
                                        <Input placeholder="repeat password" {...field} type="password"/>
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
                        <Button type="submit" className="w-full">register</Button>
                        <Button variant="link" className="w-full mt-2">
                            <Link to="/login">already have an account</Link>
                        </Button>
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
