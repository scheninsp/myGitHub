package c08;

public class LinkedList {

	public class Data {
		private Object obj;
		private Data next = null;
		
		Data(Object obj){
			this.obj = obj;
		}
	}
	
	private Data first = null;
		
	public boolean isEmpty(){
		return (first == null);
	}
	
	public void insertFirst(Object obj){
		Data data = new Data(obj);
		data.next = first;
		first = data;
	}
	
	public Object deleteFirst() throws Exception{
		if (isEmpty())
			throw new Exception("Empty list");
		Data temp = first;
		first = first.next;
		return temp.obj;
	}
	
	public Object find(Object obj) throws Exception{
		if (first == null)
			throw new Exception("Empry list");
		Data cur = first;
		while (cur != null){
			if (cur.obj.equals(obj)){ 
				return cur.obj;
			}
			cur = cur.next;
		}		
		return null;
	}
	
	public void remove(Object obj) throws Exception{
		if (first == null)
			throw new Exception("Empry list");
		Data pre = first;
		Data cur = first.next;
		
		if (first.obj.equals(obj)){
			deleteFirst();
		}
		
		while (cur!=null){
			if (cur.obj.equals(obj)){
				pre.next = cur.next;
			}
			pre = cur;
			cur = cur.next;
		}
	}
	
	public void display(){
		Data cur = first;
		while (cur!=null){
			System.out.print(cur.obj);
			System.out.print("->");
			cur = cur.next;
		}
		System.out.print("\n");
	}
	
    public static void main(String[] args) throws Exception {  
        LinkedList ll = new LinkedList();  
        //ll.deleteFirst(); //show exception
        ll.insertFirst(4);  
        ll.insertFirst(3);  
        ll.insertFirst(2);  
        ll.insertFirst(1);  
        ll.display();  
        ll.deleteFirst();  
        ll.display();  
        ll.remove(3);  
        ll.display();  
        System.out.println(ll.find(1));  
        System.out.println(ll.find(4));  
    }  

}