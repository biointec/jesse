package graph;

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

import java.util.Iterator;

/**
 * Iterator for DanglingList.
 * @author imelcken
 *
 * @param <E>
 */
public class DanglingIterator<E extends Comparable<E>> implements Iterator<E> {
	
	private DanglingElement<E> current;
//	private DanglingList<E> dl;
	
	/**
	 * Creates a new iterator for the given list.
	 * @param dl
	 */
	public DanglingIterator(DanglingList<E> dl){
//		this.dl = dl;
		current=dl.getHead();
	}

	@Override
	public boolean hasNext() {
		if(current == null){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public E next() {
		E value =  current.getValue();
		current = current.getNext();
		return value;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
