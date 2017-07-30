package c08;

import java.util.*;

class Pet{};
class Dog extends Pet{};
class Pug extends Dog{};
class Cat extends Pet{};
class Rodent extends Pet{};
class Gerbil extends Rodent{};
class Hamster extends Rodent{};

class Counter {int i;}

//Test the RTII of JAVA Classes and Objects
public class PetCount {

	static String[] typenames = {"Pet","Dog",
			"Pug","Cat","Rodent","Gerbil","Hamster"};
	
	public static void main(String[] args){
		
		Vector pets = new Vector();
		try{
			Class[] petTypes = {
					Class.forName("c08.Pet"),
					Class.forName("c08.Dog"),
					Class.forName("c08.Pug"),
					Class.forName("c08.Cat"),
					Class.forName("c08.Rodent"),
					Class.forName("c08.Gerbil"),
					Class.forName("c08.Hamster"),
			};

			// randomly create new instance of some classes
			for (int i=0;i<15;i++){
				pets.addElement(
						petTypes[
						         (int)(Math.random()*petTypes.length)]
						        		 .newInstance());}

			Hashtable h = new Hashtable();

			for (int i=0;i<typenames.length;i++){
				h.put(typenames[i],new Counter());
			}
			
			//Count Instance numbers
			for (int i=0;i<pets.size();i++){
				Object o = pets.elementAt(i);
				if (o instanceof Pet){
					((Counter)h.get("Pet")).i++;
				}
				if (o instanceof Dog){
					((Counter)h.get("Dog")).i++;
				}
				if (o instanceof Pug){
					((Counter)h.get("Pug")).i++;
				}
				if (o instanceof Cat){
					((Counter)h.get("Cat")).i++;
				}
				if (o instanceof Rodent){
					((Counter)h.get("Rodent")).i++;
				}
				if (o instanceof Gerbil){
					((Counter)h.get("Gerbil")).i++;
				}
				if (o instanceof Hamster){
					((Counter)h.get("Hamster")).i++;
				}
			}//end instane counting
			
			//print working instances
			for(int i=0; i<pets.size();i++){
				System.out.println(pets.elementAt(i)
						.getClass().toString());
			}
			
			//print hashmap
			for(int i=0;i<h.size();i++){
				System.out.println(
					typenames[i]+" count:"+
				((Counter)h.get(typenames[i])).i);
			}
			
		}
		
		catch (ClassNotFoundException e){}
		catch (InstantiationException e){}
		catch (IllegalAccessException e){}
		
	}//end main
	
}//end class PetCount
