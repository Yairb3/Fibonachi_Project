
/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{

    public HeapNode first; // set as default to null
    public HeapNode min; // set as default to null
    public int size; // number of nodes in heap , set as default to 0
    public int treesNum; // set as default to 0
	static int cuts = 0; //num of cuts we performed over all the program runtime
	static int links = 0; //num of links we performed over all the program runtime
    public int markedNodes; // number of marked nodes in the heap, set as default to 0
    public int maxRank; // set as default to 0;

    // using default constructor

    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     *
     */
    public boolean isEmpty() {return (this.first == null);}

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     */
    public HeapNode insert(int key)
    {
        HeapNode node = new HeapNode ( key);


        if (this.isEmpty()) {
            this.min = node;

        }

        else { //add a new node to the beginning of the trees list (make the node first):

            node.next = this.first ;
            node.prev = first.prev;
            this.first.prev = node;
            node.prev.next = node;
            this.min = key < this.min.getKey() ? node : this.min ; // update min if new key is the mininal in heap

        }

        //update fields:
        this.first = node;
        this.treesNum +=1;
        this.size +=1;

        return node;

    }

    /**
     * public void deleteMin()
     *
     * Deletes the node containing the minimum key.
     *
     */
    
    
    
public void deleteMin(){
        
        if (this.isEmpty()){ // case 0 - heap is empty
            return;
        }

        /* case1 - Heap containes only one Tree of rank 0 or 1
         ** case1A - rank of the Tree is 0 - need to make the Heap empty
         ** case 1B - rank of the tree is 1 - need to set the child to be the first and the min
         */

        if (this.treesNum == 1 && this.first.rank < 2){ // Case1

            if (this.first.rank == 0 ) { // Case 1A
                
                this.first = null; // update fields
                this.treesNum = 0;
                this.size = 0;
                this.min = null;

            }else { // Case 1B

                this.first = this.first.child;
                this.min = this.first;
                size -= 1;
                this.first.parent = null;

            }
        }else {

            /* case2 - treeNum > 1 or the only tree has more than 1 child - we need to delete the min and also to consolidate
             */

            prepareHeapToConsolidate();
            successiveLinking();
        }

    }

    public void seperateFromparent (HeapNode parent){

        HeapNode runner = parent.child;
        this.min = parent.child;
        this.treesNum -= 1;


        while (runner.parent == parent){

            runner.parent = null; // cut the node from the parent

            if (runner.key < this.min.key){ // also finding new minimum

                this.min = runner;
            }

            this.treesNum += 1; // also updade the new tree sum;

            runner = runner.next;

        }

        parent.child = null;// cutting the parent from the heap
    }

    public void byPassTree (HeapNode tree){ // assuming there are more trees, no children

        if (tree.child == null){

            if (this.first == tree){
                this.first = tree.next;
            }

            tree.prev.next = tree.next;
            tree.next.prev = tree.prev;

        } else {

            if (this.first == tree){
                this.first = tree.child;
            }
           


            tree.prev.next = tree.child;
            tree.next.prev = tree.child.prev;

            tree.child.prev.next = tree.next;
            tree.child.prev = tree.prev;

        }

    }

    /* we need to take the min and make his children to be part of the Heap trees series

    Case2 A - there is only one tree, and it has more than one children
    Case2 B - min is not the only tree and it has No children
    case2 C - min is not the only tree and it has  at least 1 child

     */

    public void prepareHeapToConsolidate(){

        if (treesNum == 1){ // Case2 A - there is only one tree, and it has more than one child

            HeapNode parent = this.first;
            this.first = this.first.child;
            seperateFromparent(parent);
        }
        else if (this.min.child == null){// Case2 B - min is not the only tree , and it has No children - need to bypass it

            byPassTree(this.min);
            treesNum -= 1;

        }else{   // case2 C - min is not the only tree and it has at least 1 child

            HeapNode parent = this.min;

            byPassTree(parent);

            seperateFromparent(parent);

        }

        size -= 1;

    }

    public void successiveLinking(){

        double base = (1+Math.sqrt(5))/2; //the golden ratio
        HeapNode[] buckets = new HeapNode[(int) (Math.log(this.size) / Math.log(base)) + 1]; //all buckets are null
        int treesWasHandled = 0;
        HeapNode runner = this.first;

        while (treesWasHandled < this.treesNum){

            HeapNode tree1 = runner;
            runner = runner.next;
            int currentRank = tree1.rank;

                while (buckets[currentRank] != null){

                    HeapNode tree2 = buckets[currentRank];
                    tree1 = linkTrees(tree1,tree2);
                    buckets[currentRank] = null;
                    currentRank++;
                }
                buckets[currentRank] = tree1;

            treesWasHandled ++;
        }

        creatingHeapfromBuckets(buckets);

    }

    public void creatingHeapfromBuckets(HeapNode [] buckets){

        this.min = null; //reset the heap
        this.first = null;
        this.treesNum = 0;

        for (int i=0; i<buckets.length; i++){

            if(buckets[i] != null){
                HeapNode tree = buckets[i];

                if(this.first == null){
                    this.first = tree;
                    this.min = tree;
                    tree.next = tree;
                    tree.prev = tree;

                }else{

                    insertTree(tree);
                }

                treesNum ++;
            }
        }
    }

    public void insertTree(HeapNode node) {

            node.prev = this.first.prev;
            this.first.prev.next = node;
            node.next = this.first;
            this.first.prev = node;

            if(node.key < this.min.key){
                this.min = node;
            }

        }


    public HeapNode linkTrees( HeapNode tree1, HeapNode tree2){
    	
    	links++;
        
        HeapNode smallerTree = tree1.key < tree2.key ? tree1 : tree2;
        HeapNode graterTree = tree1.key > tree2.key ? tree1 : tree2;

        graterTree.parent = smallerTree;
        smallerTree.child = graterTree;

        if(smallerTree.rank > 0) {

            graterTree.next = smallerTree.child;
            graterTree.prev = smallerTree.child.prev;

            smallerTree.child.prev.next = graterTree;
            smallerTree.child.prev = graterTree;
        }else{

            graterTree.next = graterTree;
            graterTree.prev = graterTree;

        }

        smallerTree.rank ++;
        return smallerTree;
    }

    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     *
     */
    public HeapNode findMin()
    {
    	return this.min;
    } 

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     */
    public void meld (FibonacciHeap heap2)
    {
    	
    	if (this.isEmpty() && !heap2.isEmpty()) { //this is empty and heap 2 not
    		
    		this.min = heap2.min;
    		this.first = heap2.first;
    		
    	}
    	
    	else if (! this.isEmpty() && !heap2.isEmpty() ) { // two heaps are not empty
    		
    		if(heap2.min.key < this.min.key) //change min to minimum of heap2.
    		{
    			this.min = heap2.min;
    		}
    		// meld heaps by connect heap2 right to this.
    		this.first.prev.next = heap2.first;
    		heap2.first.prev = this.first;
    		HeapNode temp = heap2.first.prev;
    		heap2.first.prev = this.first.prev;
    		this.first.prev = temp;
    		
    	}
    	
    	// other caser ( both heaps are empty or this is not empty) - do nothing. 
    	
    	
    }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *
     */
    public int size()
    {
       return this.size; // should be replaced by student code
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     *
     */
    public int[] countersRep() 
    {
    	if(this.isEmpty())
    	{
    		return new int[] {};
    	}
    	
    	int maxRank = this.first.rank;
    	HeapNode node = this.first.next;
    	
    	//find the biggest rank in the root list:
    	while(node != this.first)
    	{
    		if(node.rank > maxRank)
    		{
    			maxRank = node.rank;
    		}
    		node = node.next;
    	}
	   int[] arr = new int[maxRank+1]; //initialize the counts array
       arr[first.rank] ++;
       node = first.next;
       
       //fill the array:
   	   while(node != this.first)
   	   {
   		   arr[node.rank] ++;
   		   node = node.next;
   	   }
	   return arr; 
    }
    		

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     *
     */
    public void delete(HeapNode x) 
    {   
    	if(this.size == 1) //there's only one node in the heap - make the heap empty
    	{
    		this.first = null;
    		this.markedNodes = 0;
    		this.treesNum = 0;
    		this.min = null;
    		this.size = 0;
    	}
    	else
    	{
    		decreaseKey(x, x.key-this.min.key+1); //make x as the minimal key in heap, and then delete min.
    		this.deleteMin();
    	}
    }

    public void decreaseKey(HeapNode x, int delta)
    {    
    	assert (delta < 0) : "delta must be positive"; // invalid delta
    	
    	if(delta > 0) //if delta == 0 we do nothing
    	{
    		x.key = x.key - delta; //decrease the key x by delta
    		
    		if(x.key < this.min.key) //update min if needed
			{
				this.min = x;
			}
    		HeapNode y = x.parent;
    		if( y != null && x.key < y.key) //violating the relation order of heap x isn't a root
    		{
    		
    			cut(x, y);
    			cascadingCuts(y);
    		}
    	}
    }
    
    /**
     * public void cut(HeapNode x, HeapNode y)
     *
     * The function cuts the connection between the node x and his parent y,
     * and adds x to the root list and 
     * complexity: O(1)
     */
    public void cut(HeapNode x, HeapNode y)
    {
    	cuts ++;  //we make a cut
    	y.rank --; //y lost a child
    	this.treesNum ++; //the tree rooted at x will be added to the root list
    	x.parent = null; //x is now a root
    	if (x.isMarked()) //unmark x if needed, roots are never marked
    	{
    		x.mark = false;
        	this.markedNodes --;
    	}
    	
    	
    	if(x.next == x)  // y.child pointer points at x and x is the only child of y
    	{
    		y.child = null;
    	}
    	else if(y.child == x) //y.child pointer points at x, and x has brother/s
    	{
    		y.child = x.next;
    		x.prev.next = x.next;
    		x.next.prev = x.prev;
    	}
    	else //y.child pointer doesn't point at x, and x has brother/s
    	{
    		x.prev.next = x.next;
    		x.next.prev = x.prev;
    	} 
    	
    	//insert x to the root list
    	x.next = this.first;
    	this.first.prev.next = x;
    	x.prev = this.first.prev;
    	this.first.prev = x;
    	this.first = x;
    }
    
    /**
     * public void cascadingCuts(HeapNode y)
     * 
     * @pre: a cut was performed on y's child.
     * recursive function
     * The function travels the nodes from y to the root. It marks y if it wasn't marked before.
     * If y was already marked, it cuts y and marks its parent.
     * The function stops when it marks a node that wasn't marked before.
     * 
     * complexity: O(n)
     */
    public void cascadingCuts(HeapNode node)
    {
    	HeapNode parent = node.parent;
    	if(parent != null) //node isn't a root
    	{
    		if(!node.isMarked())
    		{
    			node.mark = true;
    			this.markedNodes ++;
    		}
    		else
    		{
    			cut(node, parent);
    			cascadingCuts(parent);
    		}
    	}
    }

    /**
     
     * This function returns the current potential of the heap:
     */
    public int potential() 
    {    
    	return (this.treesNum + 2*this.markedNodes);
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks()
    {    
    	return links;
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts()
    {    
    	return cuts; 
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        System.out.println(H.size + "-------" + k);
        int[] arr = new int[k];
        int counter = 0;
        int value = H.min.key;

        FibonacciHeap subHeap = new FibonacciHeap();
        subHeap.insert(value);
        subHeap.first.pointer = H.min;

        while (counter < k){

            HeapNode currMin = subHeap.min;
            if(currMin == null){
                break;
            }
            arr[counter] = currMin.key;
            counter ++;
            subHeap.deleteMin();

            subHeap.insertChilds(currMin.pointer);

        }

        //System.out.println(Arrays.toString(arr));

        return arr; // should be replaced by student code
    }

    public void insertChilds (HeapNode parent){

        HeapNode child = parent.child;

        if (child != null) {
            HeapNode temp = child.prev;
            child.prev.next = null;


            while (child != null) {

                this.insert(child.key);
                this.first.pointer = child;
                child = child.next;

            }

            temp.next = parent.child;

        }
    }

/**
 * public class HeapNode
 *
 * If you wish to implement classes other than FibonacciHeap
 * (for example HeapNode), do it in this file, not in another file.
 *
 */
public static class HeapNode{

    private int key;
    private String info;

    private int rank; //number of childs
    private boolean mark;
    private HeapNode next; // right sibling\ tree.
    private HeapNode prev; // left sibling\ tree.
    private HeapNode parent;
    private HeapNode child;
    private  HeapNode pointer ;

    public HeapNode(int key) {
        this.key = key;
        this.rank = 0;
        this.mark = false;
        this.next = this;
        this.prev = this;
        this.parent = null;
        this.child = null;
        this.pointer = null;


    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int k) {
        this.key = k;
    }



    public int getRank() {
        return this.rank;
    }

    public void setRank(int r) {
        this.rank = r;
    }

    public boolean isMarked() { // return true if node is marked, else return false.
        return this.mark;
    }

    public HeapNode getChild() {
        return this.child;
    }
    public void setChild(HeapNode newnode) {
        this.child=newnode;
    }

    public HeapNode getParent() {
        return this.parent;
    }
    public void setParent(HeapNode newnode) {
        this.parent=newnode;
    }

    public HeapNode getNext() {
        return this.next;
    }
    public void setNext(HeapNode newnode) {
        this.next=newnode;
    }


    public HeapNode getPrev() {
        return this.prev;
    }
    public void setPrev(HeapNode newnode) {
        this.prev=newnode;
    }

}
}







































