package codegenerating;

public interface TaskMonitor {
	public void setProgress(double progress);
	public void setStatusMessage(String msg);
	public void setTitle(String title);
}
