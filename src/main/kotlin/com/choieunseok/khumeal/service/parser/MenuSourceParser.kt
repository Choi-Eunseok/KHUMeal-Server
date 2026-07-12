package com.choieunseok.khumeal.service.parser

import com.choieunseok.khumeal.model.entity.MenuSnapshotEntity
import com.choieunseok.khumeal.model.entity.RestaurantEntity
import com.choieunseok.khumeal.model.entity.MenuSourceType

/**
 * 식당 메뉴 소스별 파서.
 * 새 소스(다른 학교 홈페이지 등)를 추가할 때는 이 인터페이스 구현체 하나만 추가하면 된다.
 * MenuSyncService가 RestaurantEntity.primarySource로 구현체를 선택한다.
 */
interface MenuSourceParser {

    val sourceType: MenuSourceType

    /**
     * 최신 메뉴를 가져와 정규화한다.
     *
     * @param restaurant   대상 식당 (sourceUrl/sourceConfig에 요청 정보)
     * @param lastSnapshot 이 소스로 마지막에 저장한 스냅샷 (변경 감지 기준). 없으면 null
     * @return 새 메뉴. **이미 최신 상태(변경 없음)면 null** — 기존 DB 데이터가 유지된다.
     * @throws Exception 소스 접근/파싱 실패 시. MenuSyncService가 잡아서 fallback을 시도한다.
     */
    fun fetchRecent(restaurant: RestaurantEntity, lastSnapshot: MenuSnapshotEntity?): ParsedWeeklyMenu?
}
