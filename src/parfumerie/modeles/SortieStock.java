package parfumerie.modeles;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modèle correspondant à la table `sortie_stock`.
 */
public class SortieStock {

    private int        idSortie;
    private int        idProduit;
    private int        quantite;
    private LocalDate  dateSortie;
    private String     motif;    // 'Vente','Perte','Retour','Offert'
    private BigDecimal prixVente;

    // ─── Constructeurs ───────────────────────────────────────────────────────

    public SortieStock() {}

    public SortieStock(int idSortie, int idProduit, int quantite,
                       LocalDate dateSortie, String motif, BigDecimal prixVente) {
        this.idSortie  = idSortie;
        this.idProduit = idProduit;
        this.quantite  = quantite;
        this.dateSortie= dateSortie;
        this.motif     = motif;
        this.prixVente = prixVente;
    }

    // ─── Getters / Setters ───────────────────────────────────────────────────

    public int        getIdSortie()              { return idSortie; }
    public void       setIdSortie(int v)          { this.idSortie = v; }

    public int        getIdProduit()              { return idProduit; }
    public void       setIdProduit(int v)         { this.idProduit = v; }

    public int        getQuantite()               { return quantite; }
    public void       setQuantite(int v)          { this.quantite = v; }

    public LocalDate  getDateSortie()             { return dateSortie; }
    public void       setDateSortie(LocalDate v)  { this.dateSortie = v; }

    public String     getMotif()                  { return motif; }
    public void       setMotif(String v)          { this.motif = v; }

    public BigDecimal getPrixVente()              { return prixVente; }
    public void       setPrixVente(BigDecimal v)  { this.prixVente = v; }
}