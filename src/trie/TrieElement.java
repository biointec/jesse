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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TrieElement<E, F> {

	private Map<E, TrieElement<E, F>> children;
	protected F value;
	protected TrieElement<E, F> parent;
	protected static int tabs = 0;
	Trie<E, F> trie;

	public TrieElement(F value) {
		this.value = value;
		children = null;
	}

	public F getValue() {
		return value;
	}

	public TrieElement<E, F> addChild(E key, F value) {
		TrieElement<E, F> result = new TrieElement<>(value);
		if (children == null) {
			children = new HashMap<>();
		}
		children.put(key, result);
		result.parent = this;
		result.trie = this.trie;
		trie.increaseSize();
		return result;
	}

	public TrieElement<E, F> getChild(E key) {
		if (children == null)
			return null;
		return children.get(key);
	}

	public TrieElement<E, F> getParent() {
		return parent;
	}

	// public TrieElement<E, F> search(Iterator<E> keys) {
	// if (!keys.hasNext()) {
	// return this;
	// } else {
	// E key = keys.next();
	// if (children == null) {
	// return null;
	// } else if (children.containsKey(key)) {
	// return children.get(key).search(keys);
	// } else {
	// return null;
	// }
	// }
	// }

	// public TrieElement<E, F> searchClosest(Iterator<E> keys) {
	// if (!keys.hasNext()) {
	// return this;
	// } else {
	// E key = keys.next();
	// if (children == null) {
	// return this;
	// } else if (children.containsKey(key)) {
	// return children.get(key).searchClosest(keys);
	// } else {
	// return this;
	// }
	// }
	// }

	// public List<F> getList() {
	// LinkedList<F> result = new LinkedList<>();
	// TrieElement<E, F> current = this;
	// while (current != null) {
	// result.addFirst(current.value);
	// current = current.parent;
	// }
	// return result;
	// }

	// public void insert(Iterator<E> keys, F value) {
	// if (!keys.hasNext()) {
	// this.value = value;
	// } else {
	// E key = keys.next();
	// if (children.containsKey(key)) {
	// children.get(key).insert(keys, value);
	// } else {
	// TrieElement<E, F> t = new TrieElement<>(null);
	// children.put(key, t);
	// t.insert(keys, value);
	// }
	// }
	// }

	public String toString() {
		// return "("+value+":"+children.toString()+")";
		String s = "";

		s += value;

		tabs += 1;
		if (children != null) {
			for (E key : children.keySet()) {
				s += "\n";
				for (int i = 0; i < tabs; i++) {
					s += " ";
				}
				s += key;
				s += "\t";
				s += children.get(key).toString();
			}
		}
		tabs -= 1;
		return s;
	}
}
