import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Balanced binary search tree.
 * @author jesse russell
 * @param <T> 
 */
public class AvlTree<T extends Comparable> implements Iterable<T>{
    // based on this youtube playlist: https://www.youtube.com/playlist?list=PLDV1Zeh2NRsD06x59fxczdWLhDDszUHKt
    // (WilliamFiset: AVL tree playlist)
    
    // I know I didn't need to make it balanced.
    
    // | sub-classes |
    private class Node{
        T value;
        Node left = null;
        Node right = null;
        // height
        byte h = 0; //* the height of the tree is around log_2(n) (assuming it is balanced and I haven't screwed something up). That means this field won't reach 127 until the node count is at least 100000000000000000000000000000000000000, which would overflow count anyway. so byte's the perfect choice.
        // balance factor (right.h - left.h)
        byte bf = 0;
        
        public Node(T value) { this.value = value; }
    }
    
    // | fields |
    private Node root = null;
    private int count = 0;
    
    // | properties |
    public int size() { return count; }
    
    
    // o=======================o
    // | searching and finding |
    // o=======================o
    public T get(T value){
        Node n = findNode(value);
        if (n == null) return null;
        else return n.value;
    }
        
    private T smallest(Node n){
        // dig left until you can't anymore
        while (n.left != null)
            n = n.left;
        
        return n.value;
    }
    
    private T largest(Node n){
        // dig right until you can't anymore
        while(n.right != null)
            n = n.right;
        
        return n.value;
    }
    
    public T max(){
        if (root == null) return null;
        return largest(root);
    }
    
    public T min(){
        if (root == null) return null;
        return smallest(root);
    }
    
    public boolean contains(T value){
        if (value == null) return false;
        return findNode(value) != null;
    }
    
    private Node findNode(T value){
        Node current = root;
        
        while(current != null){
            int cmp = value.compareTo(current.value);
            
            if (cmp < 0)
                current = current.left;
            else if (cmp > 0)
                current = current.right;
            else
                return current;
        }
        
        return null;
    }
    
    public T ceiling(T value){
        if (value == null) return null;
        
        Node current = root;
        T result = null;
        
        while(current != null){
            int cmp = value.compareTo(current.value);
            
            if (cmp < 0){
                result = current.value;
                current = current.left;
            }
            else if (cmp > 0)
                current = current.right;
            else
                return current.value;
        }
        
        return result;
    }
    
    public T floor(T value){
        if (value == null) return null;
        
        Node current = root;
        T result = null;
        
        while(current != null){
            int cmp = value.compareTo(current.value);
            
            if (cmp < 0)
                current = current.left;
            else if (cmp > 0){
                result = current.value;
                current = current.right;
            }
            else
                return current.value;
        }
        
        return result;
    }
    
    // o===========o
    // | insertion |
    // o===========o
    
    /**
     * Insert the given value into the tree. If the value is already present, it will be over-written.
     * @return True if the value wasn't already in the tree; false if the value was already in the tree or if the value was null.
     */
    public boolean put(T value){
        // refuse if null
        if (value == null) return false;
        
        // replace if already inside
        Node f = findNode(value);
        if (f != null){
            f.value = value;
            return false;
        }
        
        // insert
        root = insert(root, value);
        ++count;
        
        update(root);
        root = balance(root);
        return true;
    }
    
    private Node insert(Node n, T value){
        if (n == null) return new Node(value);
        
        int cmp = value.compareTo(n.value);
        
        if (cmp < 0)
            n.left = insert(n.left, value);
        else
            n.right = insert(n.right, value);
        
        update(n);
        return balance(n);
    }
    
    // o==========o
    // | deletion |
    // o==========o
    /**
     * Remove the given value from the tree.
     * @return True if the value was found and deleted; false if the value was not found.
     */
    public boolean delete(T value){
        if (value == null) return false;
        if (!contains(value)) return false;
        
        root = remove(root, value);
        --count;
        return true;
    }
    
    // In the case of the node to delete having two children: which child
    // should the node to swap with come from?
    boolean remove_swapRight = false;
    
