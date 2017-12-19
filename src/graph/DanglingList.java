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
import java.util.Stack;

/**
 * Doubly linked list with dangling links, i.e. elements that are removed from
 * the list will keep their outgoing links. This allows to reinsert those links
 * in the same place in the list quickly.
 * 
 * @author Ine Melckenbeeck
 *
 * @param <E>
 *            The type of elements in the list.
 */
public class DanglingList<E extends Comparable<E>> implements Comparable<DanglingList<E>>, Iterable<E> {

	private DanglingElement<E> head;
	private DanglingElement<E> tail;
	private int size;
	private int hashCode;
	private long longHashCode;
	private boolean ready;
	private static int factor = 97;
	private int ownFactor = 97;
	private E maximum;

	/**
	 * Creates a new, empty DanglingList.
	 */
	public DanglingList() {
		head = null;
		tail = null;
		size = 0;
		ready = false;
		maximum=null;
	}

	/**
	 * Creates a new DanglingList containing all elements of an Iterable.
	 * 
	 * @param elements
	 */
	public DanglingList(Iterable<E> elements) {
		this();
		addAll(elements);
	}
	
	public DanglingList(E[] elements) {
		this();
		addAll(elements);
	}

	/**
	 * Adds all elements from the Iterable to the DanglingList in the order they
	 * are returned by the iterator.
	 * 
	 * @param elements
	 */
	public void addAll(Iterable<E> elements) {
		for (E i : elements) {
			add(i);
		}
		ready = false;
	}
	public void addAll(E[] elements) {
		for (E i : elements) {
			add(i);
		}
		ready = false;
	}

	/**
	 * Adds an element to the end the DanglingList.
	 * 
	 * @param element
	 *            The element to be added.
	 */
	public void add(E element) {
		DanglingElement<E> e = new DanglingElement<>(element, this);
		if (head == null) {
			head = e;
		} else {
			tail.setNext(e);
			e.setPrevious(tail);
		}
		if(maximum == null) {
			maximum=element;
		}else if(maximum.compareTo(element)<0) {
			maximum = element;
		}
		tail = e;
		size++;
		ready = false;
	}

	/**
	 * Adds an element to the start of the DanglingList.
	 * 
	 * @param element
	 *            The element to be added.
	 */
	public void addFirst(E element) {
		DanglingElement<E> e = new DanglingElement<>(element, this);
		if (tail == null) {
			tail = e;
		} else {
			head.setPrevious(e);
			e.setNext(head);
		}
		head = e;
		size++;
		ready = false;
	}

	/**
	 * Removes an element from the list. The element still keeps its pointers to
	 * its neighbors.
	 * 
	 * @param element
	 */
	public void remove(DanglingElement<E> element) {
		if (element.getList() == this) {
			if (head == element) {
				head = element.getNext();
			}
			if (tail == element) {
				tail = element.getPrevious();
			}
			element.remove();
			size--;
			ready = false;
		}
	}

	/**
	 * Restores an element to its original position in the list.
	 * 
	 * @param element
	 */
	public void restore(DanglingElement<E> element) {
		if (element.getList() == this) {
			if (element.getNext() == head) {
				head = element;
			}
			if (element.getPrevious() == tail) {
				tail = element;
			}
			element.restore();
			size++;
			ready = false;
		}
	}

	/**
	 * 
	 * @return The first element of the list.
	 */
	public DanglingElement<E> getHead() {
		return head;
	}

	/**
	 * 
	 * @return The last element of the list.
	 */
	public DanglingElement<E> getTail() {
		return tail;
	}

	/**
	 * 
	 * @return The number of elements in the list.
	 */
	public int size() {
		return size;
	}

	/**
	 * 
	 * @return true if there are no elements in the list.
	 */
	public boolean isEmpty() {
		return head == null;
	}

	@Override
	public String toString() {
		DanglingElement<E> a = head;
		String result = "";
		while (a != null) {
			result += ", ";
			result += a.getValue().toString();
			a = a.getNext();
		}
		if (result.equals(""))
			return "[]";
		return "[" + result.substring(2) + "]";
	}

	/**
	 * Empties the list.
	 */
	public void clear() {
		head = null;
		tail = null;
		size = 0;
		ready = false;
	}

	/**
	 * Assuming the list is ordered, adds the given element in the place to keep
	 * the ordering. If the compareTo method finds the new element to be equal
	 * to an element already present in the list, the new element will be added
	 * before it.
	 * 
	 * @param element
	 */
	public void addInOrder(E element) {
		DanglingElement<E> a = head;
		if (isEmpty()) {
			add(element);
		} else if (element.compareTo(head.getValue()) <= 0) {
			addFirst(element);
		} else if (element.compareTo(tail.getValue()) > 0) {
			add(element);
		} else {
			DanglingElement<E> result = new DanglingElement<>(element, this);
			while (a != null && a.getValue().compareTo(element) < 0) {
				a = a.getNext();
			}
			a.getPrevious().setNext(result);
			result.setPrevious(a.getPrevious());
			a.setPrevious(result);
			result.setNext(a);
			size++;

			ready = false;
		}
	}

