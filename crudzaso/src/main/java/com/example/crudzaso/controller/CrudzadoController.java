package com.example.crudzaso.controller;

import com.example.crudzaso.dto.DatabaseCreateRequest;
import com.example.crudzaso.dto.DatabaseUpdateRequest;
import com.example.crudzaso.dto.ProfileUpdateRequest;
import com.example.crudzaso.dto.PasswordChangeRequest;
import com.example.crudzaso.dto.RegisterRequest;
import com.example.crudzaso.entity.Database;
import com.example.crudzaso.entity.Users;
import com.example.crudzaso.service.DataBaseService;
import com.example.crudzaso.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CrudzadoController {

    private final UserService userService;
    private final DataBaseService dataBaseService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Users user = userService.findByEmail(userDetails.getUsername());
        List<Database> databases = dataBaseService.getDatabasesByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("databases", databases);
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Users user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("profileRequest", new ProfileUpdateRequest(user.getName(), user.getLastName()));
        model.addAttribute("passwordRequest", new PasswordChangeRequest("", ""));
        return "profile";
    }

    @GetMapping("/databases/{id}")
    public String databaseDetail(@PathVariable Long id, Model model) {
        Database db = dataBaseService.getDatabaseById(id);
        model.addAttribute("db", db);
        return "database/detail";
    }

    @PostMapping("/databases/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam String action) {
        dataBaseService.changeStatus(id, action);
        return "redirect:/databases/" + id;
    }

    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest("", "", "", ""));
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String register(@Valid @ModelAttribute RegisterRequest req, Model model) {
        try {
            Users user = new Users();
            user.setName(req.name());
            user.setLastName(req.lastName());
            user.setEmail(req.email());
            user.setPassword(req.password());
            userService.registerUser(user);
            return "redirect:/auth/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/auth/forgot-password")
    public String forgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/auth/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        try {
            userService.requestPasswordReset(email);
        } catch (RuntimeException ignored) {}
        model.addAttribute("message", "If the email exists, you will receive a reset link");
        return "auth/forgot-password";
    }

    @GetMapping("/databases/create")
    public String createDatabaseForm(Model model) {
        model.addAttribute("dbRequest", new DatabaseCreateRequest("", "", null, null));
        return "database/create";
    }

    @PostMapping("/databases/create")
    public String createDatabase(@Valid @ModelAttribute("dbRequest") DatabaseCreateRequest req,
                                 @AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            Users users = userService.findByEmail(userDetails.getUsername());
            Database database = new Database();
            database.setName(req.name());
            database.setDescription(req.description());
            database.setEngine(req.engine());
            database.setPort(req.port());
            dataBaseService.createDatabase(database, users);
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "database/create";
        }
    }

    @GetMapping("/databases/{id}/edit")
    public String editDatabaseForm(@PathVariable Long id, Model model) {
        Database db = dataBaseService.getDatabaseById(id);
        DatabaseUpdateRequest req = new DatabaseUpdateRequest(db.getName(), db.getDescription());
        model.addAttribute("dbRequest", req);
        model.addAttribute("databaseId", id);
        return "database/edit";
    }

    @PostMapping("/databases/{id}/edit")
    public String updateDatabase(@PathVariable Long id, @Valid @ModelAttribute("dbRequest") DatabaseUpdateRequest req,
                                 @AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            Users users = userService.findByEmail(userDetails.getUsername());
            Database database = new Database();
            database.setName(req.name());
            database.setDescription(req.description());
            dataBaseService.updateDatabase(id, database, users);
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "database/edit";
        }
    }

    @PostMapping("/databases/{id}/delete")
    public String deleteDatabase(@PathVariable Long id) {
        dataBaseService.deleteDatabase(id);
        return "redirect:/dashboard";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute ProfileUpdateRequest req,
                                @AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            Users users = userService.findByEmail(userDetails.getUsername());
            userService.updateProfile(users.getId(), req);
            return "redirect:/profile?updated=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "profile";
        }
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute PasswordChangeRequest req,
                                 @AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            Users users = userService.findByEmail(userDetails.getUsername());
            userService.changePassword(users.getId(), req.oldPassword(), req.newPassword());
            return "redirect:/profile?passwordChanged=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "profile";
        }
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        List<Database> allDatabases = dataBaseService.getAllDatabases();
        Map<String, Long> databasesByEngine = allDatabases.stream()
                .collect(Collectors.groupingBy(d -> d.getEngine().name(), Collectors.counting()));

        model.addAttribute("totalUsers", userService.countTotalUsers());
        model.addAttribute("activeUsers", userService.countActiveUsers());
        model.addAttribute("blockedUsers", userService.countBlockedUsers());
        model.addAttribute("totalDatabases", allDatabases.size());
        model.addAttribute("databasesByEngine", databasesByEngine.entrySet());
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/admin/users/{id}/block")
    public String blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/unblock")
    public String unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/databases")
    public String adminDatabases(Model model) {
        model.addAttribute("databases", dataBaseService.getAllDatabases());
        return "admin/databases";
    }

    @GetMapping("/admin/stats")
    public String adminStats(Model model) {
        List<Database> allDatabases = dataBaseService.getAllDatabases();
        Map<String, Long> databasesByEngine = allDatabases.stream()
                .collect(Collectors.groupingBy(d -> d.getEngine().name(), Collectors.counting()));
        Map<String, Long> databasesByStatus = allDatabases.stream()
                .collect(Collectors.groupingBy(d -> d.getStatus().name(), Collectors.counting()));

        model.addAttribute("databasesByEngine", databasesByEngine.entrySet());
        model.addAttribute("databasesByStatus", databasesByStatus.entrySet());
        return "admin/stats";
    }
}
