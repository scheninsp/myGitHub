import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Util {
	
	public static void PrintVector(Double[] v) {
		for(int j=0; j<v.length; j++) 
			System.out.print(v[j] + " ");
		System.out.print("\n");
	}

	public static Double vecvecprod(Double[] p, Double[] q) {
		Double pq = 0.0;
		for(int f=0; f<p.length; f++) 
			pq += p[f]*q[f];
		
		return pq;
	}
	
	public static Double[] scalarvecprod(Double a, Double[] p) {
		Double[] q = new Double[p.length];
		for(int f=0; f<p.length; f++) 
			q[f] = a*p[f];
		
		return q;
	}
	
	public static Double[] vecvecsum(Double[] p, Double[] q) {
		Double[] p_plus_q = new Double[p.length];
		for(int f=0; f<p.length; f++) 
			p_plus_q[f] = q[f] + p[f];
		
		return p_plus_q;
	}
	
	public static Double[] vecvecminus(Double[] p, Double[] q) {
		Double[] p_plus_q = new Double[p.length];
		for(int f=0; f<p.length; f++) 
			p_plus_q[f] = q[f] - p[f];
		
		return p_plus_q;
	}
	
	
	public static Double computeMu(Map<String,Map<String,Double>> r) {
		Double sum = 0.0;
		int count = 0;
		for(String u : r.keySet()) {
			for(String i : r.get(u).keySet()) {
				sum += r.get(u).get(i);
				count++;
			}
		}
		
		return sum/count;
	}

	public static Map<String,Map<String,Double>> r_u_i_TO_r_i_u(Map<String,Map<String,Double>> r) {
		Map<String,Map<String,Double>> r_i_u = new HashMap<String,Map<String,Double>>();
		
		for(String u : r.keySet()) {
			for(String i : r.get(u).keySet()) {
				Double rating = r.get(u).get(i);
				if( !r_i_u.containsKey(i) ) {
					Map<String, Double> map = new HashMap<String, Double>();
					r_i_u.put(i, map);
				}
				r_i_u.get(i).put(u, rating);					
			}
		}
		
		return r_i_u;
	}
		
	public static Set<String> r_u_i_TO_i(Map<String,Map<String,Double>> r) {
		Set<String> items = new HashSet<String>();
		
		for(String u : r.keySet())
			for(String i : r.get(u).keySet()) 
				items.add(i);
		
		return items;
	}	

	public static Set<String> r_u_i_TO_u(Map<String,Map<String,Double>> r) {
		return r.keySet();
	}	
	
	public static Map<String,Map<String,Double>> copyR(Map<String,Map<String,Double>> r) {
		Map<String,Map<String,Double>> res = new HashMap<String,Map<String,Double>>();
		for(String u : r.keySet()) {
			res.put(u, new HashMap<String,Double>());
			for(String i : r.get(u).keySet()) {
				res.get(u).put(i, r.get(u).get(i));
			}
		}
		
		return res;
	}
	

	public static Double oneHideoutRMSE(Rec rec, Map<String,Map<String,Double>> r, boolean remove) {
		Map<String,Map<String,Double>> test = extractTest(r, 100.0);

		Double RMSEsum = 0.0;
		int count = 0;
		for(String u : test.keySet()) {
			for(String i : test.get(u).keySet()) {
				System.out.println("count="+count);
				
				if( !r.get(u).containsKey(i) ) {
					System.out.println("Hello, your test set contains ratings not in r!!!!");
					continue;
				}
				
				double r_ui = r.get(u).get(i); 
				if(remove) r.get(u).remove(i);
				
				rec.buildRecommender(r);				
				RMSEsum += Math.pow( r_ui - rec.r_hat(u, i), 2.0 );
				count++;
				
				//put it back
				if(remove) r.get(u).put(i, r_ui);
			}
		}
		
		return Math.sqrt(RMSEsum/count);
	}	
	
	public static Double RMSE(Rec rec, Map<String,Map<String,Double>> r, Map<String,Map<String,Double>> test) {
				
		System.out.println("Building the recommender...");
		rec.buildRecommender(r);
		System.out.println("Finished building the recommender...");
		
		System.out.println("Computing RMSE and MAE...");
		Double RMSEsum = 0.0;

		int count = 0;
		
		for(String u : test.keySet()) {
			for(String i : test.get(u).keySet()) {
				double r_ui = test.get(u).get(i); 
				double r_pred = rec.r_hat(u, i);
				
				RMSEsum += Math.pow( r_ui - r_pred, 2.0 );
				count++;	
			}
		}		
		System.out.println("Done with this test set");
		
		return Math.sqrt(RMSEsum/count);
	}
	
	public static String RMSEplus(Rec rec, Map<String,Map<String,Double>> r, Map<String,Map<String,Double>> test) {
				
		System.out.println("Building the recommender...");
		rec.buildRecommender(r);
		System.out.println("Finished building the recommender...");
		
		System.out.println("Computing RMSE and MAE...");
		Double RMSEsum = 0.0;
		Double MAEsum = 0.0;
		Double TP = 0.0, FP = 0.0, TN = 0.0, FN = 0.0;
		Double threshold = 3.5;		

		int count = 0;
		System.out.println("=====u\ti\tr\tr_hat");
		for(String u : test.keySet()) {
			for(String i : test.get(u).keySet()) {
							
				double r_ui = test.get(u).get(i); 
				double r_pred = rec.r_hat(u, i);
				
				System.out.println("====="+u+'\t'+i+'\t'+r_ui+'\t'+r_pred);
				
				RMSEsum += Math.pow( r_ui - r_pred, 2.0 );
				MAEsum += Math.abs(r_ui - r_pred);
				
				if (r_ui >= 4.0)
					if (r_pred >= threshold)
						TP += 1;
					else
						FN += 1;
				else
					if (r_pred >= threshold)
						FP += 1;
					else
						TN += 1;
				
				count++;	
			}
		}		
		System.out.println("Done with this test set");
		
		Double precision = 1.0 * TP / (TP + FP);
    	Double recall = 1.0 * TP / (TP + FN);
    	Double fmeasure = 1.0 * 2 * (precision * recall) / (precision + recall);
	Double accuracy = 1.0 * (TP+TN)/(TP+TN+FP+FN);
		
		String results = "RMSE="+Math.sqrt(RMSEsum/count)+";"
						 +"MAE="+MAEsum/count+";"
						 +"precision="+precision+";"
						 +"recall="+recall+";"
						 +"fmeasure="+fmeasure+";"
						 +"accuracy="+accuracy+";";
		
		return results;
	}
	
	public static Map<String,Map<String,Double>> extractTest(Map<String,Map<String,Double>> r, Double pct) {
		Map<String,Map<String,Double>> test = new HashMap<String,Map<String,Double>>();
		
		int count=0;
		for(String u : r.keySet()) {
			for(String i : r.get(u).keySet()) {
				if( Math.random() <= pct/100.0 ) {
					if( !test.containsKey(u) ) 
						test.put(u, new HashMap<String,Double>());
					test.get(u).put(i, r.get(u).get(i));
					count++;
				}
			}
		}
		
		System.out.println("Removing test ratings from r...");
		for(String u : test.keySet()) {
			for(String i : test.get(u).keySet()) {
				if( !r.get(u).containsKey(i) ) 
					continue;
				r.get(u).remove(i);				
			}
		}
		
		System.out.println(count + " ratings were extracted for test");
		return test;
	}
	
	
	public static Map<String,Map<String,Double>> readData(String filename) throws Exception {
		Map<String,Map<String,Double>> r = new HashMap<String,Map<String,Double>>();
		
		System.out.println("Reading file " + filename + " ...");
		BufferedReader br = new BufferedReader( new FileReader(filename) );
		String line;
		while ( (line = br.readLine()) != null )  {
			//System.out.println("Reading line: " + line);
			String[] array = line.split("\t");
			if (array.length == 1){
				array = line.split(" ");
				if (array.length == 1)
					continue;
			}
			String user = array[0];
			String item = array[1];
			Double rating = Double.parseDouble(array[2]);
			
			if( !r.containsKey(user) ) r.put(user, new HashMap<String,Double>());
			
			r.get(user).put(item,rating);
		}
		
		System.out.println("End of reading file " + filename);
		
		return r;
	}
	
	//Checkpoint: Add a function to readData by Item
	public static Map<String,Map<String,Double>> readDataItem(String filename) throws Exception {
		Map<String,Map<String,Double>> r = new HashMap<String,Map<String,Double>>();
		
		System.out.println("Reading file " + filename + " ...");
		BufferedReader br = new BufferedReader( new FileReader(filename) );
		String line;
		while ( (line = br.readLine()) != null )  {
			//System.out.println("Reading line: " + line);
			String[] array = line.split("\t");
			if (array.length == 1) continue;
			String user = array[0];
			String item = array[1];
			Double rating = Double.parseDouble(array[2]);
			
			if( !r.containsKey(item) ) r.put(item, new HashMap<String,Double>());
			
			r.get(item).put(user,rating);
		}
		
		System.out.println("End of reading file " + filename);
		
		return r;
	}
}
