package et.nate.backend.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/unverified")
public class UnverifiedRoleController {

    @GetMapping("/check")
    public String checkUnverifiedRole() {
        return "OK";
    }
}
