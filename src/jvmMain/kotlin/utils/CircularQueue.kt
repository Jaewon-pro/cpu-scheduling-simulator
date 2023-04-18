package utils
class CircularQueue<T>(private val capacity: Int) {
    private val queue = arrayOfNulls<Any?>(capacity)
    private var front = -1
    private var rear = -1

    @Throws(IllegalStateException::class)
    fun add(item: T) {
        if (isFull()) throw IllegalStateException("Queue is full")
        if (isEmpty()) front = 0
        rear = (rear + 1) % capacity
        queue[rear] = item
    }

    @Throws(NoSuchElementException::class)
    fun poll(): T {
        if (isEmpty()) throw NoSuchElementException("Queue is empty")
        val item = queue[front] as T
        queue[front] = null
        if (front == rear) {
            front = -1
            rear = -1
        } else {
            front = (front + 1) % capacity
        }
        return item
    }

    @Throws(NoSuchElementException::class)
    fun element(): T {
        if (isEmpty()) throw NoSuchElementException("Queue is empty")
        return queue[front] as T
    }

    fun isEmpty(): Boolean = front == -1
    fun isFull(): Boolean = (rear + 1) % capacity == front
    fun isNotEmpty(): Boolean = !isEmpty()
    fun size(): Int {
        if (isEmpty()) return 0
        return if (rear >= front) rear - front + 1 else capacity - front + rear + 1
    }
}