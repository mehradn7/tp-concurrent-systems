import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// v0, 16/10/15 (PM)

class TamponBorné
/*
 * TamponBorné<Item> serait clairement mieux, mais, pour simplifier la mise en
 * œuvre du test, on se limite ici à un tampon d'entiers. Les modifications à
 * apporter pour avoir une classe générique (qui ne comprendrait pas le code de
 * test) sont indiquées en commentaire
 */
{
	private int taille; // nombre de cases du tampon
	private int nbOccupé = 0; // nombre d'items présents dans le tampon
								// (initialement, le tampon est vide)
	private int queue = 0; // queue de file (tampon circulaire), insertions
	private int tête = 0; // tête de file (tampon circulaire), extractions

	private int trace = 0; // utile uniquement pour faciliter le test :
							// on va considérer que l'on dépose les valeurs
							// successives
							// de trace...

	private final Lock lock = new ReentrantLock();

	private final Condition notFull = lock.newCondition();
	private final Condition notEmpty = lock.newCondition();

	private final Object obj = new Object();
	private final Object obj2 = new Object();

	private int[] tampon; // private Item[] tampon;

	public TamponBorné(int t) {
		taille = t;
		tampon = new int[taille]; // tampon = (Item[]) new Object[taille];
	}

	public void déposer() throws Exception {
	//	lock.lock();
	synchronized(obj){
		try {
			while (nbOccupé == taille) {
				//notFull.await();
				obj.wait();
			}
			// dépôt
			tampon[queue] = trace++; // tampon[queue] = i;
			queue = (queue + 1) % taille;
			nbOccupé++;

			// affichage pour le test uniquement
			String msg = "P : " + (trace - 1);
			if (nbOccupé > 0) {
				//notEmpty.signal();
				obj2.notify();
			}
			System.out.println(msg);
		} finally {
			//lock.unlock();
		}
	}
	}

	public int retirer() throws Exception {
		int i;
		synchronized(obj2){
		//lock.lock();
		// retrait
		try {
			while (nbOccupé == 0) {
			//	notEmpty.await();
			obj2.wait();
			}

			i = tampon[tête];
			tête = (tête + 1) % taille;
			nbOccupé--;

			// affichage pour le test uniquement
			String msg = "C : " + i;
			if (nbOccupé <= taille) {
			  obj.notify();
			//	notFull.signal();
			}
			System.out.println(msg);
		} finally {
		//	lock.unlock();
		}
		return i;
	}
	}
}

// --------------------------------------inutile de modifier ce qui suit
// ------------------

class Producteur implements Runnable {
	private TamponBorné tampon;

	public Producteur(TamponBorné t) {
		tampon = t;
	}

	public void run() {
		try {
			Thread.sleep(10); // pour le test : initialement, les consommateurs
								// trouveront tous un tampon vide
			for (int i = 0; i < 25; i++) {
				// possible de trouver des producteurs bloqués à la fin, selon
				// le nb de consommateurs
				try {
					tampon.déposer();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Thread.sleep(2 * i); // producteurs ralentissent un peu
			}
		} catch (InterruptedException e) {
			System.out.println("interrompu");
		}
	}
}

class Consommateur implements Runnable {
	private TamponBorné tampon;
	private int identité;

	public Consommateur(TamponBorné t) {
		tampon = t;
	}

	public void run() {
		int res;
		for (int i = 0; i < 25; i++) {
			// possible de trouver des consommateurs bloqués à la fin, selon le
			// nb de producteurs
			try {
				res = tampon.retirer();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(10 * i); // consommateurs ralentissent davantage
			} catch (InterruptedException e) {
				System.out.println("interrompu");
			}
		}
	}
}

public class ProdConso {
	public static void main(String[] args) {
		int nbProd = 5;
		int nbConso = 10;
		int tailleTampon = 10;
		// aucun blindage : on suppose que les valeurs de paramètres fournies
		// sont raisonnables
		if (args.length != 3) {
			System.out.println("java ProdConso <nbProd> <nbConso> <nbCases>");
			System.out.println("-> choix par défaut : " + nbProd + "/" + nbConso + "/" + tailleTampon);
		} else {
			nbProd = Integer.parseInt(args[0]);
			nbConso = Integer.parseInt(args[1]);
			tailleTampon = Integer.parseInt(args[2]);
		}
		System.out.println(
				"nbProd (arg1) : " + nbProd + " /nbConso (arg2) : " + nbConso + " /nbCases) (arg3) : " + tailleTampon);
		TamponBorné t = new TamponBorné(tailleTampon);
		for (int i = 0; i < nbProd; i++) {
			new Thread(new Producteur(t)).start();
		}
		for (int i = 0; i < nbConso; i++) {
			new Thread(new Consommateur(t)).start();
		}
		// ajouter éventuellement un thread pour gérer l'arrêt et une prise de
		// cliché finale
	}
}

