import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);
		int N = in.nextInt();
		int count=0;
		int sum=0;
		
		for(int i=0; i<N; i++) {
			if(in.nextInt()==0) count=0;
			else count++;
			sum+=count;
		}
		System.out.println(sum);
 	    in.close();
	}

}
