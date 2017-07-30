import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RecBaseline implements Rec {

	//user_item_rating_map
	Map<String,Map<String,Double>> r;
	Set<String> users, items;
	
	Double mu; //mean
	
	Map<String,Double> bu = new HashMap<String,Double>();
	Map<String,Double> bi = new HashMap<String,Double>();
	
	Double gamma = 0.005;
	
	Double lambda_bu = 0.02;
	Double lambda_bi = 0.02;
	
	int iternum = 30;

	public void buildRecommender(Map<String,Map<String,Double>> r) {
		this.r = r;
		initialize();		
		this.mu = Util.computeMu(r);
		computeBuBiPuQi();
	}
	
	//predict rating
	public Double r_hat(String u, String i) {
		Double bu_u = 0.0, bi_i = 0.0;
		if ( bu.containsKey(u) ) bu_u = bu.get(u);			
		if ( bi.containsKey(i) ) bi_i = bi.get(i);
		
		return mu+bu_u+bi_i;
	}
	
	void initialize() {
		this.items = Util.r_u_i_TO_i(r);
		this.users = Util.r_u_i_TO_u(r);
		
		for(String u : users) bu.put(u, 0.0);
			
		for(String i : items) bi.put(i, 0.0);
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
					
					Double b_u = bu.get(u);
					Double b_i = bi.get(i);

					bu.put(u, b_u + gamma*(e_ui-lambda_bu*b_u));
					bi.put(i, b_i + gamma*(e_ui-lambda_bi*b_i));
				}
			}
		}
	}
	
	Double getLambda_bu() {return lambda_bu;}
	void setLambda_bu(Double lambda_bu) {this.lambda_bu = lambda_bu;}

	Double getLambda_bi() {return lambda_bi;}
	void setLambda_bi(Double lambda_bi) {this.lambda_bi = lambda_bi;}

	int getIternum() {return iternum;}
	void setIternum(int iternum) {this.iternum = iternum;}
	
	public void outputBaselineParameters(Map<String,Map<String,Double>> test){
		Double bu_u = 0.0, bi_i = 0.0;
		
		System.out.println("=====r\tmu\tbu\tbi");
		for(String u : test.keySet()) {
			for(String i : test.get(u).keySet()) {
				bu_u = 0.0; bi_i = 0.0;
				if ( bu.containsKey(u) ) bu_u = bu.get(u);			
				if ( bi.containsKey(i) ) bi_i = bi.get(i);	
							
				double r_ui = test.get(u).get(i); 
					
				System.out.println("====="+r_ui+'\t'+mu+'\t'+
									bu_u+'\t'+bi_i);
			}
		}	
	}
	

	public static void main(String[] args) throws Exception {
		//Map<String,Map<String,Double>> r = Util.readData("ratings_data.txt"); 
		
		//Map<String,Map<String,Double>> test = Util.extractTest(r, 1.0); //1%
		
		//RecBaseline rec = new RecBaseline();
		
		//System.out.println("RMSE="+Util.RMSE(rec,r,test));
	}
}
