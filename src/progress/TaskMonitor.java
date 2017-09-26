package progress;

/**
 * Lightweight task monitor interface to allow methods to report their 
 * progress to a client. 
 * 
 * @author Thomas Van Parys
 *
 */
public interface TaskMonitor {
	
	/**
	 * 
	 * @param title general description about the running task
	 */
	public void setTitle(String title);
	
	/**
	 * 
	 * @param progress the progress of your task on a scale from 0 to 1
	 */
	public void setProgress(double progress);
	
	/**
	 * 
	 * @param msg information about which part of the task is currently in progress 
	 */
	public void setStatusMessage(String msg);
	
	
	/**
	 * 
	 * @return true if this task got cancelled
	 */
	public boolean isCancelled();
}
