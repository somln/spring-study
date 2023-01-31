import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);
		int N = in.nextInt();
		int[][] board = new int[N][N];
		ArrayList<Integer> store = new ArrayList<Integer>();
		int sum1=0;
		int sum2=0;
		
		// 격자판 입력받기
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				board[i][j]= in.nextInt();
			}
		}
		
		//같은 행,열 끼리 합
		for(int i=0; i<N; i++) {
			sum1=0; sum2=0;
			for(int j=0; j<N; j++) {
				sum1+=board[i][j];
				sum2+=board[j][i];
			}
			store.add(sum1);
			store.add(sum2);
		}
		
		//대각선끼리 합
		sum1=0; sum2=0;
		for(int i=0; i<N; i++) {
			sum1+=board[i][i];
			sum2+=board[i][N-i-1];
		}
		store.add(sum1);
		store.add(sum2);
		
		System.out.println(Collections.max(store));
 	    in.close();
	}

}


