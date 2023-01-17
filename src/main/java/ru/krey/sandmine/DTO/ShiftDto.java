package ru.krey.sandmine.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.krey.sandmine.domain.Shift;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftDto {
    private Long shiftId;

    @NonNull private String date;

    @NonNull private Boolean attended;

    @NonNull private Long zoneId;

    public ShiftDto(Shift shift){
        this.shiftId = shift.getId();
        this.date = shift.getDate().asLocalDate().toString();
        this.attended = shift.getAttended();
        this.zoneId = shift.getZone().getId() == null ? -1 : shift.getZone().getId();
    }
}
