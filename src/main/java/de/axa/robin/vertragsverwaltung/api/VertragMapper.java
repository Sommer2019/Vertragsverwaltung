package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between VertragDTO and Vertrag.
 */
@Mapper
interface VertragMapper {
    VertragMapper INSTANCE = Mappers.getMapper(VertragMapper.class);

    /**
     * Converts a VertragDTO to a Vertrag.
     *
     * @param vertragDTO the VertragDTO to convert
     * @return the converted Vertrag
     */
    Vertrag toVertrag(VertragDTO vertragDTO);
}