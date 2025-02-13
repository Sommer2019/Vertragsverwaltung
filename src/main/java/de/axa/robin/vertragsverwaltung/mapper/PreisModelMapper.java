package de.axa.robin.vertragsverwaltung.mapper;

import de.axa.robin.preisverwaltung.model.PreisDTO;
import de.axa.robin.vertragsverwaltung.models.Preis;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper interface for converting PreisDTO to Preis.
 */
@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PreisModelMapper {
    /**
     * Maps the fields from PreisDTO to Preis.
     *
     * @param preisDTO the PreisDTO object
     * @return the mapped Preis object
     */
    @Mapping(target = "speed", source = "speed")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "faktor", source = "faktor")
    Preis toPreis(PreisDTO preisDTO);
}