	@Override
	public int hashCode() {
		if (!ready || ownFactor!=factor) {
			ownFactor = factor;
			hashCode = 0;
			longHashCode = 0l;
			DanglingElement<E> e = head;
			while (e != null) {
				hashCode *= factor;
				hashCode += e.hashCode();
				longHashCode *= factor;
				longHashCode += e.hashCode();
				e = e.getNext();
			}
			ready = true;
		}
		return hashCode;
	}
	public long longHashCode() {
		if (!ready || ownFactor!=factor) {
			ownFactor = factor;
			hashCode = 0;
			longHashCode = 0l;
			DanglingElement<E> e = head;
			while (e != null) {
				hashCode *= factor;
				hashCode += e.hashCode();
				longHashCode *= factor;
				longHashCode += e.hashCode();
				e = e.getNext();
			}
			ready = true;
		}
		return longHashCode;
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
		DanglingList<E> other = (DanglingList<E>) obj;
		if (size != other.size)
			return false;
		if (hashCode != other.hashCode)
			return false;
		if (head == null) {
			if (other.head != null)
				return false;
		} else {
			DanglingElement<E> de1 = head;
			DanglingElement<E> de2 = other.head;
			while (de1 != null) {
				if (!de1.equals(de2)) {
					return false;
				}
				de1 = de1.getNext();
				de2 = de2.getNext();
			}
		}
		return true;
	}

	/**
	 * Sets the multiplication factor used in calculating this list's hash code.
	 * 
	 * @param i
	 *            The new multiplication factor.
	 */
	public static void setFactor(int i) {
		factor = i;
	}
	
	public Stack<DanglingElement<E>> crossSection(DanglingList<E> l){
		Stack<DanglingElement<E>> removed = new Stack<DanglingElement<E>>();
		DanglingElement<E> a = this.head;
		DanglingElement<E> b = l.head;
		while (a != null && b != null) {
			if (a.getValue().compareTo(b.getValue())>0) {
				b = b.getNext();
			} else if (a.getValue().compareTo(b.getValue())<0) {
				removed.add(a);
				remove(a);
				a = a.getNext();
			} else {
				a = a.getNext();
				b = b.getNext();
			}
		}
		if (b == null) {
			while (a != null) {
				removed.add(a);
				remove(a);
				a = a.getNext();
			}
		}
		return removed;
	}

	@Override
	public int compareTo(DanglingList<E> o) {
		return this.hashCode()-o.hashCode();
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return new DanglingIterator<E>(this);
	}
	
	public DanglingList<E> combine(DanglingList<E> other){
		DanglingElement<E> a = getHead();
		DanglingElement<E> b = other.getHead();
		DanglingList<E> result = new DanglingList<>();
		while(a!=null && b != null) {
			if(a.getValue().compareTo(b.getValue())<=0) {
				result.add(a.getValue());
				a=a.getNext();
			}else {
				result.add(b.getValue());
				b=b.getNext();
			}
		}
		while(a!=null) {
			result.add(a.getValue());
			a=a.getNext();
		}
		while(b!=null) {
			result.add(b.getValue());
			b=b.getNext();
		}
		return result;
	}
	
	public DanglingList<E> difference(DanglingList<E> other){
		DanglingElement<E> a = getHead();
		DanglingElement<E> b = other.getHead();
		DanglingList<E> result = new DanglingList<>();
		while(a!=null && b != null) {
			if(a.getValue().compareTo(b.getValue())>0) {
				b=b.getNext();
			}else if(a.getValue().compareTo(b.getValue())<0) {
				result.add(a.getValue());
				a=a.getNext();
			}else {
				a=a.getNext();
				b=b.getNext();
			}
		}
		while(a!=null) {
			result.add(a.getValue());
			a=a.getNext();
		}
		return result;
	}
	
	public boolean contains(E e) {
		for (E i : this) {
			if(i.equals(e)) {
				return true;
			}
		}
		return false;
	}
	
//	public static void main(String[]args){
//		DanglingList<Integer> a = new DanglingList<Integer>();
//		for(int i=0;i<20;i+=2){
//			a.addInOrder(i);
//		}
//		DanglingList<Integer> b = new DanglingList<Integer>();
//		for(int i=0;i<20;i+=3){
//			b.addInOrder(i);
//		}
//		List<DanglingElement<Integer>> rest = a.crossSection(b);
//		System.out.println(rest);
//		System.out.println(a);
//		for(DanglingElement<Integer> element:rest){
//			a.restore(element);
//		}
//		System.out.println(a);
//	}
}
