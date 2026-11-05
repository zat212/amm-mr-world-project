package org.myassignment.controller;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.NameRequest;
import org.myassignment.dto.ThemeResponse;
import org.myassignment.dto.TierResponse;
import org.myassignment.dto.UserResponse;
import org.myassignment.service.ThemeService;
import org.myassignment.service.TierService;
import org.myassignment.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffAdminController {

    private final ThemeService themeService;
    private final TierService tierService;
    private final UserService userService;
    private final ResponseMapper mapper;

    @PostMapping("/themes")
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponse createTheme(@RequestBody NameRequest request) {
        return mapper.theme(themeService.create(request.name()));
    }

    @PutMapping("/themes/{id}")
    public ThemeResponse renameTheme(@PathVariable Long id, @RequestBody NameRequest request) {
        return mapper.theme(themeService.rename(id, request.name()));
    }

    @DeleteMapping("/themes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheme(@PathVariable Long id) {
        themeService.delete(id);
    }

    @PostMapping("/tiers")
    @ResponseStatus(HttpStatus.CREATED)
    public TierResponse createTier(@RequestBody NameRequest request) {
        return mapper.tier(tierService.create(request.name()));
    }

    @PutMapping("/tiers/{id}")
    public TierResponse renameTier(@PathVariable Long id, @RequestBody NameRequest request) {
        return mapper.tier(tierService.rename(id, request.name()));
    }

    @DeleteMapping("/tiers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTier(@PathVariable Long id) {
        tierService.delete(id);
    }


    @GetMapping("/users")
    public List<UserResponse> users() {
        return userService.getAll().stream().map(mapper::user).toList();
    }
}

