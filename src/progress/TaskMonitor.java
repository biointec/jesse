package progress;

/*
 * #%L
 * Jesse
 * %%
 * Copyright (C) 2017 Intec/UGent - Ine Melckenbeeck
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
