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
	static final int supportThreshold = 4;
	static final double min_conf = 70;

	public static void main(String[] args) {
		// TODO: Select a reasonable support threshold via trial-and-error. Can
		// either be percentage or absolute value.
		 List<AssocaitionRule> associationrules = apriori(TRANSACTIONS);
		 System.out.println("Rule\t\tminSupport: "+supportThreshold+"\tminConfidence: "+min_conf);
		 System.out.println("....................................................");
		 for (int i = 0; i < associationrules.size(); i++) {
			 if(associationrules.get(i).getConfidence() >= min_conf){
				 System.out.println(associationrules.get(i));
			 }
		}
		 System.out.println("....................................................");
	}

	@SuppressWarnings({ "unchecked" })
	public static List<AssocaitionRule> apriori(int[][] transactions) {
		Hashtable<ItemSet, Integer> result = new Hashtable<ItemSet, Integer>();
		Hashtable<ItemSet, Integer> frequentItemSets = generateFrequentItemSetsLevel1(transactions);
		
		while(frequentItemSets.size() > 0) {
			frequentItemSets = generateFrequentItemSets(transactions,
					frequentItemSets);
			// TODO: add to list
			result.putAll((Map<ItemSet, Integer>) frequentItemSets);
		}
		// TODO: create association rules from the frequent itemsets
		List<AssocaitionRule> rules = new ArrayList<AssocaitionRule>();
		Iterator<?> it = result.entrySet().iterator();
		while (it.hasNext()) {
			Entry<ItemSet, Integer> pair = (Entry<ItemSet, Integer>) it.next();
			int[] set = getSet(pair);
			// generate all subsets and generate rules for each of them
			ArrayList<Integer> list = new ArrayList<>();
			for (int i = 0; i < set.length; i++) {
				list.add(set[i]);
			}
			int max = 1 << list.size(); // there are 2 power n
			for (int i = 1; i < max-1; i++) {
				ArrayList<Integer> subset1 = new ArrayList<Integer>();
				ArrayList<Integer> subset2 = new ArrayList<Integer>();
				for (int j = 0; j < list.size(); j++) {
					if (((i >> j) & 1) == 1) {
						subset1.add(list.get(j));
					}else{
						subset2.add(list.get(j));
					}
				}
				int[] A = subset1.stream().mapToInt(l -> l).toArray();
				int[] B = subset2.stream().mapToInt(l -> l).toArray();
				int supportF = countSupport(A, transactions);
				AssocaitionRule rule = new AssocaitionRule(A, B, set, (double)(int)pair.getValue()/TRANSACTIONS.length*100, ((double)((int)pair.getValue())/supportF)*100);
				rules.add(rule);
//				System.out.println(Arrays.toString(A)+" => "+Arrays.toString(B) + " confidence = "+pair.getValue()+"/"+supportF+" = "+((double)((int)pair.getValue())/supportF)*100+" %");
			}

			it.remove();
		}
		// TODO: return something useful
		return rules;
	}

	@SuppressWarnings("unchecked")
	private static Hashtable<ItemSet, Integer> generateFrequentItemSets(
			int[][] transactions, Hashtable<ItemSet, Integer> lowerLevelItemSets) {
		// System.out.println("generateFrequentItemSets");
		// TODO: first generate candidate itemsets from the lower level itemsets
		// - join step
		Hashtable<ItemSet, Integer> candidates = generateCandidates(
				transactions, lowerLevelItemSets);
		// TODO: then check if all subsets of the candidate item sets meet the
		// apriori property - prune step
		// generate all combinations of the candidates and check if they were
		// frequent in the previous step
		Iterator<?> it3 = candidates.entrySet().iterator();
		while (it3.hasNext()) {
			Entry<ItemSet, Integer> pair = (Entry<ItemSet, Integer>) it3.next();
			int[] s = getSet(pair);
			int k = s.length;
			boolean foundSubSet = false;
			// generate all subsets of this item
			for (int i = 0; i < k; i++) {
				int[] sub = new int[k - 1];

				for (int j = i + 1; j < k + i; j++) {
					sub[j % (k - 1)] = s[j % k];
				}
				Arrays.sort(sub);
				ItemSet item = new ItemSet(sub);
				if (lowerLevelItemSets.containsKey(item)) {
					foundSubSet = true;
				}
			}
			if (!foundSubSet) {
				candidates.remove(pair.getKey());
			}
		}
		/*
		 * TODO: now check the support for the candidates left after pruning and
		 * add only those that have enough support (i.e. support >=
		 * support_threshold) to the set
		 */
		Hashtable<ItemSet, Integer> result = new Hashtable<>();
		Iterator<?> it4 = candidates.entrySet().iterator();
		while (it4.hasNext()) {
			Entry<ItemSet, Integer> pair = (Entry<ItemSet, Integer>) it4.next();
			int support = pair.getValue();
			if (support >= supportThreshold) {
				result.put(pair.getKey(), support);
			}
			it4.remove();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static Hashtable<ItemSet, Integer> generateCandidates(
			int[][] transactions, Hashtable<ItemSet, Integer> lowerLevelItemSets) {

		Hashtable<ItemSet, Integer> candidates = new Hashtable<>();

		Iterator<?> it1 = lowerLevelItemSets.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<ItemSet, Integer> p1 = (Entry<ItemSet, Integer>) it1.next();
			Iterator<?> it2 = lowerLevelItemSets.entrySet().iterator();
			while (it2.hasNext()) {
				Entry<ItemSet, Integer> p2 = (Entry<ItemSet, Integer>) it2.next();
				if (!p1.getKey().equals(p2.getKey())) {
					ItemSet candidate = joinSets((ItemSet) p1.getKey(),
							(ItemSet) p2.getKey());
					if (candidate.getSet().length > 0) {
						candidates.put(candidate,
								countSupport(candidate.getSet(), transactions));
					}
				}
			}
		}
		return candidates;
	}

	private static int[] getSet(Entry<?, ?> pair) {
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

	@SuppressWarnings("unchecked")
	private static Hashtable<ItemSet, Integer> generateFrequentItemSetsLevel1(
			int[][] transactions) {
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
		Iterator<?> it = sets.entrySet().iterator();
		while (it.hasNext()) {
			Entry<ItemSet, Integer> pair = (Entry<ItemSet, Integer>) it.next();
			ItemSet s = (ItemSet) pair.getKey();
			if ((int) pair.getValue() < supportThreshold) {
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
		return count;
	}

}
