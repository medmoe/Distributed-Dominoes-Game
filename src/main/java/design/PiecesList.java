package design;

public class PiecesList {
    private Piece head;
    public PiecesList(){
        head = null;
    }
    //add pieces to the end of the list
    public void addToEnd(Piece p){
        if(!(isEmpty()) && !(isThere(p))){
            Piece current = head;
            while(current.next != null){
                current = current.next;
            }
            p.next = current.next;
            current.next = p;
        }else{
            System.out.println("list is empty");
        }
    }
    //add pieces to the begin of the list
    public void addToBegin(Piece p){
        if(!(isThere(p))) {
            p.next = head;
            head = p;
        }
    }
    //getters

    public Piece getHead() {
        return head;
    }
    public Piece getTail() {
        Piece current = head;
        while(current.next != null){
            current = current.next;
        }
        return current;
    }
    // check if the list is empty
    public boolean isEmpty(){
        if(head == null){
            return true;
        }
        return false;
    }
    // length of the list
    public int length(){
        int counter = 0;
        Piece current = head;
        while(current != null){
            counter++;
            current = current.next;
        }
        return counter;
    }
    // no duplicates in the list
    public boolean isThere(Piece p){
        if(!(isEmpty())){
            Piece current = head;
            while(current != null){
                if(current == p){
                    return true;
                }
                current = current.next;

            }
        }
        return false;
    }
}

