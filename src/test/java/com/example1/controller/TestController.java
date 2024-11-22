package com.example1.controller;

import annotations.injection.semantic.Controller;

@Controller("/api/test")
public class TestController {


    public String test() {
        return "test";
    }
}
