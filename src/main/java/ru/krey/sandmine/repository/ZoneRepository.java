package ru.krey.sandmine.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import ru.krey.sandmine.domain.Zone;

public interface ZoneRepository extends Neo4jRepository<Zone,Long> {
}
