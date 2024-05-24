package et.nate.backend.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/users")
    public String getUsers() {
        return "Success";
    }

    @PostMapping("/users")
    public String postUser() {
        return "Success";
    }
}
