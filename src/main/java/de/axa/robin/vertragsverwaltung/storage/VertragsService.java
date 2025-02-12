package de.axa.robin.vertragsverwaltung.storage;

import de.axa.robin.vertragsverwaltung.modell.Vertrag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Component
public class VertragsService {
    private static final Logger logger = LoggerFactory.getLogger(VertragsService.class);
    @Autowired
    private Repository repository;

    /**
     * Retrieves all contracts.
     *
     * @return a list of all contracts
     */
    public List<Vertrag> getVertrage() {
        logger.info("Retrieving all contracts");
        return repository.ladeVertrage();
    }

    /**
     * Retrieves a contract by its VSNR.
     *
     * @param vsnr the VSNR of the contract
     * @return the contract with the specified VSNR, or null if not found
     */
    public Vertrag getVertrag(int vsnr) {
        logger.info("Retrieving contract with VSNR: {}", vsnr);
        return repository.ladeVertrage().stream()
                .filter(v -> v.getVsnr() == vsnr)
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates a new contract.
     *
     * @param vertrag the contract to create
     * @return the created contract
     */
    public Vertrag vertragAnlegen(Vertrag vertrag) {
        logger.info("Creating new contract: {}", vertrag);
        List<Vertrag> vertrage = repository.ladeVertrage();
        vertrage.add(vertrag);
        repository.speichereVertrage(vertrage);
        return vertrag;
    }

    /**
     * Deletes a contract by its VSNR.
     *
     * @param vsnr the VSNR of the contract to delete
     * @return true if the contract was deleted, false otherwise
     */
    public boolean vertragLoeschen(int vsnr) {
        List<Vertrag> vertrage = repository.ladeVertrage();
        boolean removed = vertrage.removeIf(v -> v.getVsnr() == vsnr);
        logger.info("Contract removed: " + removed);
        logger.info("VSNR: " + vsnr);
        repository.speichereVertrage(vertrage);
        return removed;
    }

    /**
     * Checks if a contract exists by its VSNR.
     *
     * @param vsnr the VSNR of the contract
     * @return true if the contract exists, false otherwise
     */
    public boolean vertragExistiert(int vsnr) {
        boolean exists = repository.ladeVertrage().stream().anyMatch(v -> v.getVsnr() == vsnr);
        logger.info("Contract existence check for VSNR {}: {}", vsnr, exists);
        return exists;
    }

    /**
     * Checks if a license plate exists.
     *
     * @param kennzeichen the license plate to check
     * @return true if the license plate exists, false otherwise
     */
    public boolean kennzeichenExistiert(String kennzeichen) {
        boolean exists = repository.ladeVertrage().stream()
                .anyMatch(v -> v.getFahrzeug().getAmtlichesKennzeichen().equals(kennzeichen));
        logger.info("License plate existence check for {}: {}", kennzeichen, exists);
        return exists;
    }
}
