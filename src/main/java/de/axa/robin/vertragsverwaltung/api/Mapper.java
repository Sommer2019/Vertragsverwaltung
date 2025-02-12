package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.model.*;
import de.axa.robin.vertragsverwaltung.models.Fahrzeug;
import de.axa.robin.vertragsverwaltung.models.Partner;
import de.axa.robin.vertragsverwaltung.models.Preis;
import de.axa.robin.vertragsverwaltung.models.Vertrag;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper interface for converting between VertragDTO and Vertrag.
 */
@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface Mapper {
    /**
     * Converts a VertragDTO to a Vertrag.
     *
     * @param vertragDTO the VertragDTO to convert
     * @return the converted Vertrag
     */
    @Mapping(target = "monatlich", source = "monatlich")
    @Mapping(target = "versicherungsbeginn", source = "versicherungsbeginn")
    @Mapping(target = "versicherungsablauf", source = "versicherungsablauf")
    @Mapping(target = "antragsDatum", source = "antragsDatum")
    @Mapping(target = "fahrzeug", source = "fahrzeug")
    @Mapping(target = "partner", source = "partner")
    Vertrag toVertrag(VertragDTO vertragDTO);

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

    @Mapping(target = "amtlichesKennzeichen", source = "amtlichesKennzeichen")
    @Mapping(target = "hersteller", source = "hersteller")
    @Mapping(target = "typ", source = "typ")
    @Mapping(target = "hoechstgeschwindigkeit", source = "hoechstgeschwindigkeit")
    @Mapping(target = "wagnisskennziffer", source = "wagnisskennziffer")
    Fahrzeug toFahrzeug(FahrzeugDTO fahrzeugDTO);

    @Mapping(target = "speed", source = "speed")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "faktor", source = "faktor")
    Preis toPreis(PreisDTO preisDTO);

    @Mapping(target = "vsnr", source = "vsnr")
    @Mapping(target = "preis", source = "preis")
    @Mapping(target = "monatlich", source = "monatlich")
    @Mapping(target = "versicherungsbeginn", source = "versicherungsbeginn")
    @Mapping(target = "versicherungsablauf", source = "versicherungsablauf")
    @Mapping(target = "antragsDatum", source = "antragsDatum")
    @Mapping(target = "fahrzeug", source = "fahrzeug")
    @Mapping(target = "partner", source = "partner")
    VertragApi toVertragApi(Vertrag vertrag);
}