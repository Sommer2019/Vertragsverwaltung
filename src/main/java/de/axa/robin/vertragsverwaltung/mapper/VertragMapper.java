package de.axa.robin.vertragsverwaltung.mapper;

import de.axa.robin.vertragsverwaltung.model.*;
import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper interface for converting Vertrag-Details.
 */
@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VertragMapper {
    /**
     * Converts a VertragDTO to a Vertrag.
     *
     * @param antragDTO the VertragDTO to convert
     * @return the converted Vertrag
     */
    @Mapping(target = "monatlich", source = "monatlich")
    @Mapping(target = "versicherungsbeginn", source = "versicherungsbeginn")
    @Mapping(target = "versicherungsablauf", source = "versicherungsablauf")
    @Mapping(target = "antragsDatum", source = "antragsDatum")
    @Mapping(target = "fahrzeug", source = "fahrzeug")
    @Mapping(target = "partner", source = "partner")
    Vertrag toVertrag(AntragDTO antragDTO);

    /**
     * Converts a PartnerDTO to a Partner.
     *
     * @param partnerDTO the PartnerDTO to convert
     * @return the converted Partner
     */
    @Mapping(target = "vorname", source = "vorname")
    @Mapping(target = "nachname", source = "nachname")
    @Mapping(target = "geschlecht", source = "geschlecht")
    @Mapping(target = "geburtsdatum", source = "geburtsdatum")
    @Mapping(target = "land", source = "land")
    @Mapping(target = "strasse", source = "strasse")
    @Mapping(target = "hausnummer", source = "hausnummer")
    @Mapping(target = "plz", source = "plz")
    @Mapping(target = "stadt", source = "stadt")
    @Mapping(target = "bundesland", source = "bundesland")
    Partner toPartner(PartnerDTO partnerDTO);

    /**
     * Converts a FahrzeugDTO to a Fahrzeug.
     *
     * @param fahrzeugDTO the FahrzeugDTO to convert
     * @return the converted Fahrzeug
     */
    @Mapping(target = "amtlichesKennzeichen", source = "amtlichesKennzeichen")
    @Mapping(target = "hersteller", source = "hersteller")
    @Mapping(target = "typ", source = "typ")
    @Mapping(target = "hoechstgeschwindigkeit", source = "hoechstgeschwindigkeit")
    @Mapping(target = "wagnisskennziffer", source = "wagnisskennziffer")
    Fahrzeug toFahrzeug(FahrzeugDTO fahrzeugDTO);

    /**
     * Converts a Vertrag to a VertragApi.
     *
     * @param vertrag the Vertrag to convert
     * @return the converted VertragApi
     */
    @Mapping(target = "vsnr", source = "vsnr")
    @Mapping(target = "preis", source = "preis")
    @Mapping(target = "monatlich", source = "monatlich")
    @Mapping(target = "versicherungsbeginn", source = "versicherungsbeginn")
    @Mapping(target = "versicherungsablauf", source = "versicherungsablauf")
    @Mapping(target = "antragsDatum", source = "antragsDatum")
    @Mapping(target = "fahrzeug", source = "fahrzeug")
    @Mapping(target = "partner", source = "partner")
    VertragDTO toVertragDTO(Vertrag vertrag);
}