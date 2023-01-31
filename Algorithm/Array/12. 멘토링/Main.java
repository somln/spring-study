import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);
		int N = in.nextInt();
		int M = in.nextInt();
		int[][] arr = new int[M][N];
		int result=0;
		for(int i=0; i<M; i++) {
			for(int j=0; j<N; j++) {
				arr[i][j]=in.nextInt();
			}
		}
		
		//i와 j는 학생 번호, i가 맨토, j가 맨티
		for(int i=1; i<=N; i++) {
			for(int j=1; j<=N; j++) {
				int cnt=0;
				for(int k=0; k<M; k++) {  //각 시험마다
					int pi=0, pj=0;  //i의 등수와 j의 등수 저장하는 변수
					for(int s=0; s<N; s++) {  //i와 j의 등수 구하기
						if(arr[k][s]==i) pi=s;
						if(arr[k][s]==j) pj=s;
					}
					if(pi>pj) cnt++;  //i의 등수가 j의 등수보다 크면 cnt 증가
				
				}
				if(cnt==M) result++;  //모든 시험이 다 등수가 높음면 result 증가
			}

		}

		System.out.println(result);
		
 	    in.close();	
	}
}

