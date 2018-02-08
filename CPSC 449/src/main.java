
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class main {
	public static Constraints con;
	static final String nKey = "Name:";
	static final String fpaKey = "forced partial assignment:";
	static final String fmKey = "forbidden machine:";
	static final String tnKey = "too-near tasks:";
	static final String mpKey = "machine penalties:";
	static final String tnpKey = "too-near penalties";
	
	static final String[] keys = new String[] {nKey, fpaKey, fmKey, tnKey, mpKey, tnpKey};
	
	static final String parsingError = "Error while parsing input file";
	static final String forcedAssignError = "partial assignment error";
	static final String hardConError = "invalid machine/task";
	static final String mpError = "machine penalty error";
	static final String tnpError = "invalid task";
	static final String softConError = "";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ParseInput(args[1]);
		} catch(IOException e) {
			
		}
	}
	
	public static void ParseInput(String file) throws IOException {
		
	}
	
	public static Line Eat(BufferedReader br) throws IOException {
		boolean cont = true;
		boolean space = false;
		Line line = new Line("");
		while(cont) {
			cont = false;
			space = EatSpace(br);
			line = EatLine(br);
			if(space && !line.eaten)
				throw new IOException(parsingError);
			if(space || line.eaten)
				cont = true;
		}
		return line;
	}
	
	public static boolean EatSpace(BufferedReader br) throws IOException {
		boolean ate = false;
		while(br.read() == ' ') {
			ate = true;
		}
		return ate;
	}
	
	public static Line EatLine(BufferedReader br) throws IOException {
		Line line = new Line(br.readLine());
		if(line.GetContents().equals("\n"))
			line.eaten = true;
		while(line.GetContents().equals("\n")) {
			line.SetContents(br.readLine());
		}
		return line;
	}

}
