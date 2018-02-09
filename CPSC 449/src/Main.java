
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
	
	static boolean[] chosen = new boolean[8];
	static int[] min = new int[8];
	static int[] max = new int[8];
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		con = new Constraints();
		
		InitializeState(cState);
		InitializeState(tState);
		
		BufferedWriter out;
		
		try {
			out = new BufferedWriter(new FileWriter(args[1]));
			try {
				ParseInput(args[0]);
			} catch(IOException e) {
				//System.out.println(e.getMessage());				//Print to the console instead
				out.write(e.getMessage());
				out.close();
				return;
			}
			if(!PlaceForced()) {
				//System.out.println(noSolution);					//Print to the console instead
				out.write(noSolution);
				out.close();
				return;
			}
			SetMinMax();
			BruteForce();
			out.write(SolutionToString2());
			out.close();
		} catch(IOException e) {
			System.out.println("Please use a valid output file name");
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println(programUse);						//Print to the console instead
			//out.write(programUse);
		}
		
		
		////////////////////////Print Statements To Help Test//////////////////////////////////
		//System.out.println(con.printConstraints()); 	//This will print out everything that was parsed in a neat order
		System.out.println(SolutionToString());			//This will print out the current generated solution held in BestState and BestPenalty
		//System.out.println(StateToString(cState) + " Penalty: " + cPenalty);	//Prints the current state which in this situation will only print the starting values (the set up after forced partial assignment)
		
		
		
		//Remember[row][column] || [mach][task]
		//System.out.println(con.mp[0][1]);
	}
	
	
	
	
	////////////////////////////////////////////////////////////Initialization///////////////////////////////////////////////////////
	public static void InitializeState(int[] state) {
		for(int i = 0; i < state.length; i++)
			state[i] = -1;
	}
	
	public static void SetMinMax() {
		for(int i = 0; i < max.length; i++) {
			if(cState[i] == -1) {
				min[i] = 0;
				max[i] = 8;
			}
			else {
				min[i] = cState[i];
				max[i] = cState[i] + 1;
			}
		}
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
			chosen[mach] = true;
			cPenalty += con.mp[mach][task];
			cPenalty += CheckNearPenalty(task, cState[modulo(mach+1,8)]);
			cPenalty += CheckNearPenalty(task, cState[modulo(mach-1,8)]);
		}
		return true;
	}
	
	
	
	/////////////////////////////////////////////////////////////////Solution Algorithms///////////////////////////////////////////////////
	public static void BruteForce() {
		for(int i = min[0]; i < max[0]; i++) {
			if(!CheckForbidden(0, i))
				continue;
			int iPenalty = con.mp[0][i];
			//tPenalty += iPenalty;
			for(int j = min[1]; j < max[1]; j++) {
				if(j == i)
					continue;
				if(!CheckForbidden(1, j))
					continue;
				if(!CheckNearHard(j, i))
					continue;
				int jPenalty = con.mp[1][j];
				jPenalty += CheckNearPenalty(j,i);
				//tPenalty += jPenalty;
				for(int k = min[2]; k < max[2]; k++) {
					if(k == j || k == i)
						continue;
					if(!CheckForbidden(2, k))
						continue;
					if(!CheckNearHard(k, j))
						continue;
					int kPenalty = con.mp[2][k];
					kPenalty += CheckNearPenalty(k,j);
					//tPenalty += kPenalty;
					for(int l = min[3]; l < max[3]; l++) {
						if(l == k || l == j || l == i)
							continue;
						if(!CheckForbidden(3, l))
							continue;
						if(!CheckNearHard(l, k))
							continue;
						int lPenalty = con.mp[3][l];
						lPenalty += CheckNearPenalty(l,k);
						//tPenalty += lPenalty;
						for(int m = min[4]; m < max[4]; m++) {
							if(m == l || m == k || m == j || m == i)
								continue;
							if(!CheckForbidden(4, m))
								continue;
							if(!CheckNearHard(m, l))
								continue;
							int mPenalty = con.mp[4][m];
							mPenalty += CheckNearPenalty(m,l);
							//tPenalty += mPenalty;
							for(int n = min[5]; n < max[5]; n++) {
								if(n == m || n == l || n == k || n == j || n == i)
									continue;
								if(!CheckForbidden(5, n))
									continue;
								if(!CheckNearHard(n, m))
									continue;
								int nPenalty = con.mp[5][n];
								nPenalty += CheckNearPenalty(n,m);
								//tPenalty += nPenalty;
								for(int o = min[6]; o < max[6]; o++) {
									if(o == n || o == m || o == l || o == k || o == j || o == i)
										continue;
									if(!CheckForbidden(6, o))
										continue;
									if(!CheckNearHard(o, n))
										continue;
									int oPenalty = con.mp[6][o];
									oPenalty += CheckNearPenalty(o,n);
									//tPenalty += oPenalty;
									for(int p = min[7]; p < max[7]; p++) {
										if(p == o || p == n || p == m || p == l || p == k || p == j || p == i)
											continue;
										if(!CheckForbidden(7, p))
											continue;
										if(!CheckNearHard(p, o))
											continue;
										if(!CheckNearHard(p, i))
											continue;
										int pPenalty = con.mp[7][p];
										pPenalty += CheckNearPenalty(p,o);
										pPenalty += CheckNearPenalty(p,i);
										tPenalty += iPenalty;
										tPenalty += jPenalty;
										tPenalty += kPenalty;
										tPenalty += lPenalty;
										tPenalty += mPenalty;
										tPenalty += nPenalty;
										tPenalty += oPenalty;
										tPenalty += pPenalty;
										if(tPenalty < bestPenalty) {
											bestPenalty = tPenalty;
											bestState = new int[]{i, j, k, l, m, n, o, p};
										}
										tPenalty -= iPenalty;
										tPenalty -= jPenalty;
										tPenalty -= kPenalty;
										tPenalty -= lPenalty;
										tPenalty -= mPenalty;
										tPenalty -= nPenalty;
										tPenalty -= oPenalty;
										tPenalty -= pPenalty;
										//tPenalty -= pPenalty;
									}
									//tPenalty -= oPenalty;
								}
								//tPenalty -= nPenalty;
							}
							//tPenalty -= mPenalty;
						}
						//tPenalty -= lPenalty;
					}
					//tPenalty -= kPenalty;
				}
				//tPenalty -= jPenalty;
			}
			//tPenalty -= iPenalty;
		}
	}
	
	
	
	
	
	
	/////////////////////////////////////////////////Checks///////////////////////////////////////////////////////////////////////////
	
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
		int counter = 0;
		for(int i = 0; i < con.tnp.size(); i ++) {
			if(counter == 2) {
				counter = 0;
				continue;
			}
			if(con.tnp.get(i) == task1) {
				if(counter == 1) {
					if(con.tnp.get(i-1) == task2)
						return con.tnp.get(i+1);
				}
				else {
					if(con.tnp.get(i+1) == task2)
						return con.tnp.get(i+2);
				}
			}
			counter++;
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
	
	
	public static String SolutionToString() {
		return StateToString(bestState) + " Penalty: " + bestPenalty;
	}
	
	public static String StateToString2(int[] state) {
		String ret = "\"Solution\"";
		for(int i = 0; i < state.length; i++) {
			ret += " " + con.TaskToChar(state[i]);
		}
		return ret;
	}
	
	
	public static String SolutionToString2() {
		return StateToString2(bestState) + "\"; Quality:\" " + bestPenalty;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////////////////////////////////////////////////Parsing Steps (in order)////////////////////////////////////////////////
	
	
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
				else if(j == parse.length()-1) {
					int res = con.AddMP(parse.substring(subStart, j+1),i, count);
					if(res == -1)
						throw new IOException(numberError);
					count++;
					subStart = j+1;
				}
			}
			if(count != 8)
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