    private Node remove(Node n, T value){
        int cmp = value.compareTo(n.value);
        
        // navigate to node:
        if (cmp < 0)
            n.left = remove(n.left, value);
        else if (cmp > 0)
            n.right = remove(n.right, value);
        
        // delete node:
        else if (n.left != null & n.right != null){
            // case 4: two children
            if (remove_swapRight){
                // move the smallest value on the left to the node to be deleted.
                n.value = smallest(n.right);
                n.right = remove(n.right, n.value);
            } else {
                // move the largest value on the right to the node to be deleted.
                n.value = largest(n.left);
                n.left = remove(n.left, n.value);
            }
            
            remove_swapRight = !remove_swapRight;
        }
        else if (n.left != null){
            // case 3: left child only
            return n.left;
        }
        else if (n.right != null){
            //case 2: right child only
            return n.right;
        }
        else{
            //case 1: no children
            return null;
        }
        //
        
        update(n);
        return balance(n);
    }
    
    // o===========o
    // | balancing |
    // o===========o
    
    private void update(Node N){
        // get the height of each child, -1 for null (a non-null child with no children would have height of 0)
        byte lh = N.left  == null ? (byte)-1 : N.left.h;
        byte rh = N.right == null ? (byte)-1 : N.right.h;
        
        // update height
        N.h = (byte)(Math.max(lh, rh) + 1);
        
        // update balance factor
        N.bf = (byte)(rh - lh);
    }
    
    private Node balance(Node N){
        // if left heavy
        if (N.bf < -1){
            if (N.left.bf <= 0)
                 return leftLeft(N);
            else return leftRight(N);
        }
        // else, if right heavy
        else if (N.bf > 1){
            if (N.right.bf >= 0)
                 return rightRight(N);
            else return rightLeft(N);
        }
        else     return N;
    }
    
    // left heavy with left heavy child
    private Node leftLeft(Node N){
        return rightRotate(N);
    }
    
    // left heavy with right heavy child
    private Node leftRight(Node N){
        N.left = leftRotate(N.left);
        return leftLeft(N);
    }
    
    // right heavy with right heavy child
    private Node rightRight(Node N){
        return leftRotate(N);
    }
    
    // right heavy with left heavy child
    private Node rightLeft(Node N){
        N.right = rightRotate(N.right);
        return rightRight(N);
    }
    
    
    // o===========o
    // | rotations |
    // o===========o
    
    //    | ORIGINAL |
    //       parent
    //         |
    //         A  <---- pivot
    //        / \
    //       B   C
    //      / \
    //     D   E
    
    //  | RIGHT ROTATE |
    //       parent
    //         |
    //         B
    //        / \
    //       D   A
    //          / \
    //         E   C
    private Node rightRotate(Node A){
        Node B = A.left;
        
        A.left = B.right;
        B.right = A;
        
        update(A);
        update(B);
        return B;
        
    }
    
    //    | ORIGINAL |
    //       parent
    //         |
    //         A  <---- pivot
    //        / \
    //       B   C
    //          / \
    //         F   G
    
    //   | LEFT ROTATE |
    //       parent
    //         |
    //         C
    //        / \
    //       A   G
    //      / \  
    //     B   F
    private Node leftRotate(Node A){
        Node C = A.right;
        
        A.right = C.left;
        C.left = A;
        
        update(A);
        update(C);
        return C;
    }
    
    
    // o===========o
    // | Iteration |
    // o===========o
    /**
     * Show all of the values in order.
     * @return A list of all values in order.
     */
    public List<T> show(){
        return inOrder();
    }
    
    private List<T> inOrder(){
        ArrayList<T> result = new ArrayList<T>(count);
        if (root != null) inOrder_helper(root, result);
        return result;
    }
    
    private void inOrder_helper(Node n, List<T> l){
        // explore left
        if (n.left != null)
            inOrder_helper(n.left, l);
        
        // add middle
        l.add(n.value);
        
        // explore right
        if (n.right != null)
            inOrder_helper(n.right, l);
    }
    
    public class AvlTree_iterator implements Iterator<T>{
        List<T> inOrder;
        int i = 0;
        
        public AvlTree_iterator(List<T> inOrder) { this.inOrder = inOrder; }
        
        public T next(){
            if (i < inOrder.size())
                 return inOrder.get(i++);
            else return null;
        }
        
        public boolean hasNext() { return i < inOrder.size(); }
    }
    
    public Iterator<T> iterator(){
        return new AvlTree_iterator(inOrder());
    }
}