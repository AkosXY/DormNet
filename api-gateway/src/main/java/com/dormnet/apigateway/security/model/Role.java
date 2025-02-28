package com.dormnet.apigateway.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Role {

    DEFAULT(List.of(Permission.READ)),

    ADMIN(Arrays.asList(Permission.READ, Permission.WRITE));

    private List<Permission> permissions;




}
