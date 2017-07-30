import java.util.Map;

public interface Rec {
	public void buildRecommender(Map<String,Map<String,Double>> r);
	public Double r_hat(String u, String i);
}
