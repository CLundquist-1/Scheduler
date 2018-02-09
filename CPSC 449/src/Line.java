
public class Line {
	public Line() {}
	public Line(int _contents) {
		contents = "" + (char)_contents;
	}
	public Line(String _contents) {
		contents = _contents.toLowerCase();
	}
	public Line(String _contents, boolean _eaten) {
		contents = _contents;
		eaten = _eaten;
	}
	private String contents = "";
	public boolean eaten = false;
	
	
	public String GetContents() {
		return contents;
	}
	
	public void SetContents(String _contents) {
		contents = _contents.toLowerCase();
	}
	
	public void SetContents(int _contents) {
		contents = "" + (char)_contents;
	}
}
