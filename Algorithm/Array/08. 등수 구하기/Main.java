import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);
		int N = in.nextInt();
		int[]score= new int[N];
		int[]rank = new int[N];
		int cnt=0;
		for(int i=0; i<N; i++) {
			score[i]=in.nextInt();
		}
		
		for(int i=0; i<N; i++) {
			cnt=1;
			//해당 학생의 점수보다 더 큰 점수가 있을 때 마다 등수 증가
            // 등수는 자신 보다 앞에 있는 사람이 몇 명 있는지를 나타내는 수이기 때문에
			for(int j=0; j<N; j++) {
				if(score[j]>score[i]){
					cnt++;
				}
			}
			rank[i]=cnt;
		}

		for(int r:rank) {
			System.out.print(r+ " ");
		}

 	    in.close();
	}

}
