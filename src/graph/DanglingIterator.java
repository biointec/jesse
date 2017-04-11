package graph;

import java.util.Iterator;

public class DanglingIterator<E extends Comparable<E>> implements Iterator<E> {
	
	private DanglingElement<E> current;
	private DanglingList<E> dl;
	
	public DanglingIterator(DanglingList<E> dl){
		this.dl = dl;
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
	
	public static void main(String[]args){
		DanglingList<Integer> a = new DanglingList<>();
		for(int i=0;i<10;i++){
			a.add(i);
		}
		for(int i:a){
			System.out.println(i);
		}
	}

}
