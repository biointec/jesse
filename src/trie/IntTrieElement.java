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

public class IntTrieElement extends TrieElement<Integer, Integer> {

	private TrieElement<Integer, Integer>[] childrenArray;
	private int max;
	private int min;

	public IntTrieElement(Integer value, int max) {
		super(value);
		this.max = max;
		this.min=max;
	}

	public TrieElement<Integer, Integer> addChild(Integer key, Integer value) {
		TrieElement<Integer, Integer> result = new IntTrieElement(value, max);
		if (childrenArray == null) {
			min = key;
			childrenArray = new IntTrieElement[max - min];
		}
		childrenArray[key - min] = result;
		result.parent = this;
		result.trie = this.trie;
		trie.increaseSize();
		return result;
	}

	public TrieElement<Integer, Integer> getChild(Integer key) {
		return key < min ? null : childrenArray[key - min];
	}

	public String toString() {
		// return "("+value+":"+children.toString()+")";
		String s = "";

		s += value;
		tabs += 1;
		for (int key = min; key < max; key++) {
			if (childrenArray[key - min] != null) {
				s += "\n";
				for (int i = 0; i < tabs; i++) {
					s += " ";
				}
				s += key;
				s += "\t";
				s += childrenArray[key - min].toString();
			}
		}
		tabs -= 1;
		return s;
	}

}
