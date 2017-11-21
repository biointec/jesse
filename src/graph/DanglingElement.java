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

/**
 * Element in a DanglingList. 
 * 
 * @author Ine Melckenbeeck
 *
 * @param <E>
 */
public class DanglingElement<E extends Comparable<E>> {

	private DanglingElement<E> previous;
	private DanglingElement<E> next;
	private E value;
	private DanglingList<E> list;

	/**
	 * Creates a new DanglingElement with a value, but without neighbors in a
	 * list.
	 * 
	 * @param element
	 */
	public DanglingElement(E element, DanglingList<E> list) {
		this.value = element;
		this.previous = null;
		this.next = null;
		this.list = list;
	}

	/**
	 * 
	 * @return The previous element in the list.
	 */
	public DanglingElement<E> getPrevious() {
		return previous;
	}

	void setPrevious(DanglingElement<E> prev) {
		this.previous = prev;
	}

	/**
	 * @return The next element in the list.
	 */
	public DanglingElement<E> getNext() {
		return next;
	}

	void setNext(DanglingElement<E> next) {
		this.next = next;
	}

	/**
	 * 
	 * @return This DanglingElement's value.
	 */
	public E getValue() {
		return value;
	}

	void remove() {
		if (previous != null) {
			previous.setNext(next);
		}
		if (next != null) {
			next.setPrevious(previous);
		}
	}

	void restore() {
		if (previous != null) {
			previous.setNext(this);
		}
		if (next != null) {
			next.setPrevious(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DanglingElement<E> other = (DanglingElement<E>) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	
	DanglingList<E> getList() {
		return list;
	}
}
