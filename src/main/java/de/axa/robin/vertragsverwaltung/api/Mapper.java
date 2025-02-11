package de.axa.robin.vertragsverwaltung.api;

import de.axa.robin.vertragsverwaltung.model.FahrzeugDTO;
import de.axa.robin.vertragsverwaltung.model.PartnerDTO;
import de.axa.robin.vertragsverwaltung.model.PreisDTO;
import de.axa.robin.vertragsverwaltung.model.VertragDTO;
import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Preis;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
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
}