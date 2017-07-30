import java.util.*;

public class RecMF implements Rec {

	//user_item_rating_map
	Map<String,Map<String,Double>> r;
	Set<String> users, items;
	
	Double mu; //mean
	
	Map<String,Double> bu = new HashMap<String,Double>();
	Map<String,Double> bi = new HashMap<String,Double>();
	
	/* original setting
	int F = 20; //number of factors
	*/
	int F = 20; //number of factors
	
	Map<String,Double[]> pu = new HashMap<String,Double[]>();
	Map<String,Double[]> qi = new HashMap<String,Double[]>();
	
	Double gamma = 0.005;
	
	/* original settings
	Double lambda_bu = 0.02;
	Double lambda_bi = 0.02;
	Double lambda_pu = 0.05;
	Double lambda_qi = 0.05;
	*/
	
	Double lambda_bu = 0.02;
	Double lambda_bi = 0.02;
	Double lambda_pu = 0.2;
	Double lambda_qi = 0.2;
	
	int iternum = 45;  //70

	public void buildRecommender(Map<String,Map<String,Double>> r) {
		this.r = r;
		initialize();
		this.mu = Util.computeMu(r); // Checkpoint: mu - 0.5
		computeBuBiPuQi();
	}
	
	//predict rating
	public Double r_hat(String u, String i) {
		Double bu_u = 0.0, bi_i = 0.0, pu_qi=0.0;
		if ( bu.containsKey(u) ) bu_u = bu.get(u);			
		if ( bi.containsKey(i) ) bi_i = bi.get(i);
		if ( pu.containsKey(u) && qi.containsKey(i) ) 
			pu_qi = Util.vecvecprod(pu.get(u), qi.get(i));
		else
			pu_qi = mu;
	
		return pu_qi;
		//return mu;
	}
	
	void initialize() {
		this.items = Util.r_u_i_TO_i(r);
		this.users = Util.r_u_i_TO_u(r);
		
		for(String u : users) bu.put(u, 0.0);
				
		for(String i : items) bi.put(i, 0.0);
		
		for(String u : users) {
			Double[] vec = new Double[F];
			for(int f=0; f<getF(); f++) 
				vec[f] = Math.random()/1.0;
				
			pu.put(u, vec);
		}
		System.out.println("Length of pu: "+pu.size());
		for(String i : items) {
			Double[] vec = new Double[F];
			for(int f=0; f<getF(); f++) 
				vec[f] = Math.random()/1.0;

			qi.put(i, vec);
		}
		System.out.println("Length of qi: "+qi.size());
	}
	
	//error
	Double e(String u, String i) {
		return r.get(u).get(i) - r_hat(u,i);
	}
	
	void computeBuBiPuQi() {
		for(int iter=0; iter<this.iternum; iter++) {

			for(String u : r.keySet()) {
				for(String i : r.get(u).keySet()) {
					Double e_ui = e(u,i);
					
					Double[] p_u = pu.get(u);
					Double[] q_i = qi.get(i);
					
					pu.put(u, Util.vecvecsum(p_u, Util.scalarvecprod(gamma, Util.vecvecsum(
																		Util.scalarvecprod(e_ui, q_i), 
																		Util.scalarvecprod(-lambda_pu, p_u)))));
					qi.put(i, Util.vecvecsum(q_i, Util.scalarvecprod(gamma, Util.vecvecsum(
							   											Util.scalarvecprod(e_ui, p_u), 
							   											Util.scalarvecprod(-lambda_qi, q_i)))));
				}
			}
		}
	}
	
	int getF() {return F;}
	void setF(int f) {F = f;}

	Double getLambda_bu() {return lambda_bu;}
	void setLambda_bu(Double lambda_bu) {this.lambda_bu = lambda_bu;}

	Double getLambda_bi() {return lambda_bi;}
	void setLambda_bi(Double lambda_bi) {this.lambda_bi = lambda_bi;}

	Double getLambda_pu() {return lambda_pu;}
	void setLambda_pu(Double lambda_pu) {this.lambda_pu = lambda_pu;}

	Double getLambda_qi() {return lambda_qi;}
	void setLambda_qi(Double lambda_qi) {this.lambda_qi = lambda_qi;}

	int getIternum() {return iternum;}
	void setIternum(int iternum) {this.iternum = iternum;}


	public void outputLatentFactors(){
		
		System.out.println("User Latent Factors");
		for(String u : pu.keySet()) {
			System.out.print(u);
			for(int f=0; f<F; f++) 
				System.out.print("\t"+pu.get(u)[f]);  // '\t' won't work
			System.out.println();
		}	
		
		System.out.println("Item Latent Factors");
		for(String i : qi.keySet()) {
			System.out.print(i);
			for(int f=0; f<F; f++) 
				System.out.print("\t"+qi.get(i)[f]);
			System.out.println();
		}
		
	}

	public static void main(String[] args) throws Exception {
		//Map<String,Map<String,Double>> r = Util.readData("ratings_data.txt"); 
		
		//Map<String,Map<String,Double>> test = Util.extractTest(r, 1.0); //1%
		
		//RecMF rec = new RecMF();
		
		//System.out.println("RMSE="+Util.RMSE(rec,r,test));
	}
}
