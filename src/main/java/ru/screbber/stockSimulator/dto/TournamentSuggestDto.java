package ru.screbber.stockSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentSuggestDto {

    private String name;

    private String mode;
}
