// Time-stamp: <08 déc 2009 08:30 queinnec@enseeiht.fr>

import java.util.concurrent.Semaphore;
import java.lang.Thread;

public class PhiloSem implements StrategiePhilo {

    /****************************************************************/
  private Semaphore[] baguette;

    public PhiloSem (int nbPhilosophes) {
      this.baguette = new Semaphore[nbPhilosophes];
      for(int i=0;i<nbPhilosophes;i++){
	this.baguette[i] = new Semaphore(1, true);
      }


    }

    /** Le philosophe no demande les fourchettes.
     *  Précondition : il n'en possède aucune.
     *  Postcondition : quand cette méthode retourne, il possède les deux fourchettes adjacentes à son assiette. */
    public void demanderFourchettes (int no) throws InterruptedException
    {
      baguette[Main.FourchetteDroite(no)].acquire();
      Thread.sleep(2000);
      baguette[Main.FourchetteGauche(no)].acquire();
      
    }

    /** Le philosophe no rend les fourchettes.
     *  Précondition : il possède les deux fourchettes adjacentes à son assiette.
     *  Postcondition : il n'en possède aucune. Les fourchettes peuvent être libres ou réattribuées à un autre philosophe. */
    public void libererFourchettes (int no)
    {
      baguette[Main.FourchetteDroite(no)].release();
      baguette[Main.FourchetteGauche(no)].release();

    }

    /** Nom de cette stratégie (pour la fenêtre d'affichage). */
    public String nom() {
        return "Stratégie naïve : chaque philosophe prend la fourchette à sa droite";
    }

}

