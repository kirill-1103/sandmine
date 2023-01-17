package ru.krey.sandmine.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import ru.krey.sandmine.domain.Shift;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ShiftRepository extends Neo4jRepository<Shift, Long> {
    @Query(
            "MATCH (shift: SHIFT)-[in:IN]->(zone:ZONE) WHERE toString(shift.date) >= toString($dateFrom) AND toString(shift.date) <= toString($dateTo) AND toLower(toString(shift.attended)) =~ $attended AND toString(ID(zone)) =~ $zoneIds " +
                    "WITH shift, in, zone MATCH (worker: WORKER)-[has_shift:HAS_SHIFT]->(shift) WHERE ID(worker) = $workerId " +
                    "RETURN worker, has_shift, shift, in, zone " +
                    "ORDER BY shift.date"
    )
    Set<Shift> getFilteredShiftList(
            @Param("workerId") Long workerId,
            @Param("dateFrom") String dateFrom,
            @Param("dateTo") String dateTo,
            @Param("attended") String attended,
            @Param("zoneIds") String zoneIds
    );

    @Query(
            "MATCH(worker: WORKER)-[has_shift: HAS_SHIFT]->(shift: SHIFT)-[in:IN]->(zone:ZONE) WHERE toLower(worker.name) =~ toLower($name) AND toLower(worker.surname) =~ toLower($surname) " +
                    "AND toLower(worker.patronymic) =~ toLower($patronymic) AND worker.phone_number =~ $phone AND worker.role =~ $role " +
                    "AND toString(shift.date) >= toString($dateFrom) AND toString(shift.date) <= toString($dateTo) AND toString(ID(zone)) =~ $zoneIds RETURN  worker, has_shift, shift, in, zone "
    )
    List<Shift> allShiftsFilter(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("patronymic") String patronymic,
            @Param("phone") String phone,
            @Param("role") String role,
            @Param("dateFrom") String dateFrom,
            @Param("dateTo") String dateTo,
            @Param("zoneIds") String zoneIds
    );

    @Query("MATCH (worker:WORKER)-[has_shift:HAS_SHIFT]->(shift:SHIFT)-[in:IN]->(zone:ZONE) where ID(worker)=$workerId return shift,has_shift,worker,in,zone ORDER BY shift.date")
    Set<Shift> getAllShiftsByWorker(Long workerId);
}
