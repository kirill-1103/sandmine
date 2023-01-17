package ru.krey.sandmine.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import ru.krey.sandmine.domain.MineStats;

import java.time.LocalDate;
import java.util.List;

public interface MineStatsRepository extends Neo4jRepository<MineStats, Long> {

//    void findAllByIdIn(List<Long> id);

    @Query(
            "MATCH (mine_stats:MINE_STATS)-[in_zone:IN_ZONE]->(zone:ZONE) " +
                    "WITH mine_stats, in_zone, zone MATCH (worker:WORKER) <- [last_edited:LAST_EDITED] -(mine_stats) " +
                    "RETURN mine_stats,in_zone,zone,worker,last_edited " +
                    "ORDER BY mine_stats.date DESC,mine_stats.last_edit_time DESC"
    )
    List<MineStats> findAllSortedByDate();

    @Query(
            "MATCH (mine_stats:MINE_STATS)-[in_zone:IN_ZONE]->(zone:ZONE) WHERE toString(mine_stats.date) >= toString($dateFrom) AND toString(mine_stats.date) <= toString($dateTo) " +
                    "AND ( mine_stats.last_edit_time.epochSeconds >= $timeEditStart AND mine_stats.last_edit_time.epochSeconds <= $timeEditEnd) AND mine_stats.weight >= $weightFrom AND mine_stats.weight <= $weightTo " +
                    "AND (ID(zone) IN $zoneIds OR size($zoneIds) = 0)" +
                    "WITH mine_stats, in_zone, zone MATCH (worker:WORKER) <- [last_edited:LAST_EDITED] - (mine_stats) WHERE (ID(worker) IN $lastEditorIds OR size($lastEditorIds)=0)" +
                    "RETURN mine_stats,in_zone,zone,worker,last_edited " +
                    "ORDER BY mine_stats.date DESC,mine_stats.last_edit_time DESC"
    )
    List<MineStats> getFilteredMineStats(
            @Param("timeEditStart")Long timeEditStart,
            @Param("timeEditEnd")Long timeEditEnd,
            @Param("dateFrom") String dateFrom,
            @Param("dateTo")String dateTo,
            @Param("lastEditorIds")List<Long> lastEditorIds,
            @Param("weightFrom")Double weightFrom,
            @Param("weightTo")Double weightTo,
            @Param("zoneIds")List<Long> zoneIds
    );

}
