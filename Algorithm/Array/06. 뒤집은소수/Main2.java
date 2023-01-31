import java.util.Scanner;

public class Main2 {
	
	//소수인지 판별하는 함수
	public boolean isPrime(int num) {
		if(num==1) return false;
		for(int i=2; i<num; i++) {
			if(num%i==0) return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		Main2 T = new Main2();
		Scanner in = new Scanner(System.in);
		int N = in.nextInt();

		for(int i=0; i<N; i++) {
			int tmp = in.nextInt();
			int res=0;
			
			//숫자 뒤집기
			while(tmp>0){
				int t = tmp%10;
				res = res*10+t;
				tmp= tmp/10;
			}
			//소수이면 해당 값 출력하기
			if(T.isPrime(res)) System.out.print(res + " ");
		}

 	    in.close();
	}

}

