package test;

public class Prallel {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		final Counter c = new Counter();
		
		Thread[] threads = new Thread[2];
		for( int i=0 ; i<threads.length ; ++i){
			threads[i] = new Thread(){
				@Override
				public void run(){
					System.out.println(".");
					for( int z=0 ; z<50 ; ++z){
						try{
							c.f();
							
						}
						catch( Exception e){
							System.err.println("e");
							--z;
						}
						System.out.println( c.get());
					}
					System.out.println( c.get());
				}
			};
			threads[i].start();
		}
		
		for( int i=0 ; i<threads.length ; ++i){
			threads[i].join();
		}
		
//		System.out.println( c.get());
//		System.out.println( c.get());
		
	}
	
	

}
