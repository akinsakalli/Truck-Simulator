public class MyLinkedList<AnyType> {
    public static class Node<AnyType> {
        public AnyType element;
        public Node<AnyType> next;
        public Node( AnyType element, Node<AnyType> next ) {
            this.element = element;
            this.next = next;
        }
    }
    private Node<AnyType> head;
    private Node<AnyType> tail;
    public AnyType getFirst() {
        return head.element;
    }
    public void addLast(AnyType element ) {
        Node<AnyType> newNode = new Node<>(element, null);
        if (head == null) {
            head = newNode;
            tail = newNode;
        }
        else {
            tail.next = newNode;
            tail = newNode;
        }
    }
    public AnyType removeFirst() {
        Node<AnyType> node;
        if (head == null)
            return null;
        else if (head == tail) {
            node = head;
            head = null;
            tail = null;
        }
        else {
            node = head;
            head = head.next;
        }
        return node.element;
    }
    public MyLinkedList() {
        head = null;
        tail = null;
    }

}
