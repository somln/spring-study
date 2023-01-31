import java.util.Scanner;

public class Main {
	
	//소수인지 판별하는 함수
	public boolean isPrime(int num) {
		if(num==1) return false;
		for(int i=2; i<num; i++) {
			if(num%i==0) return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		Main T = new Main();
		Scanner in = new Scanner(System.in);
		int N = in.nextInt();
		int num;
		String temp;
		
		for(int i=0; i<N; i++) {
			//문자열로 입력 받아서 StringBuilder로 뒤집기
			temp = new StringBuilder(in.next()).reverse().toString();
			//뒤집은 수를 정수형으로 변환해서 저장하기
			num= Integer.parseInt(temp);
			//소수이면 해당 값 출력하기
			if(T.isPrime(num)) System.out.print(num + " ");
		}

 	    in.close();
	}

}

