package c08;

public class IceCream {

	static String[] flav = {
			"a","b","c","d","e","f","g","h","i","j","k"
	};
	
	static String[] flavorSet(int n){
		n=Math.abs(n)%(flav.length+1);
		int[] picks = new int[n];
		String[] results = new String[n];
		
		for (int i=0;i<n;i++)
			picks[i]=-1;
		
		for (int i=0;i<n;i++)
		{
			retry:
			while(true) { 
			int t = (int) (Math.random() * flav.length);
				for (int j=0;j<i;j++){
					if (picks[j] == t)
							continue retry;
				}
			picks[i]=t;
			results[i]=flav[t];
			break;
			}
		}
		return results;
	}
	
	public static void main(String[] args){
		int nflav = 3;
		String[] fl=flavorSet(nflav);
		for(int i=0;i<fl.length;i++)
			System.out.println("Flavor "+i+" is "+fl[i]+"\n");
	}
	
	
}
