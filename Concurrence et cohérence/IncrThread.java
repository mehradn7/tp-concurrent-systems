class Incrementeur implements Runnable{
  private int nb_incr;
  public Incrementeur(int nb_incrementations){
    this.nb_incr = nb_incrementations;
  }
  public void run(){
    for (int i = 1; i <= this.nb_incr; i++){
      //System.out.println("cpt avant = " + IncrThread.cpt);
    synchronized(IncrThread.mutex){      
      IncrThread.cpt = IncrThread.cpt + i/i;
    }
      //System.out.println("cpt après = " + IncrThread.cpt);
    }
    try {
                Thread.sleep(1,0);
            }
            catch (InterruptedException ie)
            {
                System.out.println("InterruptedException : " + ie);
            }
    
  }
}

class TestInc implements Runnable{
  private int nb_incr;
  public TestInc(int nb_incrementations){
    this.nb_incr = nb_incrementations;
  }
  public void run(){
    for (int i = 1; i <= this.nb_incr; i++){
      //System.out.println("cpt avant = " + IncrThread.cpt);
    synchronized(IncrThread.autremutex){      
      IncrThread.cpt = IncrThread.cpt + i/i;
      System.out.println("ici");
    }
      //System.out.println("cpt après = " + IncrThread.cpt);
    }
    try {
                Thread.sleep(1,0);
            }
            catch (InterruptedException ie)
            {
                System.out.println("InterruptedException : " + ie);
            }
    
  }
}



public class IncrThread{
static long cpt = 0L;
static final long NB_IT = 1000L;
static Object mutex = new Object();
static Object autremutex = new Object();

  public static void main(String[] args) {
    Thread[] arrayThread = new Thread[Integer.parseInt(args[0])];
    Incrementeur inc = new Incrementeur(java.lang.Math.toIntExact(IncrThread.NB_IT)*100 / Integer.parseInt(args[0]));
    TestInc classtest = new TestInc(java.lang.Math.toIntExact(IncrThread.NB_IT)*100 / Integer.parseInt(args[0]));
    
    for(int k = 0; k < Integer.parseInt(args[0]); k++){
      if (k==1){
	Thread testaccescpt = new Thread(classtest);
	arrayThread[k] = testaccescpt;
	testaccescpt.start();
      }
      else{
            Thread t = new Thread(inc);
	    arrayThread[k] = t;
	    t.start();
      }
    }


    try{
    for(int k = 0; k < Integer.parseInt(args[0]); k++){
      arrayThread[k].join();
    }
    }catch(InterruptedException ie){System.out.println("InterruptedException : " + ie);}
            

    System.out.println("cpt final = " + IncrThread.cpt);



  }
  
}
