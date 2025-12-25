package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.grpc.*
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service

@Service
class MenuGrpcClient(
    @GrpcClient("menu-server")
    private val menuStub: MenuServiceGrpc.MenuServiceBlockingStub
) {
    fun findRecentMenu(baseUrl: String, boardId: String, keyword: String): FindRecentMenuResponse {
        val request = FindRecentMenuRequest.newBuilder()
            .setBaseUrl(baseUrl)
            .setBoardId(boardId)
            .setKeyword(keyword)
            .build()
        return menuStub.findRecentMenu(request)
    }

    fun parseMenu(imageUrl: String): ParseMenuResponse {
        val request = ParseMenuRequest.newBuilder()
            .setImageUrl(imageUrl)
            .build()
        return menuStub.parseMenu(request)
    }
}