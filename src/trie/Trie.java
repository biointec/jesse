package trie;

/*
 * #%L
 * Jesse
 * %%
 * Copyright (C) 2017 - 2018 Intec/UGent - Ine Melckenbeeck
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

//import net.sourceforge.sizeof.SizeOf;

public class Trie <E,F>{

	protected TrieElement<E,F> root;
	protected int size;
	
	public Trie(){
		root = new TrieElement<E,F>(null);
		size = 0;
		root.trie=this;
	}
	
	public F search(Iterable<E> keys) {
		TrieElement<E,F> result = root;
		Iterator<E> it = keys.iterator();
		while(it.hasNext()&& result!=null) {
			result = result.getChild(it.next());
		}
		
		
//		TrieElement<E,F> result = root.search(keys.iterator());
		return result==null?null:result.getValue();
	}
	
	public TrieElement<E,F> getRoot(){
		return root;
	}
	
	void increaseSize() {
		size++;
//		if(size%10000==0) {
//			System.out.print(size);
//			System.out.print('\t');
////			System.out.print(SizeOf.humanReadable(SizeOf.deepSizeOf(this)));
//			System.out.println();
//		}
	}
	
	public void insert(Iterable<E> keys, F value) {
		
	}
	
	public String toString() {
		return root.toString();
	}
	
}
