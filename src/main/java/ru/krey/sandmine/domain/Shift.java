package ru.krey.sandmine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.internal.value.DateValue;
import org.springframework.data.neo4j.core.schema.*;

@Node("SHIFT")
@RequiredArgsConstructor
@Getter
@AllArgsConstructor
public class Shift {
    @Id
    @GeneratedValue
    private Long id;

    @Property(name = "date")
    private DateValue date;

    @Property(name = "attended")
    private Boolean attended;

    @Relationship(type = "IN", direction = Relationship.Direction.OUTGOING)
    private Zone zone;
}
