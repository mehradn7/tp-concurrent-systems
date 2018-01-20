// Time-stamp: <08 déc 2009 08:30 queinnec@enseeiht.fr>

import java.util.concurrent.Semaphore;
import java.lang.Thread;

public class PhiloSem2 implements StrategiePhilo {

    /****************************************************************/
  private Semaphore[] philo;
  private Semaphore mutex;
  public enum etatPossible {
    pense,
    mange,
    attend;
  }
  private etatPossible[] etat;

    public PhiloSem2 (int nbPhilosophes) {
      this.philo = new Semaphore[nbPhilosophes];
      this.etat = new etatPossible[nbPhilosophes];
      for(int i=0;i<nbPhilosophes;i++){
	this.philo[i] = new Semaphore(0);
	this.etat[i] = etatPossible.pense;
      }
      this.mutex = new Semaphore(1);



    }

    /** Le philosophe no demande les fourchettes.
     *  Précondition : il n'en possède aucune.
     *  Postcondition : quand cette méthode retourne, il possède les deux fourchettes adjacentes à son assiette. */
    public void demanderFourchettes (int no) throws InterruptedException
    {
      this.mutex.acquire();
      if((this.etat[Main.PhiloGauche(no)] != etatPossible.mange) && (this.etat[Main.PhiloDroite(no)] != etatPossible.mange)){
	this.etat[no] = etatPossible.mange;
	this.mutex.release();
      }
      else{
	this.etat[no] = etatPossible.attend;
	this.mutex.release();
	this.philo[no].acquire();
      }      
    }

    /** Le philosophe no rend les fourchettes.
     *  Précondition : il possède les deux fourchettes adjacentes à son assiette.
     *  Postcondition : il n'en possède aucune. Les fourchettes peuvent être libres ou réattribuées à un autre philosophe. */
    public void libererFourchettes  (int no) throws InterruptedException
    {
      this.mutex.acquire();
      this.etat[no] = etatPossible.pense;
      if ((this.etat[Main.PhiloGauche(no)] == etatPossible.attend) && ((this.etat[Main.PhiloGauche(Main.PhiloGauche(no))] != etatPossible.mange) && (this.etat[no] != etatPossible.mange))){
	this.etat[Main.PhiloGauche(no)] = etatPossible.mange;
	this.philo[Main.PhiloGauche(no)].release();
      }
  
       if ((this.etat[Main.PhiloDroite(no)] == etatPossible.attend) && ((this.etat[Main.PhiloDroite(Main.PhiloDroite(no))] !=etatPossible.mange) && (this.etat[no] != etatPossible.mange))){
	this.etat[Main.PhiloDroite(no)] = etatPossible.mange;
	this.philo[Main.PhiloDroite(no)].release();
      }

      this.mutex.release();
     
    }

    /** Nom de cette stratégie (pour la fenêtre d'affichage). */
    public String nom() {
        return "Stratégie avec états des philosophes";
    }

}

