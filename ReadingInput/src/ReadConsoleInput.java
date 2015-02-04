import java.util.Scanner;


public class ReadConsoleInput {

	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		String line = scanner.nextLine();
		System.out.println(line);
		int i = scanner.nextInt();
		System.out.println(i);
		String l = scanner.nextLine();
		System.out.println(l);
	}
}
