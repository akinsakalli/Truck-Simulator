public class MyQueue<AnyType> {
    private MyLinkedList<AnyType> list;
    private int size;
    public MyQueue() {
        this.list = new MyLinkedList<>();
        this.size = 0;
    }
    public void enqueue( AnyType element ) {
        list.addLast(element);
        this.size = this.size + 1;
    }
    public AnyType dequeue() {
        if (size >= 1)
            this.size = this.size - 1;
        return list.removeFirst();
    }
    public AnyType peek() {
        return list.getFirst();
    }
    public boolean isEmpty() {
        return this.size == 0;
    }
    @Override
    public String toString() {
        return "Queue: " + list.toString();
    }
}
