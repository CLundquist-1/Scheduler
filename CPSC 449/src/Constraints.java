import java.io.IOException;
import java.util.ArrayList;

public class Constraints {
	public String name;
	public ArrayList<Integer> fpa;
	public ArrayList<Integer> fm;
	public ArrayList<Integer> tnt;
	public int[][] mp;
	public ArrayList<Integer> tnp;
	
	public Constraints() {
		name = "";
		fpa = new ArrayList<Integer>();
		fm = new ArrayList<Integer>();
		tnt = new ArrayList<Integer>();
		mp = new int[8][8];
		tnp = new ArrayList<Integer>();
	}
	
	public int AddFPA(char mach, char task) throws IOException {
		int temp1, temp2;
		if(fpa.size() == 16)
			return 1;
		temp1 = CharToMach(mach);
		if(temp1 == -1)
			return temp1;
		temp2 = CharToTask(task);
		if(temp2 == -1)
			return temp2;
		for(int i = 0; i < fpa.size(); i += 2) {
			if(temp1 == fpa.get(i) || temp2 == fpa.get(i+1))
				return 2;
		}
		fpa.add(temp1);
		fpa.add(temp2);
		return 0;
	}
	
	public int AddFM(char mach, char task) throws IOException {
		int temp1, temp2;
		temp1 = CharToMach(mach);
		if(temp1 == -1)
			return temp1;
		temp2 = CharToTask(task);
		if(temp2 == -1)
			return temp2;
		fm.add(temp1);
		fm.add(temp2);
		return 0;
	}
	
	public int AddTNT(char task1, char task2) throws IOException {
		int temp1, temp2;
		temp1 = CharToTask(task1);
		if(temp1 == -1)
			return temp1;
		temp2 = CharToTask(task2);
		if(temp2 == -1)
			return temp2;
		tnt.add(temp1);
		tnt.add(temp2);
		return 0;
	}
	
	public int AddMP(String pen, int row, int col) throws IOException {
		int temp = StringToPenalty(pen);
		if(temp == -1)
			return temp;
		mp[row][col] = temp;
		return 0;
	}
	
	public int AddTNP(char task1, char task2, String penalty) throws IOException {
		int temp1, temp2;
		temp1 = CharToTask(task1);
		if(temp1 == -1)
			return temp1;
		temp2 = CharToTask(task2);
		if(temp2 == -1)
			return temp2;
		int temp3 = StringToPenalty(penalty);
		tnp.add(temp1);
		tnp.add(temp2);
		tnp.add(temp3);
		return 0;
	}
	
	
	
	
	public int CharToMach(char mach) {
		int m = mach - '1';
		if(m >= 0 && m < 9)
			return m;
		return -1;
	}
	
	public int CharToTask(char task) {
		int t = task - 'a';
		if(t >= 0 && t < 9)
			return t;
		return -1;
	}
	
	public int StringToPenalty(String penalty) throws IOException {
		int temp;
		try{
			temp = Integer.parseInt(penalty);
		}catch(NumberFormatException e) {
			if(penalty.equals(""))
				throw new IOException(Main.parsingError);
			throw new IOException(Main.numberError);
		}
		return temp;
	}
	
	public char MachToChar(int march) {
		return Integer.toString(march + 1).charAt(0);
	}
	
	public char TaskToChar(int task) {
		return (char)(task + 'A');
	}
	
	
	
	
	
	
	
	
	public String printConstraints() {
		return printName() + "\n\n" + printFPA() + "\n\n" + printFB() + "\n\n" + printTNT() + "\n\n" + printMP() + "\n\n" + printTNP() + "\n\n";
	}
	
	private String printName() {
		return name;
	}
	
	private String printFPA() {
		String ret = Main.fpaKey + "\n";
		ret += printMT(fpa, 2);
		return ret;
	}
	
	private String printFB() {
		String ret = Main.fmKey + "\n";
		ret += printMT(fm, 2);
		return ret;
	}
	
	private String printTNT() {
		String ret = Main.tnKey + "\n";
		ret += printTT(tnt, 2);
		return ret;
	}
	
	private String printMP() {
		String ret = Main.mpKey + "\n";
		for(int i = 0; i < mp.length ; i++) {
			for(int j = 0; j < mp[i].length; j++) {
				if(j != 0)
					ret += " ";
				ret += mp[i][j];	
			}
			ret += "\n";
		}
		return ret;
	}
	
	private String printTNP() {
		String ret = Main.tnpKey + "\n";
		ret += printTTP(tnp, 3);
		return ret;
	}
	
	private String printArrayList(ArrayList<Integer> lst, int n) {
		int i = 0;
		String ret = "";
		for(int t : lst) {
			if(i==0)
				ret += "(";
			else if(i == n)
				ret += ")\n";
			else
				ret += ",";
			ret += t;
			i++;
		}
		return ret;
	}
	
	private String printTT(ArrayList<Integer> lst, int n) {
		n = n - 1;
		int i = 0;
		String ret = "";
		for(int t : lst) {
			if(i==0)
				ret += "(";
			ret += TaskToChar(t);
			if(i == n) {
				ret += ")\n";
				i = 0;
			}
			else {
				ret += ",";
				i++;
			}
		}
		return ret;
	}
	
	private String printTTP(ArrayList<Integer> lst, int n) {
		n = n - 1;
		int i = 0;
		int m = 1;
		String ret = "";
		for(int t : lst) {
			if(i==0)
				ret += "(";
			if(m < 3) {
				ret += TaskToChar(t);
				m++;
			}
			else {
				ret += t;
				m = 1;
			}
			if(i == n) {
				ret += ")\n";
				i = 0;
			}
			else {
				ret += ",";
				i++;
			}
		}
		return ret;
	}
	
	private String printMT(ArrayList<Integer> lst, int n) {
		n = n-1;
		int i = 0;
		boolean flip = true;
		String ret = "";
		for(int t : lst) {
			if(i==0)
				ret += "(";
			if(flip)
				ret += MachToChar(t);
			else
				ret += TaskToChar(t);
			if(i == n) {
				ret += ")\n";
				i = 0;
			}
			else {
				ret += ",";
				i++;
			}
			flip = !flip;
		}
		return ret;
	}
}
