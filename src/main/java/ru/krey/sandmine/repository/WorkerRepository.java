package ru.krey.sandmine.repository;

import org.neo4j.driver.internal.value.DateValue;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import ru.krey.sandmine.domain.Worker;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WorkerRepository extends Neo4jRepository<Worker, Long> {
    @Query(
            "MATCH (worker: WORKER {id: $workerId}-[:HAS_SHIFT]->" +
                    "(shift: SHIFT) where toString(shift.date) > toString($from) AND toString(shift.date) < toString($to) RETURN shift"
    )
    void getShiftsByPeriod(Long workerId, String from, String to);

    @Query("MATCH(worker:WORKER) WHERE worker.phone_number = $login OR worker.email = $login RETURN worker")
    Worker findByLogin(@Param("login") String login);

    Optional<Worker> findByPassId(@Param("passId") Long passId);

    Set<Worker> findAllByRole(String role);

    Set<Worker> findAllByIdIn(List<Long> id);

    @Query(
            "MATCH (worker:WORKER) where worker.role = 'admin-admin' OR worker.role = 'admin' return worker"
    )
    Set<Worker> findAdmins();


    @Query("MATCH(worker: WORKER) RETURN DISTINCT worker.role")
    Set<String> findAllRoles();

    @Query(
            "MATCH(worker: WORKER)-[has_access:HAS_ACCESS_TO]->(zone: ZONE) WHERE toLower(worker.surname) =~ toLower($surname) AND toLower(worker.name) =~ toLower($name) AND toLower(worker.patronymic) =~ toLower($patronymic) AND " +
                    "worker.phone_number =~ $phoneNumber AND toString(worker.role) =~ $roles AND toString(ID(zone)) =~ $zones " +
                    "RETURN worker, has_access, zone"
    )
    List<Worker> getFilteredWorkersList(
            @Param("surname") String surname,
            @Param("name") String name,
            @Param("patronymic") String patronymic,
            @Param("phoneNumber") String phoneNumber,
            @Param("roles") String roles,
            @Param("zones") String zones
    );

    @Query(
            "MATCH(worker: WORKER) WHERE toLower(worker.surname) =~ toLower($surname) AND toLower(worker.name) =~ toLower($name) AND toLower(worker.patronymic) =~ toLower($patronymic) AND " +
                    "worker.phone_number =~ $phoneNumber AND toString(worker.role) =~ $roles " +
                    "RETURN worker"
    )
    List<Worker> getFilteredWorkersListWithoutZones(
            @Param("surname") String surname,
            @Param("name") String name,
            @Param("patronymic") String patronymic,
            @Param("phoneNumber") String phoneNumber,
            @Param("roles") String roles
    );

    Worker getWorkerByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    Worker getWorkerByEmail(@Param("email") String email);

    @Query(
            "MATCH (worker:WORKER)-[has_shift:HAS_SHIFT]->(shift:SHIFT) where ID(shift) =$shiftId RETURN worker"
    )
    Worker getWorkerByShift(@Param("shiftId") Long shiftId);
}
