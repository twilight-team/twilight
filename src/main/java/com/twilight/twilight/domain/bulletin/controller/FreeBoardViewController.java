package com.twilight.twilight.domain.bulletin.controller;

import com.twilight.twilight.domain.bulletin.service.FreeBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bulletin/free-board")   // 클래스 레벨 경로
@RequiredArgsConstructor
@Slf4j
public class FreeBoardViewController {

    private final FreeBoardService freeBoardService;




}
