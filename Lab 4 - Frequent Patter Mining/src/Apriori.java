import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Apriori {
	/***
	 * The TRANSACTIONS 2-dimensional array holds the full data set for the lab
	 */
	static final int[][] TRANSACTIONS = new int[][] { { 1, 2, 3, 4, 5 },
			{ 1, 3, 5 }, { 2, 3, 5 }, { 1, 5 }, { 1, 3, 4 }, { 2, 3, 5 },
			{ 2, 3, 5 }, { 3, 4, 5 }, { 4, 5 }, { 2 }, { 2, 3 }, { 2, 3, 4 },
			{ 3, 4, 5 } };
	static final int supportThreshold = 2;

	public static void main(String[] args) {
		// TODO: Select a reasonable support threshold via trial-and-error. Can
		// either be percentage or absolute value.
		 List<ItemSet> r = apriori(TRANSACTIONS);
		 System.out.println("............");
		 for (int i = 0; i < r.size(); i++) {
			System.out.println(i+" "+Arrays.toString(r.get(i).getSet()));
		}
		 System.out.println("............");
	}

	public static List<ItemSet> apriori(int[][] transactions) {
		int k;
		List<ItemSet> result = new ArrayList<ItemSet>();
		Hashtable<ItemSet, Integer> frequentItemSets = generateFrequentItemSetsLevel1(transactions);
		for (k = 1; frequentItemSets.size() > 0; k++) {
			System.out.println("Finding frequent itemsets of length " + (k + 1)
					+ "…");
			frequentItemSets = generateFrequentItemSets(transactions,frequentItemSets);
			// TODO: add to list
			Iterator it = frequentItemSets.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				result.add((ItemSet) pair.getKey());
				it.remove();
			}
			
			System.out.println(" found " + frequentItemSets.size());
		}
		// TODO: create association rules from the frequent itemsets

		// TODO: return something useful
		return result;
	}

	private static Hashtable<ItemSet, Integer> generateFrequentItemSets(int[][] transactions, Hashtable<ItemSet, Integer> lowerLevelItemSets) {
		System.out.println("generateFrequentItemSets");
		// TODO: first generate candidate itemsets from the lower level itemsets
		// - join step
		Hashtable<ItemSet, Integer> candidates = new Hashtable<>();
		Iterator it1 = lowerLevelItemSets.entrySet().iterator();
		while (it1.hasNext()) {
			Map.Entry p1 = (Map.Entry) it1.next();
			Iterator<?> it2 = lowerLevelItemSets.entrySet().iterator();
//			System.out.println("Generate subsets of k-1");
			while (it2.hasNext()) {
				Map.Entry p2 = (Map.Entry) it2.next();
				if (!p1.getKey().equals(p2.getKey())) {
					ItemSet candidate = joinSets((ItemSet) p1.getKey(),
							(ItemSet) p2.getKey());
//					System.out.println("candidate set length = "+candidate.getSet().length);
					if (candidate.getSet().length > 0) {
						candidates.put(candidate,
								countSupport(candidate.getSet(), transactions));
					}
				}
//				it2.remove();
			}
//			it1.remove();
		}
		// TODO: then check if all subsets of the candidate item sets meet the
		// apriori property - prune step
		// generate all combinations of the candidates and check if they were
		// frequent in the previous step
		Hashtable<ItemSet, Integer> subsets = new Hashtable<>();
		Iterator<?> it3 = candidates.entrySet().iterator();
//		System.out.println("Generate subsets of k-1=");
		while (it3.hasNext()) {
			Map.Entry pair = (Map.Entry) it3.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());
			int[] s = getSet(pair);
			int k = s.length;
			for (int i = 0; i < k; i++) {
				int[] sub = new int[k - 1];
				System.out.print("[");
				for (int j = i + 1; j < k + i; j++) {
					sub[j % (k - 1)] = s[j % k];
					System.out.print(s[j % k] + ",");
				}
				System.out.print("]\n");
				Arrays.sort(sub);
				ItemSet item = new ItemSet(sub);
				if(!lowerLevelItemSets.containsKey(item)){
					candidates.remove(item);
				}
		
			}
			it3.remove();
		}
		
		/*
		 * TODO: now check the support for the candidates left after pruning and
		 * add only those that have enough support (i.e. support >=
		 * support_threshold) to the set
		 */
		Iterator it4 = subsets.entrySet().iterator();
		while(it4.hasNext()){
			System.out.println("it4");
			Map.Entry<ItemSet, Integer> pair = (Map.Entry) it4.next();
			int support = countSupport((int[])pair.getKey().getSet(), transactions);
			if(support < supportThreshold){
				subsets.remove(pair.getKey());
				System.out.println("remove");
			}
			it4.remove();
		}

		return subsets;
	}

	private static int[] getSet(Map.Entry pair) {
		return ((ItemSet) pair.getKey()).getSet();
	}

	private static ItemSet joinSets(ItemSet first, ItemSet second) {
		int[] firstSet = first.getSet();
		int[] secondSet = second.getSet();
		int lastIndex = firstSet.length - 1;
		int[] Ck = new int[firstSet.length + 1];

		if (firstSet[lastIndex] < secondSet[lastIndex]) {
			for (int i = 0; i < firstSet.length; i++) {
				if (i < lastIndex - 1 && firstSet[i] != secondSet[i]) {
					return new ItemSet(new int[0]);
				}
				Ck[i] = firstSet[i];
			}
			Ck[Ck.length - 1] = secondSet[lastIndex];
		} else {
			return new ItemSet(new int[0]);
		}
		return new ItemSet(Ck);
	}

	private static Hashtable<ItemSet, Integer> generateFrequentItemSetsLevel1(
			int[][] transactions) {
		System.out.println("generateFrequentItemSetsLevel1");
		Hashtable<ItemSet, Integer> sets = new Hashtable<>();
		for (int i = 0; i < transactions.length; i++) {
			for (int j = 0; j < transactions[i].length; j++) {
				ItemSet s = new ItemSet(new int[] { transactions[i][j] });
				if (sets.containsKey(s)) {
					int count = sets.get(s);
					sets.replace(s, count + 1);
				} else {
					sets.put(s, 1);
				}
			}

		}
		// TODO maybe use sets.forEach(Action a)
		Iterator it = sets.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pair = (Entry) it.next();
		ItemSet s = (ItemSet)pair.getKey();
			if ((int)pair.getValue() < supportThreshold) {
				sets.remove(s);
			}
		}
		return sets;
	}

	private static int countSupport(int[] itemSet, int[][] transactions) {
		// Assumes that items in ItemSets and transactions are both unique
		int count = 0;
		for (int i = 0; i < transactions.length; i++) {
			int[] transaction = transactions[i];

			int barrier = itemSet.length;
			for (int j = 0; j < transaction.length; j++) {
				for (int k = 0; k < itemSet.length; k++) {
					if (transaction[j] == itemSet[k]) {
						barrier--;
					}
				}
			}
			count += (barrier == 0) ? 1 : 0;
		}
		System.out.print("{ ");
		for (int i = 0; i < itemSet.length; i++) {
			System.out.print(itemSet[i]+" ");
		}
		System.out.println("} Count supoort: "+count);
		return count;
	}

}
