
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {
	public static Constraints con;
	static final String nKey = "name:";
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
	static final String numberError = "invalid penalty";
	static final String noSolution = "No valid solution possible!";
	static final String programUse = "Proper use of the program is: \n     scheduler <inputfile> <outputfile>";
	
	static int[] cState = new int[8];
	static int[] tState = new int[8];
	static int cPenalty;
	static int tPenalty;
	
	static int[] bestState = new int[8];
	static int bestPenalty = Integer.MAX_VALUE;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		con = new Constraints();
		
		InitializeState(cState);
		InitializeState(tState);
		
		
		try {
			ParseInput(args[0]);
		} catch(IOException e) {
			System.out.println(e.getMessage());
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println(programUse);
		}
		if(!PlaceForced())
			System.out.println(noSolution);
		System.out.println(StateToString(cState) + " Penalty: " + cPenalty);
		//Remember[row][column] || [mach][task]
		//System.out.println(con.mp[0][1]);
	}
	
	public static void InitializeState(int[] state) {
		for(int i = 0; i < state.length; i++)
			state[i] = -1;
	}
	
	
	public static boolean PlaceForced(){
		for(int i = 0; i < con.fpa.size(); i += 2) {
			int mach = con.fpa.get(i);
			int task = con.fpa.get(i+1);
			if(!CheckForbidden(mach, task))
				return false;
			if(!CheckNearHard(task, cState[modulo(mach+1,8)]))
				return false;
			if(!CheckNearHard(task, cState[modulo(mach-1,8)]))
				return false;
			cState[mach] = task;
			cPenalty += con.mp[mach][task];
			cPenalty += CheckNearPenalty(task, cState[modulo(mach+1,8)]);
			cPenalty += CheckNearPenalty(task, cState[modulo(mach-1,8)]);
		}
		return true;
	}
	
	public static boolean CheckForbidden(int mach, int task) {
		for(int i = 0; i < con.fm.size(); i += 2) {
			if(con.fm.get(i) == mach) {
				if(con.fm.get(i+1) == task)
					return false;
			}
		}
		return true;
	}
	
	public static boolean CheckNearHard(int task1, int task2) {
		if(task1 == -1 || task2 == -1)
			return true;
		for(int i = 0; i < con.tnt.size(); i ++) {
			if(con.tnt.get(i) == task1) {
				if(i % 2 == 0) {
					if(con.tnt.get(i+1) == task2)
						return false;
				}
				else {
					if(con.tnt.get(i-1) == task2)
						return false;
				}
			}
		}
		return true;
	}
	
	public static int CheckNearPenalty(int task1, int task2) {
		if(task1 == -1 || task2 == -1)
			return 0;
		for(int i = 0; i < con.tnp.size(); i ++) {
			if((i+1) % 3 == 0)
				continue;
			if(con.tnp.get(i) == task1) {
				if((i+1) % 2 == 0) {
					if(con.tnp.get(i-1) == task2)
						return con.tnp.get(i+1);
				}
				else {
					if(con.tnp.get(i+1) == task2)
						return con.tnp.get(i+2);
				}
			}
		}
		return 0;
	}
	
	
	public static int modulo(int x, int mod) {
		return (((x % mod) + mod) % mod);
	}
	
	
	
	
	public static String StateToString(int[] state) {
		String ret = "";
		for(int i = 0; i < state.length; i++) {
			ret += con.MachToChar(i) + ": " + con.TaskToChar(state[i]);
			if(i != state.length-1)
				ret += ", ";
		}
		return ret;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void ParseInput(String filename) throws IOException {
		File file;
		BufferedReader br;
		
		/*//Comment out
		File file2 = new File(".");
		for(String fileNames : file2.list()) System.out.println(fileNames);
		//All this*/
		try {
			file = new File(filename);
			br = new BufferedReader(new FileReader(file));
		} catch(IOException e) {
			System.out.println(e.getMessage()); ///Remove this later
			throw new IOException(parsingError);
		}
		Line line;
		line = Eat(br);
		if(!line.GetContents().equals(nKey))
			throw new IOException(parsingError);
		line = ReadName(br);
		if(!line.GetContents().equals(fpaKey))
			throw new IOException(parsingError);
		line = ReadFPA(br);
		line = ReadFBM(br);
		line = ReadTNT(br);
		line = ReadMP(br);
		ReadTNP(br);
		System.out.println(con.printConstraints());
	}
	
	public static Line Eat(BufferedReader br) throws IOException {
		boolean cont = true;
		//boolean l = false;
		Line space = new Line();
		Line line = new Line();
		while(cont) {
			cont = false;
			space = EatSpace(br);
			if(space.GetContents().equals("\n")) {
				space.SetContents("");
				//l = true;
				cont = true;
				continue;
			}
			line = EatLine(br);
			//l = false;
			if(space.eaten && !line.eaten)
				throw new IOException(parsingError);
			if(space.eaten || line.eaten)
				cont = true;
		}
		line.SetContents(space.GetContents() + line.GetContents());
		return line;
	}
	
	public static Line EatSpace(BufferedReader br) throws IOException {
		Line line = new Line(br.read());
		if(line.GetContents().equals(" "))
			line.eaten = true;
		while(line.GetContents().equals(" ")) {
			line.SetContents(br.read());
		}
		return line;
	}
	
	public static Line EatLine(BufferedReader br) throws IOException {
		Line line = new Line(br.readLine());
		//line.eaten = eaten;
		if(line.GetContents().equals("\n"))
			line.eaten = true;
		while(line.GetContents().equals("\n")) {
			line.SetContents(br.readLine());
		}
		return line;
	}
	
	public static Line ReadName(BufferedReader br) throws IOException {
		Line line = Eat(br);
		con.name = line.GetContents();
		line = Eat(br);
		return line;
	}
	
	public static Line ReadFPA(BufferedReader br) throws IOException {
		while(true) {
			Line line = Eat(br);
			if(line.GetContents().trim().equals(fmKey))
				return line;
			String parse = line.GetContents();
			if(parse.length() < 5)
				throw new IOException(parsingError);
			if(parse.charAt(0) == '(' && parse.charAt(2) == ',' && parse.charAt(4) == ')') {
				int res = con.AddFPA(parse.charAt(1), parse.charAt(3));
				if(res == 1)
					throw new IOException(parsingError);
				else if(res == 2)
					throw new IOException(forcedAssignError);
				else if (res == -1)
					throw new IOException(hardConError);
			}
			else
				throw new IOException(parsingError);
		}
	}
	
	public static Line ReadFBM(BufferedReader br) throws IOException {
		while(true) {
			Line line = Eat(br);
			if(line.GetContents().trim().equals(tnKey))
				return line;
			String parse = line.GetContents();
			if(parse.length() < 5)
				throw new IOException(parsingError);
			if(parse.charAt(0) == '(' && parse.charAt(2) == ',' && parse.charAt(4) == ')') {
				int res = con.AddFM(parse.charAt(1), parse.charAt(3));
				if (res == -1)
					throw new IOException(hardConError);
			}
			else
				throw new IOException(parsingError);
		}
	}
	
	public static Line ReadTNT(BufferedReader br) throws IOException {
		while(true) {
			Line line = Eat(br);
			if(line.GetContents().trim().equals(mpKey))
				return line;
			String parse = line.GetContents();
			if(parse.length() < 5)
				throw new IOException(parsingError);
			if(parse.charAt(0) == '(' && parse.charAt(2) == ',' && parse.charAt(4) == ')') {
				int res = con.AddTNT(parse.charAt(1), parse.charAt(3));
				if (res == -1)
					throw new IOException(hardConError);
			}
			else
				throw new IOException(parsingError);
		}
		
	}
	
	public static Line ReadMP(BufferedReader br) throws IOException {
		/*
		 * 	
			String[] possiblePens = parse.split(" ");
			for(String p : possiblePens) {
				System.out.print(p);
			}
		 */
		int i = 0;
		for(i = 0; i < 8; i++) {
			Line line = Eat(br);
			if(line.GetContents().trim().equals(tnpKey))
				break;
			String parse = line.GetContents();
			int count = 0;
			int subStart = 0;
			for(int j = 0; j < parse.length() && count < 8; j++) {
				if(parse.charAt(j) == ' ') {
					//if(j < subStart)
						//throw new IOException(parsingError);
					int res = con.AddMP(parse.substring(subStart, j),i, count);
					if(res == -1)
						throw new IOException(numberError);
					count++;
					subStart = j+1;
				}
			}
			if(count != 7)
				throw new IOException(mpError);
			count = 0;
		}
		if(i != 8)
			throw new IOException(mpError);
		Line line = Eat(br);
		if(line.GetContents().trim().equals(tnpKey))
			return line;
		return null;
	}
	
	public static void ReadTNP(BufferedReader br) throws IOException {
		while(true) {
			Line line;
			try{
				line = Eat(br);
			}catch(NullPointerException e) {
				return;
			}
			String parse = line.GetContents();
			if(parse.length() < 7)
				throw new IOException(parsingError);
			if(parse.charAt(0) == '(' && parse.charAt(2) == ',' && parse.charAt(4) == ',') {
				int subEnd;
				for(subEnd = 5; subEnd < parse.length(); subEnd++) {
					if(parse.charAt(subEnd) == ')')
						break;
				}
				int res = con.AddTNP(parse.charAt(1), parse.charAt(3), parse.substring(5, subEnd));
				if (res == -1)
					throw new IOException(hardConError);
			}
			else
				throw new IOException(parsingError);
		}
	}

}
