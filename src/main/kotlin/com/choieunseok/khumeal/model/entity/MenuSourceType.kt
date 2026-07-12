package com.choieunseok.khumeal.model.entity

enum class MenuSourceType {
    IMAGE_GRPC, // 게시판 이미지 → gRPC menu-server OCR (기존 방식)
    DORM_JSON,  // dorm2.khu.ac.kr getWeeklyMenu.do JSON
    HUFS_HTML   // hufs.ac.kr cafeteria getMenu.do HTML 테이블
}
