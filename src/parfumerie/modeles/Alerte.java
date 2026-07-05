package parfumerie.modeles;

import java.time.LocalDateTime;

/**
 * Modèle pour les alertes de stock faible.
 * Le champ "niveau" est un String : "CRITIQUE" ou "AVERTISSEMENT".
 */
public class Alerte {

    private int           idAlerte;
    private int           idProduit;
    private String        message;
    private String        niveau;      // "CRITIQUE" ou "AVERTISSEMENT"
    private LocalDateTime dateAlerte;
    private boolean       lu;

    // ─── Constructeurs ───────────────────────────────────────────────────────

    public Alerte() {}

    public Alerte(int idAlerte, int idProduit, String message,
                  String niveau, LocalDateTime dateAlerte, boolean lu) {
        this.idAlerte  = idAlerte;
        this.idProduit = idProduit;
        this.message   = message;
        this.niveau    = niveau;
        this.dateAlerte= dateAlerte;
        this.lu        = lu;
    }

    // ─── Getters / Setters ───────────────────────────────────────────────────

    public int           getIdAlerte()              { return idAlerte; }
    public void          setIdAlerte(int v)          { this.idAlerte = v; }

    public int           getIdProduit()             { return idProduit; }
    public void          setIdProduit(int v)         { this.idProduit = v; }

    public String        getMessage()               { return message; }
    public void          setMessage(String v)       { this.message = v; }

    /** Retourne "CRITIQUE" ou "AVERTISSEMENT" */
    public String        getNiveau()                { return niveau; }
    public void          setNiveau(String v)        { this.niveau = v; }

    public LocalDateTime getDateAlerte()            { return dateAlerte; }
    public void          setDateAlerte(LocalDateTime v){ this.dateAlerte = v; }

    public boolean       isLu()                     { return lu; }
    public void          setLu(boolean v)           { this.lu = v; }

    public static class Niveau {

        public Niveau() {
        }
    }
}