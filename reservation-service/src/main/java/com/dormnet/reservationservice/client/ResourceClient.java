package com.dormnet.reservationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "resource", url = "${resource.url}")
public interface ResourceClient {

    @RequestMapping(method = RequestMethod.GET, value = "api/resource/available")
    boolean isAvailable(@RequestParam("id") Long resourceId);


    @RequestMapping(method = RequestMethod.POST, value = "api/resource/makeUnavailable")
    boolean makeUnavailable(@RequestParam("id") Long resourceId);

    @RequestMapping(method = RequestMethod.POST, value = "api/resource/makeAvailable")
    void makeAvailable(@RequestParam("id") Long resourceId);
}
