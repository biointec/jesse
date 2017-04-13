package graph;

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
	
}
