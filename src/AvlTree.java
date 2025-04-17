public class    AvlTree<AnyType extends Comparable<AnyType>> {

    // This is the class representing the nodes in the AVL Tree.
    public static class AvlNode<AnyType> {
        private AnyType element;
        private AvlNode<AnyType> left;
        private AvlNode<AnyType> right;
        private int height;
        public AvlNode(AnyType element, AvlNode<AnyType> left, AvlNode<AnyType> right) {
            this.element = element;
            this.left = left;
            this.right = right;
        }
    }

    private AvlNode<AnyType> root;
    public AvlTree() {
        root = null;
    }

    // Driver of the insertion method to the AVL Tree.
    public void insert(AnyType element) {
        root = insert(element, root);
    }

    // Driver of the remove method from the AVL Tree.
    public void remove(AnyType element) {
        root = remove(element, root);
    }
    private AvlNode<AnyType> insert( AnyType element, AvlNode<AnyType> root ) {
        if (root == null)
            return new AvlNode<>(element, null, null);

        int compareResult = element.compareTo( root.element );

        if (compareResult < 0)
            root.left = insert(element, root.left);
        else if (compareResult > 0)
            root.right = insert(element, root.right);
        else {}

        return balance(root);
    }
    private AvlNode<AnyType> remove(AnyType element, AvlNode<AnyType> root ) {
        if (root == null)
            return root;

        int compareResult = element.compareTo( root.element );

        if (compareResult < 0)
            root.left = remove( element, root.left );
        else if (compareResult > 0)
            root.right = remove( element, root.right );
        else if ( root.left != null && root.right  != null ) {
            root.element = findMin( root.right ).element;
            root.right = remove( root.element, root.right );
        }
        else {
            if (root.left != null)
                root = root.left;
            else
                root = root.right;
        }
        return balance(root);
    }

    // Balance method keeps the tree satisfying the AVL tree requirements.
    private AvlNode<AnyType> balance( AvlNode<AnyType> node ) {
        if (node == null)
            return node;

        if ( height(node.left) - height(node.right) > 1 ) {
            if ( height(node.left.left) >= height(node.left.right) )
                node = singleLeftRotation(node);
            else
                node = doubleLeftRotation(node);
        }
        else if ( height(node.right) - height(node.left) > 1 ) {
            if ( height( node.right.right ) >= height( node.right.left) )
                node = singleRightRotation(node);
            else
                node = doubleRightRotation(node);
        }
        node.height = Math.max( height(node.left), height(node.right) ) + 1;
        return node;
    }

    // Rotations are performed in order to keep the tree balanced after insertions and deletions.
    private AvlNode<AnyType> singleLeftRotation( AvlNode<AnyType> node ) {
        AvlNode<AnyType> leftChild = node.left;
        node.left = leftChild.right;
        leftChild.right = node;
        node.height = Math.max( height(node.left), height(node.right) ) + 1;
        leftChild.height = Math.max( height(leftChild.left), height(leftChild.right) ) + 1;
        return leftChild;
    }
    private AvlNode<AnyType> singleRightRotation( AvlNode<AnyType> node ) {
        AvlNode<AnyType> rightChild = node.right;
        node.right = rightChild.left;
        rightChild.left = node;
        node.height = Math.max( height(node.left), height(node.right) ) + 1;
        rightChild.height = Math.max( height(rightChild.left), height(rightChild.right) ) + 1;
        return rightChild;
    }
    private AvlNode<AnyType> doubleLeftRotation( AvlNode<AnyType> node ) {
        node.left = singleRightRotation( node.left );
        return singleLeftRotation( node );
    }
    private AvlNode<AnyType> doubleRightRotation( AvlNode<AnyType> node ) {
        node.right = singleLeftRotation( node.right );
        return singleRightRotation( node );
    }
    private AvlNode<AnyType> findMin(AvlNode<AnyType> node) {
        if (node == null)
            return null;
        else if (node.left == null)
            return node;
        return findMin(node.left);
    }
    private AvlNode<AnyType> findMax(AvlNode<AnyType> node) {
        if (node == null)
            return null;
        else if (node.right == null)
            return node;
        return findMax(node.right);
    }
    private int height( AvlNode<AnyType> node ) {
        int height;
        if (node == null)
            height = -1;
        else
            height = node.height;
        return height;
    }


    // This method finds the largest node with a smaller element than the given input element.
    public AnyType findSmallerLargest( AnyType element ) {
        AvlNode<AnyType> currentNode = root;
        AvlNode<AnyType> currentLargestNode = null;
        if (currentNode == null)
            return null;
        while (true) {
            if (currentNode == null) {
                if (currentLargestNode == null)
                    return null;
                else
                    return currentLargestNode.element;
            }
            else if (currentNode.element.compareTo(element) == 0) {
                return currentNode.element;
            }
            if (currentNode.element.compareTo(element) < 0) {
                if (currentLargestNode == null) {
                    currentLargestNode = currentNode;
                }
                else {
                    if (currentNode.element.compareTo(currentLargestNode.element) > 0)
                        currentLargestNode = currentNode;
                }
                currentNode = currentNode.right;
            }
            else if (currentNode.element.compareTo(element) > 0) {
                currentNode = currentNode.left;
            }
        }
    }

    // This method finds the smallest node with a larger element than the given input element.
    public AnyType findLargerSmallest( AnyType element ) {
        AvlNode<AnyType> currentNode = root;
        AvlNode<AnyType> currentSmallestNode = null;
        if (currentNode == null)
            return null;
        while (true) {
            if (currentNode == null) {
                if (currentSmallestNode == null)
                    return null;
                else
                    return currentSmallestNode.element;
            }
            else if (currentNode.element.compareTo(element) == 0) {
                return currentNode.element;
            }
            if (currentNode.element.compareTo(element) > 0) {
                if (currentSmallestNode == null) {
                    currentSmallestNode = currentNode;
                }
                else {
                    if (currentNode.element.compareTo(currentSmallestNode.element) < 0)
                        currentSmallestNode = currentNode;
                }
                currentNode = currentNode.left;
            }
            else if (currentNode.element.compareTo(element) < 0)
                currentNode = currentNode.right;
        }
    }
}
