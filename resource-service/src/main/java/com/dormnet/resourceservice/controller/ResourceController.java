package com.dormnet.resourceservice.controller;

import com.dormnet.resourceservice.model.Resource;
import com.dormnet.resourceservice.model.ResourceRequest;
import com.dormnet.resourceservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/available")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public boolean isAvailable (@RequestParam Long id) {
        return resourceService.isAvailable(id);
    }

    @GetMapping("/availableResources")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public List<Resource> getAvailableResources() {
        return resourceService.getAvailableResources();
    }

    @GetMapping("/getResources")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public List<Resource> getResources() {
        return resourceService.getResources();
    }

    @PostMapping("/makeUnavailable")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public boolean makeUnavailable (@RequestParam Long id) {
        return resourceService.makeUnavailable(id);
    }

    @PostMapping("/makeAvailable")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public boolean makeAvailable (@RequestParam Long id) {
        return resourceService.makeAvailable(id);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public Resource createResource (@RequestBody ResourceRequest resourceRequest) {
        return resourceService.createResource(resourceRequest);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteResource(@RequestParam Long id) {
        return resourceService.deleteResource(id);
    }
}
