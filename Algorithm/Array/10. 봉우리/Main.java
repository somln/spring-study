import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);
		int N = in.nextInt();
		int[][] board = new int[N+2][N+2];
		int flag=1;;
		int cnt=0;
		
		for(int i=1; i<N+1; i++) {
			for(int j=1; j<N+1; j++) {
				board[i][j]=in.nextInt();
			   }
			}
		
		//해당 인덱스 보다 x값이 1큰 위치, x값이 1 작은 위치, y값이 1 큰 위치, y값이 1 작은 위치
		int[] x = new int[] {1, -1, 0, 0};
		int[] y = new int[] {0, 0, 1, -1};
		
		for(int i=1; i<N+1; i++) {
			for(int j=1; j<N+1; j++) {
				flag=1;
				for(int k=0; k<4; k++) {
					int nx=i+x[k];
					int ny=j+y[k];
					if(board[i][j]<=board[nx][ny]) {
						//해당 위치보다 크거나 같은 값이 있으면 바로 flag=0으로 하고 빠져나오기
						flag=0;
						break;
					}
				 }
				if(flag==1) cnt++;
			   }
			}
		System.out.println(cnt);
		
 	    in.close();	
	}
}