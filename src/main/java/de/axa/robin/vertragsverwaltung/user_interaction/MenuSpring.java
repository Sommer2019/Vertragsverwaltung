package de.axa.robin.vertragsverwaltung.user_interaction;

import de.axa.robin.vertragsverwaltung.modell.Fahrzeug;
import de.axa.robin.vertragsverwaltung.modell.Partner;
import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import de.axa.robin.vertragsverwaltung.modell.VertragDTO;
import de.axa.robin.vertragsverwaltung.storage.Setup;
import de.axa.robin.vertragsverwaltung.storage.Vertragsverwaltung;
import de.axa.robin.vertragsverwaltung.storage.editor.Create;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


@Controller
public class MenuSpring {
    private int vsnr;
    private final Setup setup = new Setup();
    private final Scanner scanner = new Scanner(System.in);
    private final Input input = new Input(scanner);
    private final Output output = new Output();
    private final Vertragsverwaltung vertragsverwaltung = new Vertragsverwaltung(setup);
    private final Create create = new Create(input, vertragsverwaltung, output);

    public int getVsnr() {
        return vsnr;
    }
    private final Create creator = new Create(input, vertragsverwaltung, output);

    @GetMapping("/printVertrage")
    public String showAll(Model model) {
        List<Vertrag> vertrage = vertragsverwaltung.getVertrage();
        BigDecimal summe = BigDecimal.ZERO;
        for (Vertrag v : vertrage) {
            if (!v.getMonatlich()) {
                summe = summe.add(BigDecimal.valueOf(v.getPreis()));
            } else {
                summe = summe.add(BigDecimal.valueOf(v.getPreis() * 12));
            }
        }
        model.addAttribute("vertrage", vertrage);
        model.addAttribute("preis", summe);
        return "printVertrage";
    }
    @GetMapping("/")
    public String startWebsite() {
        return "index";
    }
    @GetMapping("/showDelete")
    public String showDelete(Model model) {
        model.addAttribute("showFields", true);
        return "handleVertrag";
    }
    @GetMapping("/createVertrag")
    public String createVertrag(Model model) {
        vsnr = create.createvsnr();
        VertragDTO vertragDTO = new VertragDTO();
        vertragDTO.setGender("M");
        vertragDTO.setAbrechnung("true");
        model.addAttribute("vertragdto", vertragDTO);
        model.addAttribute("vsnr",vsnr);
        return "createVertrag";
    }
    @PostMapping("/createVertrag")
    public String createVertrag(
            @ModelAttribute VertragDTO vertragdto,
            Model model) {

        boolean monatlich = Objects.equals(vertragdto.getAbrechnung(), "true");
        int PLZ = Integer.parseInt(vertragdto.getPlz());
        Partner partner = new Partner(vertragdto.getVorname(), vertragdto.getNachname(), vertragdto.getGender().charAt(0), vertragdto.getBirth(), vertragdto.getLand(), vertragdto.getStrasse(), vertragdto.getHausnummer(), PLZ, vertragdto.getStadt(), vertragdto.getBundesland());
        Fahrzeug fahrzeug = new Fahrzeug(vertragdto.getKennzeichen(), vertragdto.getHersteller(), vertragdto.getTyp(), vertragdto.getSpeed(), vertragdto.getWkz());
        double preis = creator.createPreis(monatlich, partner, fahrzeug);
        Vertrag vertrag = new Vertrag(getVsnr(), monatlich, preis, vertragdto.getStart(), vertragdto.getEnd(), vertragdto.getCreate(), fahrzeug, partner);
        vertragsverwaltung.vertragAnlegen(vertrag);

        String confirm = "Vertrag erfolgreich erstellt! Preis: " + preis + "€";
        model.addAttribute("confirm", confirm);
        return "index";
    }
    @PostMapping("/showDelete")
    public String deleteVertrag(Model model){
        vertragsverwaltung.vertragLoeschen(vsnr);
        String confirm ="Vertrag erfolgreich gelöscht!";
        model.addAttribute("confirm",confirm);
        return "index";
    }
    @PostMapping("/")
    public String processPrintVertrag(@RequestParam int VSNR, Model model) {
        vsnr = VSNR;
        Vertrag v = vertragsverwaltung.getVertrag(VSNR);
        if(v == null){
            String result ="Vertrag nicht gefunden!";
            model.addAttribute("result",result);
            return "index";
        }
        model.addAttribute("vsnr", VSNR);
        model.addAttribute("preis", v.getPreis());
        model.addAttribute("abrechnungszeitraumMonatlich", v.getMonatlich());
        model.addAttribute("versicherungsbeginn", v.getVersicherungsbeginn());
        model.addAttribute("versicherungsablauf", v.getVersicherungsablauf());
        model.addAttribute("antragsdatum", v.getAntragsDatum());
        model.addAttribute("kennzeichen", v.getFahrzeug().getAmtlichesKennzeichen());
        model.addAttribute("hersteller", v.getFahrzeug().getHersteller());
        model.addAttribute("typ", v.getFahrzeug().getTyp());
        model.addAttribute("maxspeed", v.getFahrzeug().getHoechstgeschwindigkeit());
        model.addAttribute("wkz", v.getFahrzeug().getWagnisskennziffer());
        model.addAttribute("vorname", v.getPartner().getVorname());
        model.addAttribute("nachname", v.getPartner().getNachname());
        model.addAttribute("geschlecht", v.getPartner().getGeschlecht());
        model.addAttribute("geburtsdatum", v.getPartner().getGeburtsdatum());
        model.addAttribute("strasse", v.getPartner().getStrasse());
        model.addAttribute("hausnummer", v.getPartner().getHausnummer());
        model.addAttribute("plz", v.getPartner().getPlz());
        model.addAttribute("stadt", v.getPartner().getStadt());
        model.addAttribute("bundesland", v.getPartner().getBundesland());
        model.addAttribute("land", v.getPartner().getLand());
        return "handleVertrag";
    }
    @GetMapping("/showVertrag")
    public String showWelcomePage() {
        return "handleVertrag";
    }
}
