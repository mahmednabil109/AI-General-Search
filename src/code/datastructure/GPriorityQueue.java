package code.datastructure;

import java.util.PriorityQueue;

public class GPriorityQueue<T> implements GenericQueue<T>{
    private final PriorityQueue<T> _pq;

    public GPriorityQueue(){
        this._pq = new PriorityQueue<>();
    }

    @Override
    public T removeFront() {
        return this._pq.remove();
    }

    @Override
    public void add(T t) {
        this._pq.add(t);
    }

    @Override
    public boolean isEmpty() {
        return this._pq.isEmpty();
    }
}
