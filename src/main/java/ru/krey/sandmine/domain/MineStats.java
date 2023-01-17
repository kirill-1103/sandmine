package ru.krey.sandmine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.neo4j.driver.internal.value.DateTimeValue;
import org.neo4j.driver.internal.value.DateValue;
import org.springframework.data.neo4j.core.schema.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Node("MINE_STATS")
public class MineStats {
    @Id
    @GeneratedValue
    private Long id = null;

    @Property(name = "date")
    private DateValue date;

    @Property(name = "weight")
    private Double weight;

    @Property(name = "last_edit_time")
    private DateTimeValue lastEditTime;

    @Relationship(type = "LAST_EDITED", direction = Relationship.Direction.OUTGOING)
    private Worker lastEditedBy;

    @Relationship(type = "IN_ZONE", direction = Relationship.Direction.OUTGOING)
    private Zone parentZone;

    public MineStats(DateValue date, Double weight, DateTimeValue lastEditTime, Worker lastEditedBy, Zone parentZone){
        this.date = date;
        this.weight = weight;
        this.lastEditedBy = lastEditedBy;
        this.lastEditTime = lastEditTime;
        this.parentZone = parentZone;
    }
}
