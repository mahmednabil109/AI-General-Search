package code.datastructure;

import java.util.LinkedList;
import java.util.Queue;

public class GQueue<T> implements GenericQueue<T>{
    private final Queue<T> _queue;
    public GQueue(){
        this._queue = new LinkedList<>();
    }

    @Override
    public T removeFront() {
        return this._queue.remove();
    }

    @Override
    public void add(T o) {
        this._queue.add(o);
    }

    @Override
    public boolean isEmpty() {
        return this._queue.isEmpty();
    }
}
