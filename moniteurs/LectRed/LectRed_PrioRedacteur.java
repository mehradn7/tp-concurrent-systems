// Time-stamp: <08 Apr 2008 11:35 queinnec@enseeiht.fr>

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import Synchro.Assert;

/** Lecteurs/rédacteurs
 * stratégie d'ordonnancement: priorité aux rédacteurs,
 * implantation: avec un moniteur. */
public class LectRed_PrioRedacteur implements LectRed
{
  private boolean ecriture;
  private int lect;
  private int lectAttente;
  private Lock moniteur;
  private Condition LP;
  private Condition EP;

    public LectRed_PrioRedacteur()
    {
      this.ecriture = false;
      this.lect = 0;
      this.lectAttente = 0;
      this.moniteur = new ReentrantLock();
      this.LP = moniteur.newCondition();
      this.EP = moniteur.newCondition();
    }

    public void demanderLecture() throws InterruptedException
    {
      this.moniteur.lock();
      while ((ecriture == true)){
	this.lectAttente++;
	LP.await();
	this.lectAttente--;	
      }
      this.lect++;
      LP.signal();
      this.moniteur.unlock();
    }

    public void terminerLecture() throws InterruptedException
    {
      this.moniteur.lock();
      if (this.lectAttente > 0){
	this.LP.signal();
      }
      else{
      this.lect--;
      if ((this.lect == 0) ){
	this.EP.signal();
      }
      }
      this.moniteur.unlock();
    }

    public void demanderEcriture() throws InterruptedException
    {
      this.moniteur.lock();
     while((this.lect > 0) || (ecriture == true) || (this.lectAttente > 0)){
	this.EP.await();
      }
      this.ecriture = true;
      this.moniteur.unlock();
    }

    public void terminerEcriture() throws InterruptedException
    {
      this.moniteur.lock();
      this.ecriture = false;
      if (lectAttente > 0){
	this.LP.signal();
      }
      else{
	this.EP.signal();
      }
      this.moniteur.unlock();
    }

    public String nomStrategie()
    {
        return "Stratégie: Priorité LECTEURS (contrairement au nom du fichier).";
    }
}
