package com.dormnet.resourceservice.service;

import com.dormnet.resourceservice.kafka.config.ResourceEventProducer;
import com.dormnet.resourceservice.model.Resource;
import com.dormnet.resourceservice.model.ResourceRequest;
import com.dormnet.resourceservice.repository.ResourceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    private final ResourceEventProducer eventProducer;

    @PersistenceContext
    private EntityManager entityManager;

    public boolean isAvailable(Long id){
        Optional<Resource> optionalResource = resourceRepository.findById(id);
        return optionalResource.isPresent() && optionalResource.get().isAvailable();
    }

    public List<Resource> getAvailableResources() {
        return resourceRepository.findAll()
                .stream()
                .filter(Resource::isAvailable)
                .collect(Collectors.toList());
    }

    @Transactional
    public Resource createResource(ResourceRequest resourceRequest) {
        Resource resource = new Resource();
        resource.setAvailable(resourceRequest.available());
        resource.setName(resourceRequest.name());
        resource.setStatus(resourceRequest.status());
        resourceRepository.save(resource);
        entityManager.refresh(resource);
        return resource;
    }

    @Transactional
    public boolean makeUnavailable(Long id) {
        Optional<Resource> optionalResource = resourceRepository.findById(id);
        if (optionalResource.isPresent()) {
            Resource resource = optionalResource.get();
            resource.setAvailable(false);
            resourceRepository.save(resource);
            eventProducer.sendResourceUnavailableEvent(id);
            return true;
        } else {
            return false;
        }
    }

    public boolean makeAvailable(Long id) {
        Optional<Resource> optionalResource = resourceRepository.findById(id);
        if (optionalResource.isPresent()) {
            Resource resource = optionalResource.get();
            resource.setAvailable(true);
            resourceRepository.save(resource);
            return true;
        } else {
            return false;
        }
    }

    public List<Resource> getResources() {
        return resourceRepository.findAll();
    }

    @Transactional
    public boolean deleteResource(Long id) {
        Optional<Resource> optionalResource = resourceRepository.findById(id);
        if (optionalResource.isPresent()) {
            Resource resource = optionalResource.get();
            if (!resource.isAvailable()) {
                resourceRepository.delete(resource);
                return true;
            }
        }
        return false;
    }
}
