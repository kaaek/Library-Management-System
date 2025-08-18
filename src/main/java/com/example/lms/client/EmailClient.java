package com.example.lms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.lms.dto.email.EmailRequest;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "email-service", url = "http://localhost:8081") // MS2 runs on 8081 for example
public interface EmailClient {

    @PostMapping("/send-email")
    void sendEmail(@RequestBody EmailRequest emailRequest);

}

