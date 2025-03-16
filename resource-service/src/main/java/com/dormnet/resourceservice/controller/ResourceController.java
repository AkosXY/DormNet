package com.dormnet.resourceservice.controller;

import com.dormnet.resourceservice.model.Resource;
import com.dormnet.resourceservice.model.ResourceRequest;
import com.dormnet.resourceservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/available")
    @ResponseStatus(HttpStatus.OK)
    public boolean isAvailable (@RequestParam Long id) {
        return resourceService.isAvailable(id);
    }


    @PostMapping("/makeUnavailable")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.OK)
    public boolean makeUnavailable (@RequestParam Long id) {
        return resourceService.makeUnavailable(id);
    }

    @PostMapping("/makeAvailable")
    @PreAuthorize("hasRole('admin')")
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

}
