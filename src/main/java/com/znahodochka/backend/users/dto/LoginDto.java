package com.znahodochka.backend.users.dto;

import lombok.Builder;

@Builder
public record LoginDto (String login, String password){}
